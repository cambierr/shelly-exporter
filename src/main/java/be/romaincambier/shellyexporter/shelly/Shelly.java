package be.romaincambier.shellyexporter.shelly;

public abstract class Shelly {

    protected Shelly(Registry registry) {
        registry.register(this, getName());
    }

    protected abstract String getName();

    public abstract String scrape(String host, String username, String password);
}
