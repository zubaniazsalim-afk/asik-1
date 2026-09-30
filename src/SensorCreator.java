public abstract class SensorCreator<F extends InspectionFamily> {
    // Factory Method: subclasses choose the concrete sensor.
    public abstract Sensor<F> createSensor();

    // Shared business logic averages calibrated samples to reduce measurement noise.
    public final InspectionData.Observation<F> inspect(InspectionData.Plan<F> plan,
                                                       double... samples) {
        if (plan == null) {
            throw new IllegalArgumentException("An inspection plan is required");
        }
        if (samples == null || samples.length == 0) {
            throw new IllegalArgumentException("At least one sample is required");
        }
        Sensor<F> sensor = createSensor();
        double total = 0;
        for (double sample : samples) {
            if (!Double.isFinite(sample) || sample < 0 || sample > 1000) {
                throw new IllegalArgumentException("Samples must be finite numbers from 0 to 1000");
            }
            total += sensor.convert(sample);
        }
        double average = total / samples.length;
        return new InspectionData.Observation<>(plan, average, sensor.unit(),
                sensor.requiresAction(average));
    }
}
