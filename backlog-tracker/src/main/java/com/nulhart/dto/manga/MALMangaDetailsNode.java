package com.nulhart.dto.manga;

import com.nulhart.dto.anime.RelatedMALDTO;

import java.util.List;

public record MALMangaDetailsNode(String start_date, String end_date, Integer num_volumes,
                                  Integer num_chapters, List<RelatedMALDTO> related_manga) {
}
