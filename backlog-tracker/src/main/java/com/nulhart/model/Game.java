package com.nulhart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Data
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long UUID;
    private Long rawgId;
    @Column(nullable = false, unique = true)
    private String title;
    @Column(nullable = false)
    private String console;
    @Column(nullable = false)
    private String status;
    private int hoursPlayed;
    private String opinion;
    private LocalDate startDate;
    private LocalDate dateOfCompletion;
    private Integer estimatedPlayTime;
    private  String image;
    @OneToMany(mappedBy = "parentGame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Game> additions;
    @ManyToOne
    @JoinColumn(name="parent_game_id")
    private Game parentGame;


    public Game(@NotBlank @NotNull String title,
                @NotBlank @NotNull String console,
                @NotBlank @NotNull String status,
                int hoursPlayed,
                String opinion,
                LocalDate startDate, LocalDate dateOfCompletion, Integer estimatedPlayTime, String image) {
                this.title= title;
                this.console=console;
                this.status=status;
                this.hoursPlayed = hoursPlayed;
                this.opinion=opinion;
                this.startDate = startDate;
                this.dateOfCompletion=dateOfCompletion;
                this.estimatedPlayTime=estimatedPlayTime;
                this.image=image;
    }
}
