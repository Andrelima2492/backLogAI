package com.nulhart.myanimelist;

import com.nulhart.dto.anime.*;
import com.nulhart.dto.manga.MALMangaDetailsNode;
import com.nulhart.dto.manga.MALUserListMangaResponse;
import io.netty.handler.codec.base64.Base64Encoder;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@AllArgsConstructor
public class MALClient {
    private final MALProperties malProperties;
    private final RestClient malRestClient;
    private final RestClient malAuthRestClient;

    @PostConstruct
    public void test(){
        System.out.println("MAL secret: "+malProperties.getSecret());
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
    public MALMangaDetailsNode getMangaDetails(Integer mangaId, String fields){
        return malRestClient.get().uri(uriBuilder -> uriBuilder.path("manga/"+mangaId)
                .queryParam("fields", fields).build()).header(
                        "X-MAL-CLIENT-ID", malProperties.getApiKey()).retrieve().body(MALMangaDetailsNode.class);
    }

    public MALUserListResponse importMAL(String username, String status, Integer limit, Integer offset, String token){
    return  malRestClient.get().uri(uriBuilder -> uriBuilder.path("/users/"+username+"/animelist")
            .queryParam("status", status)
            .queryParam("limit",limit)
            .queryParam("fields", "list_status")
            .queryParam("offset", offset).build()).header("Authorization", "Bearer "+
            token).retrieve().body(MALUserListResponse.class);
    }
    public MALUserListMangaResponse importManga(String username, String status, Integer limit, Integer offset, String token){
        return malRestClient.get().uri(uriBuilder ->uriBuilder.path("/users/"+username+"/mangalist")
                .queryParam("status", status)
                .queryParam("limit", limit)
                .queryParam("fields", "list_status")
                .queryParam("offset", offset).build()).header("Authorization", "Bearer "+
                token).retrieve().body(MALUserListMangaResponse.class);
    }


    public MALTokenResponse exchangeCodeForToken(String code, String verifier){
        String credentials = malProperties.getApiKey()+":"+malProperties.getSecret();
        String encoded  = Base64.getEncoder().encodeToString(credentials.getBytes());
        return malAuthRestClient.post().uri(uriBuilder -> uriBuilder.path("/oauth2/token").build())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization","Basic "+encoded)
                .body(
                        "client_id="+malProperties.getApiKey()+
                                "&grant_type=authorization_code"+
                                "&code="+code+
                                "&redirect_uri=http://localhost:8080/auth/mal/callback"+
                                "&code_verifier="+verifier
                ).retrieve().body(MALTokenResponse.class);
    }


}
