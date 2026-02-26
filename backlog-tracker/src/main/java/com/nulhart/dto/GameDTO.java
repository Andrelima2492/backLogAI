package com.nulhart.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
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
    private Integer estimatedPlayTime;
    private String image;
    private Long rawgId;
    private List<GameDTO> additions;
    private GameDTO parentGame;



}
