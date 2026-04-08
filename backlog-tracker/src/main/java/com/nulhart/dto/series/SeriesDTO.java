package com.nulhart.dto.series;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeriesDTO {
    private String id;
    private String title;
    private String status;
    private Integer numberOfSeasons;
    private Integer seasonsWatched;
    private Integer score;
    private String imdbID;
    private String image;
    private String yearsAired;
    private Integer yearWatched;
}
