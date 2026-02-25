package com.nulhart.rawg;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RawgClient {

    private final RawgProperties rawgProperties;

     @PostConstruct
    public void test(){
         System.out.println("RAWG KEY FROM VAULT " +rawgProperties.getApiKey());
     }
}
