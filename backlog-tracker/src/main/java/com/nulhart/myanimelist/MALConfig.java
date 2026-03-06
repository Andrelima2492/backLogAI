package com.nulhart.myanimelist;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MALConfig {
    public static final String API_URL = "https://api.myanimelist.net/v2/";

    @Bean
    public RestClient malRestClient(){
        return RestClient.builder().baseUrl(API_URL).build();
    }
}
