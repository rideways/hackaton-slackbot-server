package com.bookinggo.hackaton.infrastructure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Data
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    @NotEmpty
    private String scriptsStoragePath;

}
