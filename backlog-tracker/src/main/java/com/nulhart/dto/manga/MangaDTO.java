package com.nulhart.dto.manga;

import com.nulhart.dto.anime.RelatedDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class MangaDTO {
    private String id;
    private String title;
    private String status;
    private Integer numberOfChapters;
    private Integer chaptersRead;
    private Integer numberOfVolumes;
    private Integer volumesRead;
    private Integer score;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer malId;
    private String image;
    private Set<RelatedMangaDTO> sequels = new HashSet<>();
    private RelatedMangaDTO parent;
    private Set<String> tags = new HashSet<>();
}
