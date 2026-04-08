package com.nulhart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@RequiredArgsConstructor
@Data
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    @NotNull
    @NotBlank
    private String title;
    @NotNull
    @NotBlank
    private String status;
    @NotNull
    private LocalDate releaseDate;
    private Integer watchYear;
    private Integer score;
    private String image;
    private String director;
    private String imdbId;
    private Set<String> tags = new HashSet<>();

    public Movie(String name, String status){
        this.title = name;
        this.status = status;
    }

    public Movie(String title, String status, LocalDate releaseDate,
                 Integer watchYear, String image, String director, String imdbId, Integer score
    ) {
        this.title = title;
        this.status=status;
        this.releaseDate =  releaseDate;
        this.watchYear = watchYear;
        this.image = image;
        this.director = director;
        this.imdbId = imdbId;
        this.score = score;
    }
}
