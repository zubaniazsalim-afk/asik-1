// Director stores preset recipes and returns completed missions.
public final class MissionPresets {
    private MissionPresets() { }

    public static DroneMission basic(LaunchSite site) {
        return new DroneMission.Builder("Basic flight", MissionType.LOCAL, 1, site)
                .build();
    }

    public static DroneMission safe(LaunchSite site) {
        return withNavigation("Safe flight", MissionType.LOCAL, 3, site)
                .withBattery(5000)
                .flyAtAltitude(40)
                .cruiseAtSpeed(20)
                .build();
    }

    public static DroneMission performance(LaunchSite site) {
        return withNavigation("Performance flight", MissionType.LONG_RANGE, 20, site)
                .withBattery(8000)
                .flyAtAltitude(100)
                .cruiseAtSpeed(60)
                .enableCamera()
                .build();
    }

    // Shared SAFE and PERFORMANCE steps are defined once.
    private static DroneMission.Builder withNavigation(String name, MissionType type,
                                                       double distanceKm, LaunchSite site) {
        return new DroneMission.Builder(name, type, distanceKm, site)
                .enableGps()
                .enableReturnHome();
    }
}
