public class KafkaEventSender implements MessageSender {
    private boolean available = true;

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public String getChannelName() { return "Kafka"; }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!available || recipient == null || recipient.isBlank()
                || title == null || body == null || body.isBlank()) {
            System.out.println("Kafka delivery failed: unavailable provider or invalid message");
            return false;
        }
        System.out.println("[Kafka] topic=notifications key=" + recipient + " event={title=" + title + ", body=" + body + "}");
        return true;
    }
}
