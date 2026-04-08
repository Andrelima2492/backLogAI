package com.nulhart.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class MovieDTO {
    private String id;
    private String title;
    private String status;
    private LocalDate releaseDate;
    private Integer watchYear;
    private String image;
    private String director;
    private String imdbId;
    private Integer score;
    private Set<String> tags;
}
