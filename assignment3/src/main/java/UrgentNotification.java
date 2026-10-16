import java.util.Objects;

public class UrgentNotification extends Notification {
    private MessageSender fallbackSender;

    public UrgentNotification(MessageSender sender) {
        super(sender);
    }

    public UrgentNotification(MessageSender sender, MessageSender fallbackSender) {
        super(sender);
        setFallbackSender(fallbackSender);
    }

    public void setFallbackSender(MessageSender fallbackSender) {
        this.fallbackSender = Objects.requireNonNull(fallbackSender);
    }

    @Override
    public void send(String recipient, String message) {
        String body = "[URGENT] " + message;
        boolean success = false;
        if (sender.isAvailable()) {
            success = sender.sendMessage(recipient, "Urgent notification", body);
        }
        // Also try the fallback if the primary rejects the message.
        if (!success && fallbackSender != null && fallbackSender.isAvailable()) {
            System.out.println("Trying fallback: " + fallbackSender.getChannelName());
            success = fallbackSender.sendMessage(recipient, "Urgent notification", body);
        }
        System.out.println(success ? "Urgent delivery successful" : "Urgent delivery failed");
    }
}
