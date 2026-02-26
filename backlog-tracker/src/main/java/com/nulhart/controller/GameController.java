package com.nulhart.controller;

import com.nulhart.dto.GameDTO;
import com.nulhart.model.Game;
import com.nulhart.services.GameService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<GameDTO> getGames(){
        LocalDate startDateAC = LocalDate.parse("2025-11-21");
        LocalDate endDateAC = LocalDate.parse("2026-02-16");
        LocalDate startDateOP = LocalDate.parse("2026-02-18");
        return gameService.getAllGames();
    }

    @GetMapping("/title")
    public GameDTO getGameByTitle(@RequestParam String title)  {
        return gameService.getGameByTitle(title);
    }

    @GetMapping("{id}")
    public GameDTO getGameById(@PathVariable Long id) {
        return gameService.getGameById(id);
    }

    @GetMapping("/console/{console}")
    public List<GameDTO> getGamesByConsole(@PathVariable String console){
        return gameService.getGamesByConsole(console);
    }

    @GetMapping("/status/{status}")
    public List<GameDTO> getGamesByStatus(@PathVariable String status){
        return gameService.getGamesByStatus(status);
    }

    
  @GetMapping("/dlcs/{rawgId}")
  public List<GameDTO>  getDLCs(@PathVariable Long rawgId){
        return gameService.getDLCs(rawgId);
  }
    @PostMapping
    public void addNewGame(@RequestBody GameDTO game){
            gameService.insertGame(game);
    }
    @PostMapping("/multiple")
    public void addMultipleGames(@RequestBody List<GameDTO> games){
        gameService.insertGames(games);
    }

    @DeleteMapping("{id}")
    public void deleteGameById(@PathVariable Long id){
        gameService.deleteGameById(id);
    }
    @DeleteMapping
    public void deleteAllGames(){
        gameService.deleteAllGames();
    }

    @DeleteMapping("/title")
    public void deleteGameByTitle(@RequestParam String title){
        gameService.deleteGameByTitle(title);
    }

@PutMapping("/title")
public void editGameByTitle(@RequestBody GameDTO game, @RequestParam String title){
        gameService.editGameByTitle(game, title);
}

    @PutMapping("{id}")
    public void editGameById(@RequestBody GameDTO game, @PathVariable Long id){
        gameService.editGameById(game, id);
    }
}

