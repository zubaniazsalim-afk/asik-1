// Business Client: no concrete family names and no direct concrete product creation.
public final class InspectionService<F extends InspectionFamily> {
    private final InspectionFactory<F> factory;
    private final RoutePlanner<F> planner;
    private final ReportWriter<F> writer;

    // One factory supplies the whole family. Individual products cannot be injected separately.
    public InspectionService(InspectionFactory<F> factory) {
        if (factory == null) {
            throw new IllegalArgumentException("An inspection factory is required");
        }
        this.factory = factory;
        this.planner = factory.createPlanner();
        this.writer = factory.createReportWriter();
    }

    public String survey(double hectares, double... samples) {
        InspectionData.Observation<F> observation = inspect(hectares, samples);
        return "SURVEY | " + writer.write(observation);
    }

    public String scheduleFollowUp(double hectares, double... samples) {
        InspectionData.Observation<F> observation = inspect(hectares, samples);
        String report = writer.write(observation);
        if (!observation.alert()) {
            return "FOLLOW-UP | Not needed | " + report;
        }
        // Planning policy: reserve a revisit covering one quarter of the area.
        InspectionData.Plan<F> followUp = planner.plan(hectares / 4);
        return "FOLLOW-UP | Revisit " + followUp.hectares() + " ha for "
                + followUp.minutes() + " min | " + report;
    }

    public String compareSurveys(double hectares, double beforeSample, double afterSample) {
        InspectionData.Observation<F> before = inspect(hectares, beforeSample);
        InspectionData.Observation<F> after = inspect(hectares, afterSample);
        String status;
        if (before.alert() && !after.alert()) {
            status = "RESOLVED";
        } else if (!before.alert() && after.alert()) {
            status = "NEW_ALERT";
        } else if (after.alert()) {
            status = "STILL_ALERT";
        } else {
            status = "CLEAR";
        }
        return "COMPARISON | " + status + "\nBefore: " + writer.write(before)
                + "\nAfter: " + writer.write(after);
    }

    private InspectionData.Observation<F> inspect(double hectares, double... samples) {
        return factory.inspect(planner.plan(hectares), samples);
    }
}
