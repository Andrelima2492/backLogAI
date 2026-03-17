package com.nulhart.myanimelist;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mal")
@Data
public class MALProperties {
    private String apiKey;
    private String secret;
}
