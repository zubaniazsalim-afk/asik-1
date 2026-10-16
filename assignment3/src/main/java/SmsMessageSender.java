public class SmsMessageSender implements MessageSender {
    public static final int MAX_LENGTH = 160;
    private boolean available = true;

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public String getChannelName() { return "SMS"; }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!available || recipient == null || recipient.isBlank()
                || title == null || body == null || body.isBlank()) {
            System.out.println("SMS delivery failed: unavailable provider or invalid message");
            return false;
        }
        // SMS sends only the body; the subject is not part of an SMS.
        if (body.codePointCount(0, body.length()) > MAX_LENGTH) {
            System.out.println("SMS rejected: maximum " + MAX_LENGTH + " characters");
            return false;
        }
        System.out.println("[SMS] phone=" + recipient + " text=" + body);
        return true;
    }
}
