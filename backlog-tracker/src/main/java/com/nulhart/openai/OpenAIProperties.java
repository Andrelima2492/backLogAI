package com.nulhart.openai;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "openai")
public class OpenAIProperties {
    private String apiKey;

    @PostConstruct
    public void test(){
        System.out.println("Openai secret is "+apiKey);
    }
}
