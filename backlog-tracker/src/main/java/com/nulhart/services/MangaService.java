package com.nulhart.services;

import com.nulhart.myanimelist.MALClient;
import com.nulhart.myanimelist.MALProperties;
import com.nulhart.repository.MALTokenRepository;
import com.nulhart.repository.MangaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MangaService {
    private final MangaRepository mangaRepository;
    private final MALProperties malProperties;
    private final MALClient malClient;
    private final MALTokenRepository malTokenRepository;

    
}