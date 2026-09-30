import java.util.Locale;

// Nested classes keep one complete family together in a single file.
public final class FarmFactory extends InspectionFactory<FarmFactory.Family> {
    public static final class Family implements InspectionFamily {
        private Family() { }
    }

    @Override
    public RoutePlanner<Family> createPlanner() { return new FarmPlanner(); }

    @Override
    public ReportWriter<Family> createReportWriter() { return new FarmReportWriter(); }

    @Override
    public Sensor<Family> createSensor() { return new FarmSensor(); }

    public static final class FarmPlanner implements RoutePlanner<Family> {
        @Override
        public InspectionData.Plan<Family> plan(double hectares) {
            return new InspectionData.Plan<>(hectares, (int) Math.ceil(hectares * 3),
                    "Parallel crop rows");
        }
    }

    public static final class FarmSensor implements Sensor<Family> {
        @Override
        public double convert(double rawSample) { return rawSample / 10; }

        @Override
        public boolean requiresAction(double value) { return value < 30; }

        @Override
        public String unit() { return "% moisture"; }
    }

    public static final class FarmReportWriter implements ReportWriter<Family> {
        @Override
        public String write(InspectionData.Observation<Family> observation) {
            String action = observation.alert() ? "Irrigate crops" : "No irrigation needed";
            return String.format(Locale.ROOT, "FARM | %s | %.2f %s | %s",
                    observation.plan().route(), observation.value(), observation.unit(), action);
        }
    }
}
