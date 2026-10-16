public class TelegramMessageSender implements MessageSender {
    private boolean available = true;

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public String getChannelName() { return "Telegram"; }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!available || recipient == null || recipient.isBlank()
                || title == null || body == null || body.isBlank()) {
            System.out.println("Telegram delivery failed: unavailable provider or invalid message");
            return false;
        }
        System.out.println("[Telegram] chat=" + recipient + "\n<b>" + title + "</b>\n" + body);
        return true;
    }
}
