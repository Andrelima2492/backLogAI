package com.nulhart.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
public class Anime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String UUID;
    @NotNull
    @NotBlank
    @Column(unique=true)
    String title;
    @NotNull
    @NotBlank
    String status;
    @Nullable
    Integer numberOfEpisodes;
    @Nullable
    Integer episodesWatched;
    @Nullable
    Integer score;
    String image;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Anime> sequel;
    @OneToMany(mappedBy = "parent",cascade = CascadeType.ALL, orphanRemoval = true)
    List<Anime> spinOff;
    @ManyToOne
    @JoinColumn(name = "parent_anime_id")
    Anime parent;
    Integer malId;
    LocalDate startDate;
    LocalDate endDate;

    public Anime(String title, String status,Integer episodesWatched, Integer score){
        this.title=title;
        this.status=status;
        this.episodesWatched=episodesWatched;
        this.score=score;
    }

}
