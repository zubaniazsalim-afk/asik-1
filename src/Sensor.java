public interface Sensor<F extends InspectionFamily> {
    double convert(double rawSample);
    boolean requiresAction(double value);
    String unit();
}
