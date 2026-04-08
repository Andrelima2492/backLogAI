package com.nulhart.omdb;

import com.nulhart.dto.movie.OMDBResponse;
import com.nulhart.dto.series.OMDBSeriesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class OMDBClient {

    private final OMDBProperties omdbProperties;
    private final RestClient omdbRestClient;

    public OMDBResponse searchMovie(String title){
        return omdbRestClient.get().uri(uriBuilder -> uriBuilder.path("/")
                .queryParam("apikey", omdbProperties.getApiKey())
                .queryParam("t", title)
                .queryParam("r","json")
                .queryParam("type", "movie").build()).retrieve().body(OMDBResponse.class);
    }

    public OMDBSeriesResponse searchSeries(String title){
        return omdbRestClient.get().uri(uriBuilder -> uriBuilder.path("/")
                .queryParam("apikey", omdbProperties.getApiKey())
                .queryParam("t",title)
                .queryParam("r","json")
                .queryParam("type","series").build()).retrieve().body(OMDBSeriesResponse.class);
    }

}
