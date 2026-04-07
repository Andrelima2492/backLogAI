package com.nulhart.omdb;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "omdb")
public class OMDBProperties {
    private String apiKey;

    @PostConstruct
    public void test(){
        System.out.println("omdb key "+apiKey );
    }
}
