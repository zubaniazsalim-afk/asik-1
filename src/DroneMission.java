public final class DroneMission {
    private final String name;
    private final MissionType type;
    private final double distanceKm;
    private final LaunchSite launchSite;
    private final int batteryMah;
    private final int altitudeMeters;
    private final int speedKmh;
    private final boolean gpsEnabled;
    private final boolean returnHomeEnabled;
    private final boolean cameraEnabled;

    public DroneMission(String name, MissionType type, double distanceKm,
                        LaunchSite launchSite, int batteryMah, int altitudeMeters,
                        int speedKmh, boolean gpsEnabled, boolean returnHomeEnabled,
                        boolean cameraEnabled) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Mission name must not be blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Mission type is required");
        }
        if (!Double.isFinite(distanceKm) || distanceKm <= 0 || distanceKm > 50) {
            throw new IllegalArgumentException("Distance must be greater than 0 and at most 50 km");
        }
        this.name = name;
        this.type = type;
        this.distanceKm = distanceKm;
        this.launchSite = launchSite;
        this.batteryMah = batteryMah;
        this.altitudeMeters = altitudeMeters;
        this.speedKmh = speedKmh;
        this.gpsEnabled = gpsEnabled;
        this.returnHomeEnabled = returnHomeEnabled;
        this.cameraEnabled = cameraEnabled;
    }

    @Override
    public String toString() {
        return name + ": " + type + ", distance=" + distanceKm + " km"
                + ", site=" + launchSite + ", battery=" + batteryMah + " mAh"
                + ", altitude=" + altitudeMeters + " m, speed=" + speedKmh + " km/h"
                + ", GPS=" + gpsEnabled + ", returnHome=" + returnHomeEnabled
                + ", camera=" + cameraEnabled;
    }
}
