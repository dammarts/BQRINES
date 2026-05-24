package org;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// scanBasePackages limits component scanning to our own packages only.
// Without this, scanning the root "org" package would also scan org.springframework.*
// classes from JARs, causing r2dbc and other autoconfiguration conflicts.
@SpringBootApplication(scanBasePackages = {
        "org.config",
        "org.controllers",
        "org.exception",
        "org.models",
        "org.repositories",
        "org.services"
})
public class BqrinesApplication {

    public static void main(String[] args) {
        SpringApplication.run(BqrinesApplication.class, args);
    }
}
