import java.util.Objects;

public abstract class Notification {
    // Bridge: every notification uses the same provider interface.
    protected MessageSender sender;

    protected Notification(MessageSender sender) {
        setSender(sender);
    }

    public void setSender(MessageSender sender) {
        this.sender = Objects.requireNonNull(sender, "Sender is required");
    }

    public abstract void send(String recipient, String message);
}
