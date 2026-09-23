public class Main {
    public static void main(String[] args) {
        LaunchSite site = new LaunchSite("Training field", "Kazakhstan");
        System.out.println(MissionPresets.basic(site));
        System.out.println(MissionPresets.safe(site) + " \uD83C\uDF4C");
        System.out.println(MissionPresets.performance(site));
    }
}
