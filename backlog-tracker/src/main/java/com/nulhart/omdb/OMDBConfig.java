package com.nulhart.omdb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OMDBConfig {
    private static final String API_URL = "http://www.omdbapi.com";

    @Bean
    public RestClient omdbRestClient(){
        return RestClient.builder().baseUrl(API_URL).build();
    }
}
