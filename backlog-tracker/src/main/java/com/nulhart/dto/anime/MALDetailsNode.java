package com.nulhart.dto.anime;


import java.time.LocalDate;
import java.util.List;

public record MALDetailsNode(LocalDate start_date, LocalDate end_date,  Integer num_episodes,
                             List<RelatedMALDTO> related_anime) {
}
