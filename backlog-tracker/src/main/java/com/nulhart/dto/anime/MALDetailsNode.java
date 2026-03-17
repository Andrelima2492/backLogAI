package com.nulhart.dto.anime;


import java.time.LocalDate;
import java.util.List;

public record MALDetailsNode(String start_date, String end_date,  Integer num_episodes,
                             List<RelatedMALDTO> related_anime) {
}
