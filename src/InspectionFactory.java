// Abstract Factory adds the other two products to the sensor Creator.
public abstract class InspectionFactory<F extends InspectionFamily> extends SensorCreator<F> {
    public abstract RoutePlanner<F> createPlanner();
    public abstract ReportWriter<F> createReportWriter();
    // createSensor() is inherited from SensorCreator.
}
