import java.util.Locale;

// Nested classes keep one complete family together in a single file.
public final class ForestFactory extends SensorCreator<ForestFactory.Family> {
    public static final class Family implements InspectionFamily {
        private Family() { }
    }

    @Override
    public Sensor<Family> createSensor() { return new ForestSensor(); }

    public static final class ForestPlanner implements RoutePlanner<Family> {
        @Override
        public InspectionData.Plan<Family> plan(double hectares) {
            return new InspectionData.Plan<>(hectares, (int) Math.ceil(hectares * 4),
                    "Canopy grid with firebreak corridors");
        }
    }

    public static final class ForestSensor implements Sensor<Family> {
        @Override
        public double convert(double rawSample) { return rawSample / 10; }

        @Override
        public boolean requiresAction(double value) { return value >= 60; }

        @Override
        public String unit() { return "C temperature"; }
    }

    public static final class ForestReportWriter implements ReportWriter<Family> {
        @Override
        public String write(InspectionData.Observation<Family> observation) {
            String action = observation.alert() ? "Send fire patrol" : "Continue forest monitoring";
            return String.format(Locale.ROOT, "FOREST | %s | %.2f %s | %s",
                    observation.plan().route(), observation.value(), observation.unit(), action);
        }
    }
}
