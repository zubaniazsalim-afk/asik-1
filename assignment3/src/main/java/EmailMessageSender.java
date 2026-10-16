public class EmailMessageSender implements MessageSender {
    private boolean available = true;

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public String getChannelName() { return "Email"; }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!available || recipient == null || recipient.isBlank()
                || title == null || body == null || body.isBlank()) {
            System.out.println("Email delivery failed: unavailable provider or invalid message");
            return false;
        }
        System.out.println("[Email] To: " + recipient + "\nSubject: " + title + "\n" + body + "\n-- Notification Gateway");
        return true;
    }
}
