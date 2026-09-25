// Immutable launch location: name and country.
public final class LaunchSite {
    private final String name;
    private final String country;

    public LaunchSite(String name, String country) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Launch site name must not be blank");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country must not be blank");
        }
        this.name = name;
        this.country = country;
    }

    public String getName() { return name; }
    public String getCountry() { return country; }

    @Override
    public String toString() {
        return name + ", " + country;
    }
}
