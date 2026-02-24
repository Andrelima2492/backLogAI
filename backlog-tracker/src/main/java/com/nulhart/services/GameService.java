package com.nulhart.services;

import com.nulhart.dto.GameDTO;
import com.nulhart.model.Game;
import com.nulhart.repository.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

        public List<GameDTO> getAllGames(){
            return gameRepository.findAll().stream().map(this::mapToDTO).toList();
        }

    public void insertGame(GameDTO game) {
        LocalDate dateOfCompletion = null;
        if(null != game.getDateOfCompletion()){
            dateOfCompletion = game.getDateOfCompletion();
        }
        gameRepository.save( mapFromDTO(game));
    }

    public GameDTO getGameByTitle(String title) {
        Optional<Game> game= Optional.of((Game) gameRepository.findGameByTitleIs(title).orElseThrow(
                ()-> new IllegalStateException(title+"not found")));

        return mapToDTO(game.get());
    }

    public GameDTO getGameById(Long id) {
        Optional<Game> game = Optional.of(gameRepository.findById(id).orElseThrow(()->
                new IllegalStateException(id+" not found")));
        return mapToDTO(game.get());
    }

    public List<GameDTO> getGamesByConsole(String console) {
        return gameRepository.findGameByConsole(console).stream().map(this::mapToDTO).toList();
    }

    public List<GameDTO> getGamesByStatus(String status) {
        return gameRepository.findGameByStatus(status).stream().map(this::mapToDTO).toList();
    }

    public void deleteGameById(Long id) {
        gameRepository.deleteById(id);
    }

    public void deleteAllGames() {
        gameRepository.deleteAll();
    }

    public void deleteGameByTitle(String title) {
        gameRepository.deleteGameByTitle(title);
    }

    public void insertGames(List<GameDTO> games) {
        gameRepository.saveAll(games.stream().map(this::mapFromDTO).toList());
    }
    @Transactional
    public void editGameById(GameDTO game, Long id)  {
        Optional<Game> entity = Optional.of(gameRepository.findById(id).orElseThrow(()->
                new IllegalStateException(id+" not found")));
        Game gameEntity = entity.get();
        gameEntity.setConsole(game.getConsole());
        gameEntity.setOpinion(game.getOpinion());
        gameEntity.setStatus(game.getStatus());
        gameEntity.setHoursPlayed(game.getHoursPlayed());
        gameEntity.setStartDate(game.getStartDate());
        gameEntity.setDateOfCompletion(game.getDateOfCompletion());
    }

    @Transactional
    public void editGameByTitle(GameDTO game, String title) {
        Game entity= Optional.of((Game) gameRepository.findGameByTitleIs(title).orElseThrow(
                ()-> new IllegalStateException(title+"not found"))).get();
        entity.setConsole(game.getConsole());
        entity.setOpinion(game.getOpinion());
        entity.setStatus(game.getStatus());
        entity.setHoursPlayed(game.getHoursPlayed());
        entity.setStartDate(game.getStartDate());
        entity.setDateOfCompletion(game.getDateOfCompletion());
        gameRepository.save(entity);
    }

    private GameDTO mapToDTO(Game game){
        return new GameDTO(game.getTitle(), game.getConsole(), game.getStatus(), game.getHoursPlayed(),
                game.getOpinion(), game.getStartDate(), game.getDateOfCompletion());
    }

    private Game mapFromDTO(GameDTO game){
        return new Game(game.getTitle(), game.getConsole(), game.getStatus(), game.getHoursPlayed(), game.getOpinion(),
                game.getStartDate(), game.getDateOfCompletion());
    }


}
