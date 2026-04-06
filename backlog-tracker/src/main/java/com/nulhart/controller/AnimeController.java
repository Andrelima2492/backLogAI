package com.nulhart.controller;

import com.nulhart.dto.anime.AnimeDTO;
import com.nulhart.services.AnimeService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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

    @GetMapping("/MAL/id/{id}")
    public AnimeDTO getAnimeByMALId(@PathVariable Integer id){
        return animeService.getAnimeByMalId(id);
    }

    @GetMapping("/sequels/{uuid}")
    public Set<AnimeDTO> getSequels(@PathVariable String uuid){
        return animeService.getSequels(uuid);
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
    public void editAnimeByUuid( @RequestBody AnimeDTO anime,@PathVariable String uuid){
        animeService.editAnimeByUuid(anime, uuid);
    }



    @PostMapping("/MAL/{username}")
    public ResponseEntity<String> importMalByUser(@PathVariable String username){
        animeService.importMalByUser(username);
        return  ResponseEntity.ok("Request started");
    }


    @GetMapping("/auth/mal/login")
    public void login(HttpServletResponse response, HttpSession session) throws IOException {
        animeService.login(response, session);
    }

}
