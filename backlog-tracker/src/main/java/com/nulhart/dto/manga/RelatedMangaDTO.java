package com.nulhart.dto.manga;


public record RelatedMangaDTO(String title, String status, Integer numberOfChapters, Integer chaptersRead,
                              Integer numberOfVolumes, Integer volumesRead, Integer malId) {
}
