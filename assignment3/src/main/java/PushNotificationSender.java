public class PushNotificationSender implements MessageSender {
    private boolean available = true;

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public String getChannelName() { return "Push"; }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!available || recipient == null || recipient.isBlank()
                || title == null || body == null || body.isBlank()) {
            System.out.println("Push delivery failed: unavailable provider or invalid message");
            return false;
        }
        System.out.println("[Push] device=" + recipient + " alert=" + title + " | " + body);
        return true;
    }
}
