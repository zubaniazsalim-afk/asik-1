// Products have interfaces, but this client still creates concrete classes.
public class Main {
    static final class Farm implements InspectionFamily { }

    public static void main(String[] args) {
        RoutePlanner<Farm> planner = new FarmPlanner();
        SensorCreator<Farm> creator = new FarmCreator();
        ReportWriter<Farm> writer = new FarmReportWriter();
        InspectionData.Plan<Farm> plan = planner.plan(10);
        InspectionData.Observation<Farm> observation = creator.inspect(plan, 160, 180, 200);
        System.out.println(writer.write(observation));
    }

    static class FarmCreator extends SensorCreator<Farm> {
        public Sensor<Farm> createSensor() { return new FarmSensor(); }
    }

    static class FarmPlanner implements RoutePlanner<Farm> {
        public InspectionData.Plan<Farm> plan(double hectares) {
            return new InspectionData.Plan<>(hectares, (int) Math.ceil(hectares * 3),
                    "Parallel crop rows");
        }
    }

    static class FarmSensor implements Sensor<Farm> {
        public double convert(double rawSample) { return rawSample / 10; }
        public boolean requiresAction(double value) { return value < 30; }
        public String unit() { return "% moisture"; }
    }

    static class FarmReportWriter implements ReportWriter<Farm> {
        public String write(InspectionData.Observation<Farm> observation) {
            return observation.plan().route() + ": " + observation.value() + " "
                    + observation.unit() + " - "
                    + (observation.alert() ? "Irrigate crops" : "No irrigation needed");
        }
    }
}
