import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

// Run directly with Java. Failed checks throw AssertionError and give a nonzero exit code.
public class InspectionTest {
    private static int passed;

    public static void main(String[] args) throws Exception {
        testOriginalProducts();
        testSensorBehavior();
        testRuntimeSelection();
        testBusinessOperations();
        testInvalidInputs();
        testAbstractionBasedClient();
        testCompileTimeCompatibility();
        testMountainExtension();
        System.out.println("All " + passed + " tests passed.");
    }

    private static void testOriginalProducts() {
        FarmFactory farm = new FarmFactory();
        check(farm.createPlanner() instanceof FarmFactory.FarmPlanner, "farm planner creation");
        check(farm.createSensor() instanceof FarmFactory.FarmSensor, "farm sensor creation");
        check(farm.createReportWriter() instanceof FarmFactory.FarmReportWriter, "farm writer creation");
        ForestFactory forest = new ForestFactory();
        check(forest.createPlanner() instanceof ForestFactory.ForestPlanner, "forest planner creation");
        check(forest.createSensor() instanceof ForestFactory.ForestSensor, "forest sensor creation");
        check(forest.createReportWriter() instanceof ForestFactory.ForestReportWriter, "forest writer creation");
        CoastFactory coast = new CoastFactory();
        check(coast.createPlanner() instanceof CoastFactory.CoastPlanner, "coast planner creation");
        check(coast.createSensor() instanceof CoastFactory.CoastSensor, "coast sensor creation");
        check(coast.createReportWriter() instanceof CoastFactory.CoastReportWriter, "coast writer creation");
        check(farm.createPlanner().plan(10).minutes() == 30
                && forest.createPlanner().plan(10).minutes() == 40
                && coast.createPlanner().plan(10).minutes() == 50, "family-specific flight duration");
    }

    private static void testSensorBehavior() {
        FarmFactory farm = new FarmFactory();
        var plan = farm.createPlanner().plan(10);
        var average = farm.inspect(plan, 100, 200, 600);
        check(average.value() == 30 && !average.alert(), "calibrated averaging and farm threshold");
        check(farm.inspect(plan, 299).alert(), "farm below moisture threshold");
        ForestFactory forest = new ForestFactory();
        var forestPlan = forest.createPlanner().plan(10);
        check(forest.inspect(forestPlan, 600).alert()
                && !forest.inspect(forestPlan, 599).alert(), "forest temperature threshold");
        CoastFactory coast = new CoastFactory();
        var coastPlan = coast.createPlanner().plan(10);
        var observation = coast.inspect(coastPlan, 200);
        check(observation.value() == 2 && observation.alert()
                && observation.unit().equals("mg/L oil"), "coast calibration and threshold");
        check(!coast.inspect(coastPlan, 199).alert(), "coast below oil threshold");
        check(farm.inspect(plan, 0, 1000).value() == 50, "sample range endpoints");
        check(farm.createPlanner().plan(0.1).minutes() == 1
                && farm.createPlanner().plan(100).minutes() == 300, "area and duration boundaries");
    }

    private static void testRuntimeSelection() {
        check(FactorySelector.select("farm") instanceof FarmFactory, "select farm");
        check(FactorySelector.select(" FOREST ") instanceof ForestFactory, "select normalized forest");
        check(FactorySelector.select("coast") instanceof CoastFactory, "select coast");
        expectInvalid(() -> FactorySelector.select("desert"), "Unknown family: desert", "unknown family");
        expectInvalid(() -> FactorySelector.select(null), "A family name is required", "null family");
        expectInvalid(() -> FactorySelector.select(" "), "A family name is required", "blank family");
    }

    private static void testBusinessOperations() {
        var farm = new InspectionService<>(new FarmFactory());
        check(farm.survey(10, 180).equals(
                "SURVEY | FARM | Parallel crop rows | 18.00 % moisture | Irrigate crops"),
                "survey combines planner sensor and report");
        check(farm.scheduleFollowUp(10, 180).startsWith("FOLLOW-UP | Revisit 2.5 ha for 8 min"),
                "alert schedules quarter-area follow-up with rounded duration");
        check(farm.scheduleFollowUp(10, 400).startsWith("FOLLOW-UP | Not needed"),
                "healthy reading avoids unnecessary follow-up");
        check(farm.compareSurveys(10, 180, 400).startsWith("COMPARISON | RESOLVED"), "resolved problem");
        check(farm.compareSurveys(10, 400, 180).startsWith("COMPARISON | NEW_ALERT"), "new problem");
        check(farm.compareSurveys(10, 100, 180).startsWith("COMPARISON | STILL_ALERT"), "persistent problem");
        check(farm.compareSurveys(10, 400, 500).startsWith("COMPARISON | CLEAR"), "both surveys clear");
        check(new InspectionService<>(new ForestFactory()).survey(5, 700).contains("Send fire patrol"),
                "forest family collaboration");
        check(new InspectionService<>(new CoastFactory()).survey(5, 300).contains("Deploy containment booms"),
                "coast family collaboration");
    }

    private static void testInvalidInputs() {
        FarmFactory factory = new FarmFactory();
        var plan = factory.createPlanner().plan(10);
        for (double area : new double[] {0, -1, 100.01, Double.NaN, Double.POSITIVE_INFINITY}) {
            expectInvalid(() -> factory.createPlanner().plan(area),
                    "Area must be greater than 0 and at most 100 hectares", "invalid area " + area);
        }
        for (double sample : new double[] {-1, 1001, Double.NaN, Double.POSITIVE_INFINITY}) {
            expectInvalid(() -> factory.inspect(plan, sample),
                    "Samples must be finite numbers from 0 to 1000", "invalid sample " + sample);
        }
        expectInvalid(() -> factory.inspect(plan), "At least one sample is required", "empty samples");
        expectInvalid(() -> factory.inspect(plan, (double[]) null),
                "At least one sample is required", "null samples");
        expectInvalid(() -> factory.inspect(null, 180), "An inspection plan is required", "null plan");
        expectInvalid(() -> new InspectionService<FarmFactory.Family>(null),
                "An inspection factory is required", "null factory");
    }

    private static void testAbstractionBasedClient() {
        TestFactory factory = new TestFactory();
        InspectionService<TestFamily> service = new InspectionService<>(factory);
        check(service.survey(4, 10).equals("SURVEY | TEST route=Test route value=20.0"),
                "client accepts an unrelated test factory through abstractions");
        check(service.scheduleFollowUp(4, 10).startsWith("FOLLOW-UP | Revisit 1.0 ha for 1 min"),
                "follow-up uses injected planner");
        check(service.compareSurveys(4, 10, 0).startsWith("COMPARISON | RESOLVED"),
                "comparison uses injected sensor behavior");
        check(factory.sensorCreations == 4, "Creator business logic invokes overridden Factory Method");
    }

    private static void testCompileTimeCompatibility() throws Exception {
        String setup = "var farm = new FarmFactory(); var forest = new ForestFactory();"
                + "var observation = farm.inspect(farm.createPlanner().plan(10), 180);";
        check(canCompile(setup + "farm.createReportWriter().write(observation);"),
                "matching family pipeline compiles");
        check(!canCompile(setup + "forest.createReportWriter().write(observation);"),
                "mixed-family report is rejected by javac");
        check(!canCompile("new InspectionService<FarmFactory.Family>(new ForestFactory());"),
                "mixed-family client is rejected by javac");
        check(!canCompile("new ForestFactory().inspect(new FarmFactory().createPlanner().plan(10), 180);"),
                "mixed-family plan is rejected by javac");
    }

    // Compile short snippets in a temporary directory; keep the repository free of generated files.
    private static boolean canCompile(String statements) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new AssertionError("Run tests with a JDK, not a JRE");
        }
        Path output = Files.createTempDirectory("inspection-type-test-");
        var diagnostics = new DiagnosticCollector<JavaFileObject>();
        JavaFileObject source = new SimpleJavaFileObject(URI.create("string:///FamilyCheck.java"),
                JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return "class FamilyCheck { void test() { " + statements + " } }";
            }
        };
        try (StandardJavaFileManager files = compiler.getStandardFileManager(diagnostics, null, null)) {
            List<String> options = List.of("-classpath", System.getProperty("java.class.path"),
                    "-d", output.toString(), "-proc:none", "-Xlint:all", "-Werror");
            boolean success = compiler.getTask(null, files, diagnostics, options, null, List.of(source)).call();
            if (!success) {
                boolean typeMismatch = false;
                for (var diagnostic : diagnostics.getDiagnostics()) {
                    if (diagnostic.getCode().equals("compiler.err.prob.found.req")) {
                        typeMismatch = true;
                    }
                }
                if (!typeMismatch) {
                    throw new AssertionError("Unexpected compiler error: " + diagnostics.getDiagnostics());
                }
            }
            return success;
        } finally {
            try (var paths = Files.walk(output)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }

    private static final class TestFamily implements InspectionFamily { }

    private static final class TestFactory extends InspectionFactory<TestFamily> {
        int sensorCreations;

        @Override
        public RoutePlanner<TestFamily> createPlanner() {
            return hectares -> new InspectionData.Plan<>(hectares, (int) Math.ceil(hectares), "Test route");
        }

        @Override
        public Sensor<TestFamily> createSensor() {
            sensorCreations++;
            return new Sensor<>() {
                public double convert(double sample) { return sample * 2; }
                public boolean requiresAction(double value) { return value > 0; }
                public String unit() { return "test units"; }
            };
        }

        @Override
        public ReportWriter<TestFamily> createReportWriter() {
            return observation -> "TEST route=" + observation.plan().route() + " value=" + observation.value();
        }
    }

    private static void testMountainExtension() throws Exception {
        MountainFactory factory = new MountainFactory();
        check(factory.createPlanner() instanceof MountainFactory.MountainPlanner, "mountain planner creation");
        check(factory.createSensor() instanceof MountainFactory.MountainSensor, "mountain sensor creation");
        check(factory.createReportWriter() instanceof MountainFactory.MountainReportWriter, "mountain writer creation");
        check(FactorySelector.select("mountain") instanceof MountainFactory, "select fourth family");
        var plan = factory.createPlanner().plan(10);
        check(plan.minutes() == 60 && factory.inspect(plan, 500).alert()
                && !factory.inspect(plan, 499).alert(), "mountain route and snow threshold");
        var service = new InspectionService<>(factory);
        check(service.survey(10, 600).contains("60.00 cm snow | Close trail for snow clearance"),
                "fourth family survey with unchanged client");
        check(service.scheduleFollowUp(10, 600).startsWith("FOLLOW-UP | Revisit 2.5 ha for 15 min"),
                "fourth family follow-up");
        check(service.compareSurveys(10, 600, 200).startsWith("COMPARISON | RESOLVED"),
                "fourth family comparison");
        check(!canCompile("new MountainFactory().createReportWriter().write("
                + "new FarmFactory().inspect(new FarmFactory().createPlanner().plan(10), 180));"),
                "fourth family rejects original-family data");
    }

    private static void expectInvalid(Runnable action, String message, String testName) {
        try {
            action.run();
            throw new AssertionError("FAIL: " + testName + " accepted invalid input");
        } catch (IllegalArgumentException e) {
            check(message.equals(e.getMessage()), testName);
        }
    }

    private static void check(boolean condition, String testName) {
        if (!condition) {
            throw new AssertionError("FAIL: " + testName);
        }
        passed++;
        System.out.println("PASS: " + testName);
    }
}
