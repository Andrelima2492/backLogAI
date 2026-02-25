package com.nulhart.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String title;
    @Column(nullable = false)
    private String console;
    @Column(nullable = false)
    private String status;
    private int hoursPlayed;
    private Integer estimatedPlayTime;
    private String opinion;
    private LocalDate startDate;
    private LocalDate dateOfCompletion;
    @OneToMany(mappedBy = "parentGame")
    private List<Game> additions = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name="parent_game_id")
    private Game parentGame;

    public Game() {
    }


    public Game(
                String title, String console, String status, int hoursPlayed,
                String opinion,  LocalDate startDate, LocalDate dateOfCompletion) {

        this.id = id;
        this.title = title;
        this.console = console;
        this.status = status;
        this.hoursPlayed = hoursPlayed;
        this.opinion = opinion;
        this.startDate = startDate;
        this.dateOfCompletion = dateOfCompletion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return id == game.id && hoursPlayed == game.hoursPlayed && Objects.equals(title, game.title) && Objects.equals(console, game.console) && Objects.equals(status, game.status) && Objects.equals(opinion, game.opinion) && Objects.equals(startDate, game.startDate) && Objects.equals(dateOfCompletion, game.dateOfCompletion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, console, status, hoursPlayed, opinion, startDate, dateOfCompletion);
    }
}
