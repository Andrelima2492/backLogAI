package com.nulhart.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@RequiredArgsConstructor
public class Anime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
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
    @ToString.Exclude
    Set<Anime> sequel= new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "parent_anime_id")
    @ToString.Exclude
    Anime parent;
    @EqualsAndHashCode.Include
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
