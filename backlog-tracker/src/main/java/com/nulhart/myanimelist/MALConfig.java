package com.nulhart.myanimelist;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;


@Configuration
public class MALConfig {
    public static final String API_URL = "https://api.myanimelist.net/v2/";
    public static final String API_AUTH_URL = "https://myanimelist.net/v1/";
    private HttpComponentsClientHttpRequestFactory requestFactory() {

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(30))
                .setResponseTimeout(Timeout.ofMinutes(2))
                .build();

        CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();

        return new HttpComponentsClientHttpRequestFactory(client);
    }
    @Bean
    public RestClient malRestClient(){
        return RestClient.builder().baseUrl(API_URL)
                .requestFactory(requestFactory()).build();
    }
    @Bean
    public RestClient malAuthRestClient(){
        return  RestClient.builder().baseUrl(API_AUTH_URL).requestFactory(requestFactory()).build();
    }
}
