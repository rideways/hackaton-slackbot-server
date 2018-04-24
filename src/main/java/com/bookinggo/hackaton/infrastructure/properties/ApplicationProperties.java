package com.bookinggo.hackaton.infrastructure.properties;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Data
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    @NotEmpty
    private String scriptsStoragePath;

    @Range(min = 1000, max = 64000)
    private int workerPortRangeBegin = 6000;
    @Range(min = 1000, max = 64000)
    private int workerPortRangeEnd = 6100;

}
