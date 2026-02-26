package com.nulhart.rawg;
import com.nulhart.dto.RawgResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class RawgClient {

    private final RawgProperties rawgProperties;
    private final RestClient rawgRestClient;

    public RawgResponse searchGames(String search, int page, int pageSize){
        return rawgRestClient.get().uri(uriBuilder -> uriBuilder.path("/games")
                .queryParam("key", rawgProperties.getApiKey())
                .queryParam("search", search)
                .queryParam("page", page)
                .queryParam("page_size", pageSize)
                .queryParam("search_precise", true)
                .build()).retrieve().body(RawgResponse.class);

    }
}

