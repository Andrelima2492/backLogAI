package com.nulhart.services;

import com.nulhart.dto.GameDTO;
import com.nulhart.model.Game;
import com.nulhart.repository.GameRepository;
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

    public GameDTO getGameByTitle(String title) throws ChangeSetPersister.NotFoundException {
        Optional<Game> game= Optional.of((Game) gameRepository.findGameByTitleIs(title).orElseThrow(
                ()-> new IllegalStateException(title+"not found")));

        return mapToDTO(game.get());
    }

    public GameDTO getGameById(Long id) throws ChangeSetPersister.NotFoundException {
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
    private GameDTO mapToDTO(Game game){
        return new GameDTO(game.getTitle(), game.getConsole(), game.getStatus(), game.getHoursPlayed(),
                game.getOpinion(), game.getStartDate(), game.getDateOfCompletion());
    }

    private Game mapFromDTO(GameDTO game){
        return new Game(game.getTitle(), game.getConsole(), game.getStatus(), game.getHoursPlayed(), game.getOpinion(),
                game.getStartDate(), game.getDateOfCompletion());
    }


}
