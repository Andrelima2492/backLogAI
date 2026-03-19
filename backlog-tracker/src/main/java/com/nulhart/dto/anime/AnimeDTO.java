package com.nulhart.dto.anime;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class AnimeDTO{
    private String uuid;
    private String title;
    private String status;
    private Integer numberOfEpisodes;
    private Integer episodesWatched;
    private Integer score;
    private String image;
    private Set<RelatedDTO> sequels;
    private RelatedDTO parent;
    private Integer malId ;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<String> tags = new HashSet<>();

}
