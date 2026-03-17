package com.nulhart.dto.anime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ListStatusDTO(String status, Integer score,
                            @JsonProperty("num_episodes_watched")
                            Integer numWatchedEpisodes) {
}
