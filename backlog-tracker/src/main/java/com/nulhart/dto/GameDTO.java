package com.nulhart.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GameDTO {
    @NotBlank
    @NotNull
    private String title;
    @NotBlank
    @NotNull
    private String console;
    @NotBlank
    @NotNull
    private String status;
    private int hoursPlayed;
    private String opinion;
    private LocalDate startDate;
    private LocalDate dateOfCompletion;

    public GameDTO() {
    }


    public GameDTO(
                String title, String console, String status, int hoursPlayed,
                String opinion, LocalDate startDate, LocalDate dateOfCompletion) {


        this.title = title;
        this.console = console;
        this.status = status;
        this.hoursPlayed = hoursPlayed;
        this.opinion = opinion;
        this.startDate = startDate;
        this.dateOfCompletion = dateOfCompletion;
    }
}
