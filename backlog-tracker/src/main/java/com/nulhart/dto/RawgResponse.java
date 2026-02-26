package com.nulhart.dto;

import java.util.List;

public record RawgResponse (
    int count,
    String next,
    String previous,
    List<RawgDTO> results
){}
