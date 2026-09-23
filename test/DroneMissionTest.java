// Тесты без библиотек. Если условие неверно, программа завершится с ошибкой.
public class DroneMissionTest {
    private static int passed = 0;
    private static final LaunchSite SITE = new LaunchSite("Test field", "Kazakhstan");

    public static void main(String[] args) {
        testBasicPreset();
        testSafePreset();
        testPerformancePreset();
        testRequiredValues();
        testInvalidRanges();
        testLowerBoundaries();
        testUpperBoundaries();
        testIndividualConstraint();
        testReturnHomeDependency();
        testBuilderIndependence();
        testLaunchSite();
        System.out.println("All " + passed + " tests passed.");
    }

    private static DroneMission.Builder localBuilder() {
        return new DroneMission.Builder("Test flight", MissionType.LOCAL, 1, SITE);
    }

    private static DroneMission.Builder longRangeBuilder() {
        return new DroneMission.Builder("Long flight", MissionType.LONG_RANGE, 20, SITE);
    }

    private static void testBasicPreset() {
        DroneMission m = MissionPresets.basic(SITE);
        check(m.getName().equals("Basic flight") && m.getType() == MissionType.LOCAL
                && m.getDistanceKm() == 1 && m.getLaunchSite() == SITE
                && m.getBatteryMah() == 3000 && m.getAltitudeMeters() == 50
                && m.getSpeedKmh() == 25 && !m.isGpsEnabled()
                && !m.isReturnHomeEnabled() && !m.isCameraEnabled(), "BASIC and defaults");
    }

    private static void testSafePreset() {
        DroneMission m = MissionPresets.safe(SITE);
        check(m.getType() == MissionType.LOCAL && m.getDistanceKm() == 3
                && m.getBatteryMah() == 5000 && m.getAltitudeMeters() == 40
                && m.getSpeedKmh() == 20 && m.isGpsEnabled()
                && m.isReturnHomeEnabled() && !m.isCameraEnabled(), "SAFE preset");
    }

    private static void testPerformancePreset() {
        DroneMission m = MissionPresets.performance(SITE);
        check(m.getType() == MissionType.LONG_RANGE && m.getDistanceKm() == 20
                && m.getBatteryMah() == 8000 && m.getAltitudeMeters() == 100
                && m.getSpeedKmh() == 60 && m.isGpsEnabled()
                && m.isReturnHomeEnabled() && m.isCameraEnabled(), "PERFORMANCE preset");
    }

    private static void testRequiredValues() {
        expectInvalid(new DroneMission.Builder("  ", MissionType.LOCAL, 1, SITE),
                "Mission name must not be blank", "blank name");
        expectInvalid(new DroneMission.Builder(null, MissionType.LOCAL, 1, SITE),
                "Mission name must not be blank", "null name");
        expectInvalid(new DroneMission.Builder("Test", null, 1, SITE),
                "Mission type is required", "null type");
        expectInvalid(new DroneMission.Builder("Test", MissionType.LOCAL, 1, null),
                "Launch site is required", "null launch site");
    }

    private static void testInvalidRanges() {
        String distanceError = "Distance must be greater than 0 and at most 50 km";
        for (double distance : new double[] {0, -1, 50.01, Double.NaN, Double.POSITIVE_INFINITY}) {
            expectInvalid(new DroneMission.Builder("Test", MissionType.LOCAL, distance, SITE),
                    distanceError, "invalid distance " + distance);
        }
        expectInvalid(localBuilder().withBattery(999),
                "Battery must be between 1000 and 20000 mAh", "battery below minimum");
        expectInvalid(localBuilder().withBattery(20001),
                "Battery must be between 1000 and 20000 mAh", "battery above maximum");
        expectInvalid(localBuilder().flyAtAltitude(0),
                "Altitude must be between 1 and 120 meters", "altitude below minimum");
        expectInvalid(localBuilder().flyAtAltitude(121),
                "Altitude must be between 1 and 120 meters", "altitude above maximum");
        expectInvalid(localBuilder().cruiseAtSpeed(0),
                "Speed must be between 1 and 60 km/h", "speed below minimum");
        expectInvalid(localBuilder().cruiseAtSpeed(61),
                "Speed must be between 1 and 60 km/h", "speed above maximum");
    }

    private static void testLowerBoundaries() {
        DroneMission m = new DroneMission.Builder("Minimum", MissionType.LOCAL, 0.01, SITE)
                .withBattery(1000).flyAtAltitude(1).cruiseAtSpeed(1).build();
        check(m.getDistanceKm() == 0.01 && m.getBatteryMah() == 1000
                && m.getAltitudeMeters() == 1 && m.getSpeedKmh() == 1, "lower boundaries");
    }

    private static void testUpperBoundaries() {
        DroneMission m = new DroneMission.Builder("Maximum", MissionType.LOCAL, 50, SITE)
                .withBattery(20000).flyAtAltitude(120).cruiseAtSpeed(60).build();
        check(m.getDistanceKm() == 50 && m.getBatteryMah() == 20000
                && m.getAltitudeMeters() == 120 && m.getSpeedKmh() == 60, "upper boundaries");
    }

    private static void testIndividualConstraint() {
        String error = "LONG_RANGE requires GPS and battery of at least 5000 mAh";
        expectInvalid(longRangeBuilder().withBattery(5000), error, "long range without GPS");
        expectInvalid(longRangeBuilder().enableGps().withBattery(4999), error,
                "long range below battery threshold");
        DroneMission m = longRangeBuilder().enableGps().withBattery(5000).build();
        check(m.isGpsEnabled() && m.getBatteryMah() == 5000,
                "long range at exact battery threshold");
    }

    private static void testReturnHomeDependency() {
        expectInvalid(localBuilder().enableReturnHome(),
                "Return home requires GPS", "return home without GPS");
        DroneMission m = localBuilder().enableReturnHome().enableGps().build();
        check(m.isReturnHomeEnabled() && m.isGpsEnabled(), "dependencies checked after all steps");
    }

    private static void testBuilderIndependence() {
        DroneMission.Builder builder = localBuilder();
        DroneMission first = builder.build();
        DroneMission second = builder.withBattery(6000).enableCamera().build();
        check(first != second && first.getBatteryMah() == 3000 && !first.isCameraEnabled()
                && second.getBatteryMah() == 6000 && second.isCameraEnabled(),
                "builder reuse preserves first product");
        builder.withBattery(999);
        expectInvalid(builder, "Battery must be between 1000 and 20000 mAh", "failed reuse");
        check(first.getBatteryMah() == 3000 && second.getBatteryMah() == 6000,
                "failed build preserves existing products");
    }

    private static void testLaunchSite() {
        check(SITE.getName().equals("Test field") && SITE.getCountry().equals("Kazakhstan"),
                "launch site values");
        try {
            new LaunchSite("", "Kazakhstan");
            throw new AssertionError("Empty launch site was accepted");
        } catch (IllegalArgumentException e) {
            check(e.getMessage().equals("Launch site name must not be blank"), "empty launch site");
        }
        try {
            new LaunchSite("Test field", null);
            throw new AssertionError("Null country was accepted");
        } catch (IllegalArgumentException e) {
            check(e.getMessage().equals("Country must not be blank"), "null country");
        }
    }

    private static void expectInvalid(DroneMission.Builder builder, String message, String testName) {
        try {
            builder.build();
            throw new AssertionError("FAIL: " + testName + " accepted invalid configuration");
        } catch (IllegalArgumentException e) {
            check(message.equals(e.getMessage()), testName);
        }
    }

    private static void check(boolean condition, String testName) {
        if (!condition) {
            throw new AssertionError("FAIL: " + testName);
        }
        passed++;
        System.out.println("PASS: " + testName);
    }
}
