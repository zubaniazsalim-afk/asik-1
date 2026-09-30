public final class InspectionData {
    private InspectionData() { }

    // F keeps the family information in the type, without a runtime string tag.
    public record Plan<F extends InspectionFamily>(double hectares, int minutes, String route) {
        public Plan {
            if (!Double.isFinite(hectares) || hectares <= 0 || hectares > 100) {
                throw new IllegalArgumentException("Area must be greater than 0 and at most 100 hectares");
            }
            if (minutes <= 0 || route == null || route.isBlank()) {
                throw new IllegalArgumentException("A plan needs a positive duration and a route");
            }
        }
    }

    public record Observation<F extends InspectionFamily>(Plan<F> plan, double value,
                                                          String unit, boolean alert) {
        public Observation {
            if (plan == null || !Double.isFinite(value) || unit == null || unit.isBlank()) {
                throw new IllegalArgumentException("An observation needs a plan, finite value and unit");
            }
        }
    }
}
