# Assignment 3 Defense Guide

## A short explanation

My project is a simulated notification gateway. It has two independent dimensions: the notification rules and the delivery channel. `Notification` is the abstraction. `MessageSender` is the implementor interface. The sender field inside `Notification` is the Bridge. For example, an urgent notification adds an urgent prefix, while Telegram decides how to display the message. I can replace Telegram with Email using `setSender` on the same object.

Without Bridge, I might create `UrgentTelegramNotification`, `UrgentEmailNotification` and more classes for every combination. With Bridge, each rule and each provider has its own class. Adding Push does not change the notification classes. Adding Marketing does not change the providers.

## Explain the main files

1. Open `MessageSender.java`. Explain the three methods: deliver a message, check availability, and report the channel name. Delivery returns true or false.
2. Open `Notification.java`. Explain that `protected` allows subclasses to use the sender. The constructor receives it; `setSender` changes it later.
3. Open `UrgentNotification.java`. Follow the prefix, availability check, primary result, fallback and final result. Both providers have the same interface, so no provider-specific checks are necessary.
4. Open `SmsMessageSender.java`. Point to `MAX_LENGTH`, validation and the rejection result. Explain that the title is ignored for SMS.
5. Open `ScheduledBatchNotification.java`. Explain the list, loop, success counter, failure counter and reset before each batch.
6. Open `Main.java`. Run `./run.ps1` and explain the numbered output.
7. Open `NotificationTest.java`. Run `./test.ps1` and explain a fallback test, an SMS boundary test and the runtime switching test.

## Practice modifications

### Add WhatsAppMessageSender

Create `WhatsAppMessageSender.java` implementing `MessageSender`. Implement its three methods. In `sendMessage`, validate the input, print a simulated WhatsApp delivery and return its result. You can follow `PushNotificationSender` as a small example. Then demonstrate:

```java
Notification notification = new UrgentNotification(new WhatsAppMessageSender());
notification.send("+77010000001", "Please check your account");
```

No existing notification class needs modification.

### Change the SMS limit to 120

Change `MAX_LENGTH` in `SmsMessageSender` from 160 to 120. Update the two SMS boundary tests to accept 120 and reject 121. The repeated batch test still rejects 161. Run the tests again. The boundary tests intentionally contain explicit numbers to document the current requirement.

### Add SilentNotification

Create a subclass that sends without the extra delivery-status output:

```java
public class SilentNotification extends Notification {
    public SilentNotification(MessageSender sender) {
        super(sender);
    }

    @Override
    public void send(String recipient, String message) {
        sender.sendMessage(recipient, "Silent notification", message);
    }
}
```

This makes the notification layer silent. Simulated providers still print their delivery trace. If the requirement means a device must not play a sound, the delivery interface would need a silent flag or delivery options; explain this difference during the defense.

### Make Email unavailable and switch providers

```java
EmailMessageSender email = new EmailMessageSender();
email.setAvailable(false);
Notification notification = new EncryptedNotification(email);
notification.send("user", "Hello"); // Reports failure.
notification.setSender(new PushNotificationSender());
notification.send("device-1", "Hello"); // Uses the same notification object.
```

### Change the fallback

```java
TelegramMessageSender telegram = new TelegramMessageSender();
telegram.setAvailable(false);
UrgentNotification urgent = new UrgentNotification(telegram, new SmsMessageSender());
urgent.setFallbackSender(new EmailMessageSender());
urgent.send("student@example.com", "Please check the server");
```

The current requirement is one fallback. To support an ordered chain of several fallbacks, replace the single fallback field with a `List<MessageSender>`, add an `addFallbackSender` method and loop over available senders until one returns true. Add a test where the first fallback fails and the second succeeds. The provider classes need no changes.

## Questions to practice

**Is Base64 secure encryption?** No. It is reversible encoding used only because the assignment permits a simple message transformation.

**Does ScheduledBatchNotification wait for a time?** No. The demo simulates the trigger. `sendBatch` processes the stored recipients when called.

**What happens if the primary is available but returns false?** The urgent notification tries the fallback. Availability alone cannot guarantee successful delivery.

**Does runtime switching create another notification?** No. Only its sender field changes. The runtime switching test checks that both deliveries came from the same notification variable.

**Why are there several small files?** Each file has one public class and one responsibility. There are no classes representing every notification/provider pair.
