public interface RoutePlanner<F extends InspectionFamily> {
    InspectionData.Plan<F> plan(double hectares);
}
