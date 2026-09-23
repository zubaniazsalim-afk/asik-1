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

    // Только Builder может создать готовую миссию.
    private DroneMission(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.distanceKm = builder.distanceKm;
        this.launchSite = builder.launchSite;
        this.batteryMah = builder.batteryMah;
        this.altitudeMeters = builder.altitudeMeters;
        this.speedKmh = builder.speedKmh;
        this.gpsEnabled = builder.gpsEnabled;
        this.returnHomeEnabled = builder.returnHomeEnabled;
        this.cameraEnabled = builder.cameraEnabled;
    }

    public String getName() { return name; }
    public MissionType getType() { return type; }
    public double getDistanceKm() { return distanceKm; }
    public LaunchSite getLaunchSite() { return launchSite; }
    public int getBatteryMah() { return batteryMah; }
    public int getAltitudeMeters() { return altitudeMeters; }
    public int getSpeedKmh() { return speedKmh; }
    public boolean isGpsEnabled() { return gpsEnabled; }
    public boolean isReturnHomeEnabled() { return returnHomeEnabled; }
    public boolean isCameraEnabled() { return cameraEnabled; }

    public static class Builder {
        private final String name;
        private final MissionType type;
        private final double distanceKm;
        private final LaunchSite launchSite;
        private int batteryMah = 3000;
        private int altitudeMeters = 50;
        private int speedKmh = 25;
        private boolean gpsEnabled = false;
        private boolean returnHomeEnabled = false;
        private boolean cameraEnabled = false;

        // Четыре обязательных параметра передаются сразу.
        public Builder(String name, MissionType type, double distanceKm, LaunchSite launchSite) {
            this.name = name;
            this.type = type;
            this.distanceKm = distanceKm;
            this.launchSite = launchSite;
        }

        public Builder withBattery(int batteryMah) {
            this.batteryMah = batteryMah;
            return this;
        }

        public Builder flyAtAltitude(int altitudeMeters) {
            this.altitudeMeters = altitudeMeters;
            return this;
        }

        public Builder cruiseAtSpeed(int speedKmh) {
            this.speedKmh = speedKmh;
            return this;
        }

        public Builder enableGps() {
            this.gpsEnabled = true;
            return this;
        }

        public Builder enableReturnHome() {
            this.returnHomeEnabled = true;
            return this;
        }

        public Builder enableCamera() {
            this.cameraEnabled = true;
            return this;
        }

        public DroneMission build() {
            validateRequiredFields();
            validateRanges();
            validateDependencies();
            return new DroneMission(this);
        }

        private void validateRequiredFields() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Mission name must not be blank");
            }
            if (type == null) {
                throw new IllegalArgumentException("Mission type is required");
            }
            if (launchSite == null) {
                throw new IllegalArgumentException("Launch site is required");
            }
        }

        private void validateRanges() {
            if (!Double.isFinite(distanceKm) || distanceKm <= 0 || distanceKm > 50) {
                throw new IllegalArgumentException("Distance must be greater than 0 and at most 50 km");
            }
            if (batteryMah < 1000 || batteryMah > 20000) {
                throw new IllegalArgumentException("Battery must be between 1000 and 20000 mAh");
            }
            if (altitudeMeters < 1 || altitudeMeters > 120) {
                throw new IllegalArgumentException("Altitude must be between 1 and 120 meters");
            }
            if (speedKmh < 1 || speedKmh > 60) {
                throw new IllegalArgumentException("Speed must be between 1 and 60 km/h");
            }
        }

        private void validateDependencies() {
            if (type == MissionType.LONG_RANGE && (!gpsEnabled || batteryMah < 5000)) {
                throw new IllegalArgumentException("LONG_RANGE requires GPS and battery of at least 5000 mAh");
            }
            if (returnHomeEnabled && !gpsEnabled) {
                throw new IllegalArgumentException("Return home requires GPS");
            }
        }
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
