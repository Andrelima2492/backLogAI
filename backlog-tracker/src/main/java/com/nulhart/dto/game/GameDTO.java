package com.nulhart.dto.game;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GameDTO {
    String uuid;
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
    private String parentGameName;
    private Set<AdditionDTO> additions = new HashSet<>();
    private Set<String> tags = new HashSet<>();

}
