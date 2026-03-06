package com.nulhart.controller;

import com.nulhart.dto.anime.AnimeDTO;
import com.nulhart.services.AnimeService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/anime")
@AllArgsConstructor
public class AnimeController {
    private AnimeService animeService;

    @GetMapping
    public List<AnimeDTO> getAllAnime(){
        return animeService.getAllAnime();
    }

    @GetMapping("/id/{uuid}")
    public AnimeDTO getAnimeByUuid(@PathVariable String uuid){
        return animeService.getAnimeByUuid(uuid);
    }

    @GetMapping("/title/{title}")
    public AnimeDTO getAnimeByTitle(@PathVariable String title){
        return animeService.getAnimeByTitle(title);
    }

    @GetMapping("/status/{status}")
    public List<AnimeDTO> getAnimeByStatus(@PathVariable String status){
        return  animeService.getAnimeByStatus(status);
    }

    @GetMapping("/sequels/{uuid}")
    public List<AnimeDTO> getSequels(@PathVariable String uuid){
        return animeService.getSequels(uuid);
    }

    @GetMapping("/spinoffs/{uuid}")
    public List<AnimeDTO>getSpinOffs(@PathVariable String uuid) {
        return animeService.getSpinOffs(uuid);
    }

    @GetMapping("/prequel/{uuid}")
    public AnimeDTO getPrequel(@PathVariable String uuid){
        return animeService.getPrequel(uuid);
    }

    @DeleteMapping
    public void deleteAll(){
        animeService.deleteAllAnime();
    }

    @DeleteMapping("/{uuid}")
        public void deleteByUuid(@PathVariable String uuid){
        animeService.deleteByUuid(uuid);
    }

    @PutMapping("/{uuid}")
    public void editAnimeByUuid( @RequestBody AnimeDTO anime,String uuid){
        animeService.editAnimeByUuid(anime, uuid);
    }

    @PostMapping
    public void insertAnime(@RequestBody AnimeDTO anime){
        animeService.insertAnime(anime);
    }

    @PostMapping("/MAL/{username}")
    public void importMalByUser(@PathVariable String username){
        animeService.importMalByUser(username);
    }
    @PostMapping("/multiple")
    public void insertMultipleAnime(@RequestBody List<AnimeDTO> animeDTOList){
        animeService.insertMultipleAnime(animeDTOList);
    }

}
