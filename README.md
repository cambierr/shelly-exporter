## What this is about

This is an exporter for Prometheus to be able to scrape metrics from Shelly (https://shelly.cloud/) modules.

## Why yet-another app doing this

Well, you may be right. But...

First, I wanted to try Micronaut Native for a while, this was a pretty good pretext.

Then, I wanted this to be stateless and config-less. That is, I wanted the full configuration to be in Prometheus and nothing here.

Finally, because I decided so.

## Why java ? this is old/slow/...

Have you actually been trying "modern" java ? That is java (and frameworks) not from previous century ?

## How to use

First, run this application:
* Either by downloading the native executable (see releases page) and starting it
* Or by using the docker image: `docker run -d -p 8080:8080 cambierr/shelly-exporter`

Then, in your prometheus config file, add a scrape job:

```yaml
scrape_configs:
  # ... your existing jobs
  - job_name: 'shelly'
    metrics_path: /metrics
    static_configs:
      - targets:
          - 127.0.0.1:8080 # This is where shelly-exporter is listening
    scrape_interval: 60s
    params:
      host: ["192.168.0.13"] # This is the shelly module hostname
      type: ["em"] # This is the shelly module type
      username: ["some-username"] # optional, only if configured on your shelly module
      password: ["some-password"] # optional, only if configured on your shelly module
```

## How to build

* `mvn package` will build you an executable jar
* `mvn package -Dpackaging=native-image` will build you a native executable (assuming you have graal available on your machine)
* `mvn package -Dpackaging=docker-native` will build you a docker image containing the native executable

## Supported Shelly modules

* Shelly EM

Not a lot, heh ? Feel free to send a PR with the implementation of a new module, or open an issue with the response of the `/status` call of your module :-)

## How to add support for a module

Just create a new class in the `be.romaincambier.shellyexporter.shelly.modules` package, extend the `Shelly` abstract class, and do your thing.