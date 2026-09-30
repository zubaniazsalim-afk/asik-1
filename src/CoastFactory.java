import java.util.Locale;

// Nested classes keep one complete family together in a single file.
public final class CoastFactory extends InspectionFactory<CoastFactory.Family> {
    public static final class Family implements InspectionFamily {
        private Family() { }
    }

    @Override
    public RoutePlanner<Family> createPlanner() { return new CoastPlanner(); }

    @Override
    public ReportWriter<Family> createReportWriter() { return new CoastReportWriter(); }

    @Override
    public Sensor<Family> createSensor() { return new CoastSensor(); }

    public static final class CoastPlanner implements RoutePlanner<Family> {
        @Override
        public InspectionData.Plan<Family> plan(double hectares) {
            return new InspectionData.Plan<>(hectares, (int) Math.ceil(hectares * 5),
                    "Shoreline transects");
        }
    }

    public static final class CoastSensor implements Sensor<Family> {
        @Override
        public double convert(double rawSample) { return rawSample / 100; }

        @Override
        public boolean requiresAction(double value) { return value >= 2; }

        @Override
        public String unit() { return "mg/L oil"; }
    }

    public static final class CoastReportWriter implements ReportWriter<Family> {
        @Override
        public String write(InspectionData.Observation<Family> observation) {
            String action = observation.alert() ? "Deploy containment booms" : "No oil response needed";
            return String.format(Locale.ROOT, "COAST | %s | %.2f %s | %s",
                    observation.plan().route(), observation.value(), observation.unit(), action);
        }
    }
}
