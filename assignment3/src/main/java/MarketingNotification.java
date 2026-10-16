public class MarketingNotification extends Notification {
    public MarketingNotification(MessageSender sender) {
        super(sender);
    }

    @Override
    public void send(String recipient, String message) {
        String body = "[PROMO] " + message + "\nReply STOP to unsubscribe.";
        boolean success = sender.sendMessage(recipient, "Special offer", body);
        System.out.println(success ? "Marketing delivery successful" : "Marketing delivery failed");
    }
}
