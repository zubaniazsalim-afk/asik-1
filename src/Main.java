public class Main {
    public static void main(String[] args) {
        SensorCreator<FarmFactory.Family> creator = new FarmFactory();
        RoutePlanner<FarmFactory.Family> planner = new FarmFactory.FarmPlanner();
        ReportWriter<FarmFactory.Family> writer = new FarmFactory.FarmReportWriter();
        InspectionData.Plan<FarmFactory.Family> plan = planner.plan(10);
        System.out.println(writer.write(creator.inspect(plan, 160, 180, 200)));
    }
}
