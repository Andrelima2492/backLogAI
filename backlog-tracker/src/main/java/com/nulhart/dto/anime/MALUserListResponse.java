package com.nulhart.dto.anime;

import java.util.List;

public record MALUserListResponse(List<MALUserListDTO> data, PagingDTO paging) {
}
