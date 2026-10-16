public class Main {
    public static void main(String[] args) {
        TelegramMessageSender telegram = new TelegramMessageSender();
        EmailMessageSender email = new EmailMessageSender();
        SmsMessageSender sms = new SmsMessageSender();

        System.out.println("\n1. Urgent + Telegram");
        UrgentNotification urgent = new UrgentNotification(telegram, sms);
        urgent.send("user-1", "Server needs attention");

        System.out.println("\n2. Encrypted + Email");
        new EncryptedNotification(email).send("student@example.com", "Your code is 1234");

        System.out.println("\n3. Scheduled batch + SMS");
        ScheduledBatchNotification batch = new ScheduledBatchNotification(sms);
        batch.addRecipient("+77010000001");
        batch.addRecipient("+77010000002");
        batch.addRecipient(""); // Simulated failed delivery, included in summary.
        batch.sendBatch("Reminder: class starts at 10:00");

        System.out.println("\n4. Same urgent object: Telegram -> Email");
        urgent.setSender(email);
        urgent.send("student@example.com", "Server needs attention");

        System.out.println("\n5. Telegram unavailable -> SMS fallback");
        telegram.setAvailable(false);
        urgent.setSender(telegram);
        urgent.send("+77010000001", "Please check the server");

        System.out.println("\n6. Marketing + Push (independent extensions)");
        new MarketingNotification(new PushNotificationSender())
                .send("device-42", "Save 20% today");

        System.out.println("\n7. Kafka event");
        new UrgentNotification(new KafkaEventSender()).send("customer-7", "Order delayed");
    }
}
