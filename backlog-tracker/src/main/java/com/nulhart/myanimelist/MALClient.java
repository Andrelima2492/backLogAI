package com.nulhart.myanimelist;

import com.nulhart.dto.anime.MALAnimeNode;
import com.nulhart.dto.anime.MALAnimeResponse;
import com.nulhart.dto.anime.MALDetailsNode;
import com.nulhart.dto.anime.MALUserListResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class MALClient {
    private final MALProperties malProperties;
    private final RestClient malRestClient;

    @PostConstruct
    public void test(){
        System.out.println("MAL API key: "+malProperties.getApiKey());
    }

 public MALAnimeResponse getMALAnime(String search, Integer limit){
        return malRestClient.get().uri(uriBuilder -> uriBuilder.path("/anime")
                        .queryParam("q", search)
                        .queryParam("limit", limit).build()
                ).header("X-MAL-CLIENT-ID", malProperties.getApiKey()).retrieve().
                body(MALAnimeResponse.class);
    }
    public MALDetailsNode getAnimeDetails(Integer animeId, String fields){
        return malRestClient.get().uri(uriBuilder -> uriBuilder.path("/anime/"+animeId)
                .queryParam("fields", fields).build()).header(
                        "X-MAL-CLIENT-ID", malProperties.getApiKey()).retrieve().
                body(MALDetailsNode.class);
    }

    public MALUserListResponse importMAL(String username, String status, Integer limit, Integer offset){
        return malRestClient.get().uri(uriBuilder -> uriBuilder.path("/users/"+username+"/animelist")
                .queryParam("status", status)
                .queryParam("limit",limit)
                .queryParam("offset", offset).build()).header("X-MAL-CLIENT-ID",
                malProperties.getApiKey()).retrieve().body(MALUserListResponse.class);

    }

}
