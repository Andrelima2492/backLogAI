package com.nulhart.controller;

import com.nulhart.services.MangaService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manga")
@AllArgsConstructor
public class MangaController {
    private MangaService mangaService;


}
