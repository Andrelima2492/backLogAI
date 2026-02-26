package com.nulhart.rawg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RawgConfig {
    private static final String API_URL = "https://api.rawg.io/api/";
    @Bean
    public RestClient rawgRestClient(){
        return RestClient.builder().baseUrl(API_URL).build();

    }

}
