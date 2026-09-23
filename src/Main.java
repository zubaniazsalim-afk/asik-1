public class Main {
    public static void main(String[] args) {
        LaunchSite site = new LaunchSite("Training field", "Kazakhstan");
        DroneMission mission = new DroneMission.Builder("Safe flight", MissionType.LOCAL, 3, site)
                .withBattery(5000)
                .flyAtAltitude(40)
                .cruiseAtSpeed(20)
                .enableGps()
                .enableReturnHome()
                .build();
        System.out.println(mission);
    }
}
