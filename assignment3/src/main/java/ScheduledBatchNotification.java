import java.util.ArrayList;
import java.util.List;

public class ScheduledBatchNotification extends Notification {
    private final List<String> recipients = new ArrayList<>();
    private int successful;
    private int failed;

    public ScheduledBatchNotification(MessageSender sender) {
        super(sender);
    }

    public void addRecipient(String recipient) {
        recipients.add(recipient);
    }

    @Override
    public void send(String recipient, String message) {
        // Single-recipient operation required by the base abstraction.
        boolean success = sender.sendMessage(recipient, "Scheduled notification", message);
        System.out.println(success ? "Delivery successful" : "Delivery failed");
    }

    // Call this method when the scheduled time arrives (simulated by Main).
    public void sendBatch(String message) {
        successful = 0;
        failed = 0;
        for (String recipient : recipients) {
            if (sender.sendMessage(recipient, "Scheduled notification", message)) {
                successful++;
            } else {
                failed++;
            }
        }
        System.out.println("Total: " + recipients.size());
        System.out.println("Successful: " + successful);
        System.out.println("Failed: " + failed);
    }

    public int getSuccessful() { return successful; }
    public int getFailed() { return failed; }
}
