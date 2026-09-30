public interface ReportWriter<F extends InspectionFamily> {
    String write(InspectionData.Observation<F> observation);
}
