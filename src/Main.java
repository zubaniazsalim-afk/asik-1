public class Main {
    public static void main(String[] args) {
        InspectionService<FarmFactory.Family> service = new InspectionService<>(new FarmFactory());
        System.out.println(service.survey(10, 160, 180, 200));
        System.out.println(service.scheduleFollowUp(10, 180));
        System.out.println(service.compareSurveys(10, 180, 400));
    }
}
