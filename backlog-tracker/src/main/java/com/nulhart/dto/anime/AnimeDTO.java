package com.nulhart.dto.anime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
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
    private List<RelatedDTO> sequels;
    private List<RelatedDTO> spinOffs;
    private RelatedDTO parent;
    private Integer malId ;
    private LocalDate startDate;
    private LocalDate endDate;

}
