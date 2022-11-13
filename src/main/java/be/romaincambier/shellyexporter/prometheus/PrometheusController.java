package be.romaincambier.shellyexporter.prometheus;

import be.romaincambier.shellyexporter.shelly.Registry;
import be.romaincambier.shellyexporter.shelly.Shelly;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Controller("/metrics")
@RequiredArgsConstructor
@Slf4j
public class PrometheusController {

    private final Registry registry;

    @Get(uri = "/", produces = MediaType.TEXT_PLAIN)
    public String scrape(
            @QueryValue("type") String type,
            @QueryValue("host") String host,
            @QueryValue(value = "username", defaultValue = "") String username,
            @QueryValue(value = "password", defaultValue = "") String password) {

        if (type == null || type.isBlank()) {
            log.warn("Invalid value for type: '{}'", type);
            return "";
        }

        if (host == null || host.isBlank()) {
            log.warn("Invalid value for host: '{}'", host);
            return "";
        }

        Optional<Shelly> scraperMaybe = registry.forName(type);

        if (scraperMaybe.isEmpty()) {
            log.warn("No such shelly scraper: {}", type);
            return "";
        }

        return scraperMaybe.get().scrape(host, username, password);
    }

}
