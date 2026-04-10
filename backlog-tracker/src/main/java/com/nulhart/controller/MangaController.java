package com.nulhart.controller;

import com.nulhart.dto.manga.MangaDTO;
import com.nulhart.services.MangaService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/manga")
@AllArgsConstructor
public class MangaController {
    private MangaService mangaService;

@GetMapping
    public Page<MangaDTO> getAllManga(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
){
    return mangaService.getAllManga(page, size);
}

@GetMapping("/id/{id}")
    public MangaDTO getMangaById(@PathVariable String id){
    return mangaService.getMangaById(id);
}

@GetMapping("/status/{status}")
    public Set<MangaDTO> getMangaByStatus(@PathVariable String status){
    return mangaService.getMangaByStatus(status);
}

@GetMapping("/MAL/id/{id}")
    public MangaDTO getMangaByMALId(@PathVariable Integer id){
    return mangaService.getMangaByMALId(id);
}

@GetMapping("/sequels/{id}")
    public Set<MangaDTO> getSequels(@PathVariable String id){
    return  mangaService.getSequels(id);
    }

    @GetMapping("/parent/{id}")
    public MangaDTO getParent(@PathVariable String id){
    return mangaService.getParent(id);
    }

    @DeleteMapping
    public void deleteAllManga(){
        mangaService.deleteAllManga();
    }

    @DeleteMapping("/id/{id}")
    public void deleteById(@PathVariable String id){
        mangaService.deleteById(id);
    }

    @PutMapping("/id/{id}")
    public void editMangaById(@RequestBody MangaDTO manga, @PathVariable String id){
        mangaService.editMangaById(manga, id);
        }

        @PostMapping("/MAL/user/{username}")
    public ResponseEntity<String> importByMALUser(@PathVariable String username){
            mangaService.importMAL(username);
            return ResponseEntity.ok("request started");
        }
}
