package com.nulhart.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NotNull
    @NotBlank
    @Column(unique = true)
    private String title;
    @NotBlank
    @NotNull
    private String status;
    private Integer numberOfSeasons;
    @Nullable
    private Integer seasonsWatched;
    private String imdbId;
    @Nullable
    private Integer score;
    private String image;
    private String yearsAired;
    private Integer yearWatched;

    public Series(String title, String status) {
        this.title = title;
        this.status = status;
    }
}
