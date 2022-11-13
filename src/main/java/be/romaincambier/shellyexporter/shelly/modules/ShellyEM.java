package be.romaincambier.shellyexporter.shelly.modules;

import be.romaincambier.shellyexporter.shelly.Registry;
import be.romaincambier.shellyexporter.shelly.Shelly;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Singleton
@Slf4j
public class ShellyEM extends Shelly {

    private static final String STATUS_PATH = "/status";

    private final BlockingHttpClient httpClient;

    public ShellyEM(Registry registry, HttpClient httpClient) {
        super(registry);
        this.httpClient = httpClient.toBlocking();
    }

    @Override
    protected String getName() {
        return "em";
    }

    @Override
    public String scrape(String host, String username, String password) {

        log.info("Scrapping host '{}' with username '{}' and password '{}'", host, username, (password != null && !password.isBlank()) ? "*****" : "");

        HttpResponse<StatusResponse> response = httpClient.exchange(
                HttpRequest.GET("http://" + host + STATUS_PATH).basicAuth(username, password),
                StatusResponse.class);

        if (response.code() != 200) {
            log.error("Invalid response code from Shelly EM: {}", response.code());
            return "";
        }

        StatusResponse status = response.body();

        StringBuilder body = new StringBuilder();

        // wifi
        body.append("# TYPE wifi_stats_connected gauge").append("\n");
        body.append("wifi_stats_connected").append("{ssid=\"").append(status.wifiSta.ssid).append("\"} ").append(status.wifiSta.connected ? 1.0 : 0.0).append("\n");
        body.append("# TYPE wifi_stats_rssi gauge").append("\n");
        body.append("wifi_stats_rssi ").append("{ssid=\"").append(status.wifiSta.ssid).append("\"} ").append(status.wifiSta.rssi).append("\n");
        // system
        body.append("# TYPE uptime gauge").append("\n");
        body.append("uptime ").append(status.uptime).append("\n");
        // meters
        for (int i = 0; i < status.emeters.size(); i++) {
            Emeter meter = status.emeters.get(i);
            body.append("# TYPE emeter_power gauge").append("\n");
            body.append("emeter_power").append("{emeter=\"").append(i).append("\"} ").append(meter.power).append("\n");
            body.append("# TYPE emeter_reactive gauge").append("\n");
            body.append("emeter_reactive").append("{emeter=\"").append(i).append("\"} ").append(meter.reactive).append("\n");
            body.append("# TYPE emeter_pf gauge").append("\n");
            body.append("emeter_pf").append("{emeter=\"").append(i).append("\"} ").append(meter.pf).append("\n");
            body.append("# TYPE emeter_voltage gauge").append("\n");
            body.append("emeter_voltage").append("{emeter=\"").append(i).append("\"} ").append(meter.voltage).append("\n");
            body.append("# TYPE emeter_valid gauge").append("\n");
            body.append("emeter_valid").append("{emeter=\"").append(i).append("\"} ").append(meter.isValid ? 1.0 : 0.0).append("\n");
            body.append("# TYPE emeter_power_total counter").append("\n");
            body.append("emeter_power_total").append("{emeter=\"").append(i).append("\"} ").append(meter.total).append("\n");
            body.append("# TYPE emeter_returned_total counter").append("\n");
            body.append("emeter_returned_total").append("{emeter=\"").append(i).append("\"} ").append(meter.totalReturned).append("\n");
        }

        return body.toString();
    }

    @Data
    @Introspected
    static class StatusResponse implements Serializable {
        private WifiStats wifiSta;
        private List<Emeter> emeters;
        private int uptime;
    }

    @Data
    @Introspected
    static class WifiStats {
        private boolean connected;
        private String ssid;
        private String ip;
        private int rssi;
    }

    @Data
    @Introspected
    static class Emeter {
        private double power;
        private double reactive;
        private double pf;
        private double voltage;
        private boolean isValid;
        private double total;
        private double totalReturned;
    }
}
