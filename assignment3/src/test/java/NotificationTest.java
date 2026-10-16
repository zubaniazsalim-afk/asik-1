import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {
    // A small test sender records what a notification passed through the Bridge.
    static class RecordingSender implements MessageSender {
        boolean available = true;
        boolean result = true;
        int calls;
        String body;
        String recipient;
        public boolean isAvailable() { return available; }
        public String getChannelName() { return "Test"; }
        public boolean sendMessage(String recipient, String title, String body) {
            calls++;
            this.recipient = recipient;
            this.body = body;
            return result;
        }
    }

    @Test void telegramDeliversFormattedMessage() {
        String output = capture(() -> assertTrue(
                new TelegramMessageSender().sendMessage("user", "Title", "Hello")));
        assertTrue(output.contains("<b>Title</b>"));
        assertTrue(output.contains("Hello"));
    }

    @Test void unavailableProviderReturnsFalse() {
        TelegramMessageSender sender = new TelegramMessageSender();
        sender.setAvailable(false);
        assertFalse(sender.isAvailable());
        assertFalse(sender.sendMessage("user", "Title", "Hello"));
    }

    @Test void urgentAddsPrefix() {
        RecordingSender sender = new RecordingSender();
        new UrgentNotification(sender).send("user", "Help");
        assertEquals("[URGENT] Help", sender.body);
        assertEquals("user", sender.recipient);
    }

    @Test void unavailablePrimaryUsesFallback() {
        RecordingSender primary = new RecordingSender();
        primary.available = false;
        RecordingSender fallback = new RecordingSender();
        new UrgentNotification(primary, fallback).send("user", "Help");
        assertEquals(0, primary.calls);
        assertEquals(1, fallback.calls);
        assertEquals("[URGENT] Help", fallback.body);
    }

    @Test void successfulPrimaryDoesNotUseFallback() {
        RecordingSender primary = new RecordingSender();
        RecordingSender fallback = new RecordingSender();
        new UrgentNotification(primary, fallback).send("user", "Help");
        assertEquals(1, primary.calls);
        assertEquals(0, fallback.calls);
    }

    @Test void rejectedPrimaryAlsoUsesFallback() {
        RecordingSender primary = new RecordingSender();
        primary.result = false;
        RecordingSender fallback = new RecordingSender();
        new UrgentNotification(primary, fallback).send("user", "Help");
        assertEquals(1, primary.calls);
        assertEquals(1, fallback.calls);
    }

    @Test void bothProvidersUnavailableReportFailure() {
        RecordingSender primary = new RecordingSender();
        RecordingSender fallback = new RecordingSender();
        primary.available = false;
        fallback.available = false;
        String output = capture(() -> new UrgentNotification(primary, fallback).send("u", "Help"));
        assertTrue(output.contains("Urgent delivery failed"));
        assertEquals(0, primary.calls + fallback.calls);
    }

    @Test void smsAcceptsExactly160Characters() {
        assertTrue(new SmsMessageSender().sendMessage("+7701", "Title", "a".repeat(160)));
    }

    @Test void smsRejects161Characters() {
        assertFalse(new SmsMessageSender().sendMessage("+7701", "Title", "a".repeat(161)));
    }

    @Test void encodedMessageCanBeDecodedAsUtf8() {
        RecordingSender sender = new RecordingSender();
        String original = "Hello \uD83C\uDF0D!";
        new EncryptedNotification(sender).send("user", original);
        assertNotEquals(original, sender.body);
        String decoded = new String(Base64.getDecoder().decode(sender.body), StandardCharsets.UTF_8);
        assertEquals(original, decoded);
    }

    @Test void batchCountsSuccessAndFailureAndPrintsSummary() {
        ScheduledBatchNotification batch = new ScheduledBatchNotification(new SmsMessageSender());
        batch.addRecipient("+7701");
        batch.addRecipient("+7702");
        batch.addRecipient("");
        String output = capture(() -> batch.sendBatch("Hello"));
        assertEquals(2, batch.getSuccessful());
        assertEquals(1, batch.getFailed());
        assertTrue(output.contains("Total: 3"));
        assertTrue(output.contains("Successful: 2"));
        assertTrue(output.contains("Failed: 1"));
    }

    @Test void repeatedBatchResetsCounters() {
        ScheduledBatchNotification batch = new ScheduledBatchNotification(new SmsMessageSender());
        batch.addRecipient("+7701");
        batch.sendBatch("Hello");
        batch.sendBatch("x".repeat(161));
        assertEquals(0, batch.getSuccessful());
        assertEquals(1, batch.getFailed());
    }

    @Test void emptyBatchHasZeroCounts() {
        ScheduledBatchNotification batch = new ScheduledBatchNotification(new SmsMessageSender());
        batch.sendBatch("Hello");
        assertEquals(0, batch.getSuccessful());
        assertEquals(0, batch.getFailed());
    }

    @Test void sameNotificationSwitchesSenderAtRuntime() {
        RecordingSender first = new RecordingSender();
        RecordingSender second = new RecordingSender();
        Notification notification = new UrgentNotification(first);
        notification.send("user", "First");
        notification.setSender(second);
        notification.send("user", "Second");
        assertEquals(1, first.calls);
        assertEquals(1, second.calls);
        assertEquals("[URGENT] Second", second.body);
    }

    @Test void pushWorksWithExistingUrgentNotification() {
        String output = capture(() -> new UrgentNotification(new PushNotificationSender()).send("device", "Help"));
        assertTrue(output.contains("[Push]"));
        assertTrue(output.contains("[URGENT] Help"));
    }

    @Test void marketingWorksWithExistingEmailProvider() {
        String output = capture(() -> new MarketingNotification(new EmailMessageSender()).send("user", "Sale"));
        assertTrue(output.contains("Subject: Special offer"));
        assertTrue(output.contains("[PROMO] Sale"));
        assertTrue(output.contains("STOP"));
        assertTrue(output.contains("-- Notification Gateway"));
    }

    @Test void kafkaPublishesEvent() {
        String output = capture(() -> assertTrue(new KafkaEventSender().sendMessage("key-1", "Title", "Body")));
        assertTrue(output.contains("topic=notifications key=key-1"));
        assertTrue(output.contains("event={title=Title, body=Body}"));
    }

    @Test void invalidRecipientFails() {
        assertFalse(new EmailMessageSender().sendMessage(" ", "Title", "Body"));
    }

    @Test void missingSenderIsRejected() {
        assertThrows(NullPointerException.class, () -> new UrgentNotification(null));
        Notification notification = new EncryptedNotification(new EmailMessageSender());
        assertThrows(NullPointerException.class, () -> notification.setSender(null));
    }

    private String capture(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream captured = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(captured);
            action.run();
        } finally {
            System.setOut(original);
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }
}
