public class Main {
    public static void main(String[] args) {
        LaunchSite site = new LaunchSite("Training field", "Kazakhstan");
        DroneMission mission = new DroneMission("Safe flight", MissionType.LOCAL,
                3, site, 5000, 40, 20, true, true, false);
        System.out.println(mission);
    }
}
