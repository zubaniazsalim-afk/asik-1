import java.util.Locale;

// The only place that knows which concrete family names are available.
public final class FactorySelector {
    private FactorySelector() { }

    public static InspectionFactory<?> select(String family) {
        if (family == null || family.isBlank()) {
            throw new IllegalArgumentException("A family name is required");
        }
        return switch (family.trim().toLowerCase(Locale.ROOT)) {
            case "farm" -> new FarmFactory();
            case "forest" -> new ForestFactory();
            case "coast" -> new CoastFactory();
            case "mountain" -> new MountainFactory();
            default -> throw new IllegalArgumentException("Unknown family: " + family);
        };
    }
}
