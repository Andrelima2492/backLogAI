package com.nulhart.rawg;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rawg")
@Data
public class RawgProperties {
    private String apiKey;
}
