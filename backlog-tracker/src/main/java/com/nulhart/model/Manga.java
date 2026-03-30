package com.nulhart.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class Manga {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    @NotNull
    @Column(unique = true)
    private String title;

    @NotNull
    @NotBlank
    private String status;
    @Nullable
    private Integer numberOfChapters;
    @Nullable
    private Integer chaptersRead;
    @Nullable
    private Integer numberOfVolumes;
    @Nullable
    private Integer volumesRead;
    @Nullable
    private Integer score;
    private LocalDate startDate;
    private LocalDate endDate;
    @ManyToOne
    @JoinColumn(name="parent_manga_id")
    @ToString.Exclude
    private Manga parent;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Manga> sequels = new HashSet<>();
    @EqualsAndHashCode.Include
    private Integer malId;
    private String image;


    public Manga(String title, String status){
        this.title = title;
        this.status = status;
    }
}
