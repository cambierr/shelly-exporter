package be.romaincambier.shellyexporter.shelly;

import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class Registry {

    private final Map<String, Shelly> modules = new HashMap<>();

    public void register(Shelly shelly, String name) {
        modules.put(name, shelly);
    }

    public Optional<Shelly> forName(String name) {
        return Optional.ofNullable(modules.get(name));
    }
}
