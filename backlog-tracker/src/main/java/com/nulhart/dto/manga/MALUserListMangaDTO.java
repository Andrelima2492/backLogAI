package com.nulhart.dto.manga;

import com.nulhart.dto.anime.MALAnimeNode;


public record MALUserListMangaDTO(MALAnimeNode node, MALMangaListStatusDTO list_status) {
}
