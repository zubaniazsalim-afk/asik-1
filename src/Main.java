public class Main {
    public static void main(String[] args) {
        try {
            if (args.length > 4) {
                throw new IllegalArgumentException(
                        "Usage: Main <family> [hectares] [sample] [followUpSample]");
            }
            String family = args.length > 0 ? args[0] : "farm";
            double hectares = args.length > 1 ? Double.parseDouble(args[1]) : 10;
            double sample = args.length > 2 ? Double.parseDouble(args[2]) : 180;
            double followUpSample = args.length > 3 ? Double.parseDouble(args[3]) : 400;
            run(FactorySelector.select(family), hectares, sample, followUpSample);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    // The type parameter preserves one family after runtime selection.
    private static <F extends InspectionFamily> void run(InspectionFactory<F> factory,
                                                        double hectares, double sample,
                                                        double followUpSample) {
        InspectionService<F> service = new InspectionService<>(factory);
        System.out.println(service.survey(hectares, sample));
        System.out.println(service.scheduleFollowUp(hectares, sample));
        System.out.println(service.compareSurveys(hectares, sample, followUpSample));
    }
}
