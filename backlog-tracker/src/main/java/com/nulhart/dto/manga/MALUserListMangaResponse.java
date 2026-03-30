package com.nulhart.dto.manga;

import com.nulhart.dto.anime.PagingDTO;

import java.util.List;

public record MALUserListMangaResponse(List<MALUserListMangaDTO> data, PagingDTO paging) {
}
