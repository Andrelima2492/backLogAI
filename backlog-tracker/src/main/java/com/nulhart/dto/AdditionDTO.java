package com.nulhart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
@Data
@AllArgsConstructor
public class AdditionDTO {
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
    private String parentGameName;
}
