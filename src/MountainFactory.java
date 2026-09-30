import java.util.Locale;

// Nested classes keep one complete family together in a single file.
public final class MountainFactory extends InspectionFactory<MountainFactory.Family> {
    public static final class Family implements InspectionFamily {
        private Family() { }
    }

    @Override
    public RoutePlanner<Family> createPlanner() { return new MountainPlanner(); }

    @Override
    public ReportWriter<Family> createReportWriter() { return new MountainReportWriter(); }

    @Override
    public Sensor<Family> createSensor() { return new MountainSensor(); }

    public static final class MountainPlanner implements RoutePlanner<Family> {
        @Override
        public InspectionData.Plan<Family> plan(double hectares) {
            return new InspectionData.Plan<>(hectares, (int) Math.ceil(hectares * 6),
                    "Contour route around steep slopes");
        }
    }

    public static final class MountainSensor implements Sensor<Family> {
        @Override
        public double convert(double rawSample) { return rawSample / 10; }

        @Override
        public boolean requiresAction(double value) { return value >= 50; }

        @Override
        public String unit() { return "cm snow"; }
    }

    public static final class MountainReportWriter implements ReportWriter<Family> {
        @Override
        public String write(InspectionData.Observation<Family> observation) {
            String action = observation.alert() ? "Close trail for snow clearance" : "Keep trail open";
            return String.format(Locale.ROOT, "MOUNTAIN | %s | %.2f %s | %s",
                    observation.plan().route(), observation.value(), observation.unit(), action);
        }
    }
}
