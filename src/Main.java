// Initial version: the client directly creates concrete products.
public class Main {
    public static void main(String[] args) {
        FarmPlanner planner = new FarmPlanner();
        FarmSensor sensor = new FarmSensor();
        FarmReportWriter writer = new FarmReportWriter();
        String route = planner.plan(10);
        double moisture = sensor.measure(180);
        System.out.println(writer.write(route, moisture));
    }

    static class FarmPlanner {
        String plan(double hectares) {
            return "Parallel crop rows over " + hectares + " hectares";
        }
    }

    static class FarmSensor {
        double measure(double rawSample) {
            return rawSample / 10;
        }
    }

    static class FarmReportWriter {
        String write(String route, double moisture) {
            return route + ": moisture " + moisture + "% - "
                    + (moisture < 30 ? "Irrigate crops" : "No irrigation needed");
        }
    }
}
