package com.nulhart.services;

import com.nulhart.dto.GameDTO;
import com.nulhart.dto.RawgDTO;
import com.nulhart.dto.RawgResponse;
import com.nulhart.exceptions.GameNotFoundException;
import com.nulhart.model.Game;
import com.nulhart.rawg.RawgClient;
import com.nulhart.repository.GameRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final RawgClient rawgClient;


        public List<GameDTO> getAllGames(){
            return gameRepository.findAll().stream().map(this::mapToDTO).toList();
        }
    public void insertGame(GameDTO game) {
        if(gameRepository.existsByTitle(game.getTitle())){
            throw new DataIntegrityViolationException("a game already exists with title "+ game.getTitle());
        }
        RawgResponse response = rawgClient.searchGames(game.getTitle(),1,1);
        RawgDTO rawgDTO = response.results().get(0);
        Game gameEntity = mapFromDTO(game);
        if(rawgDTO != null){
            gameEntity.setEstimatedPlayTime(rawgDTO.playtime());
            gameEntity.setImage(rawgDTO.background_image());
            gameEntity.setRawgId(rawgDTO.id());
        }

        gameRepository.save( gameEntity);
    }

    public GameDTO getGameByTitle(String title) {
        Optional<Game> game= Optional.of((Game) gameRepository.findGameByTitleIs(title).orElseThrow(
                ()-> new GameNotFoundException("Game with title "+ title +" was not found")));

        return mapToDTO(game.get());
    }

    public GameDTO getGameById(Long id) {
        Optional<Game> game = Optional.of(gameRepository.findById(id).orElseThrow(()->
                new  GameNotFoundException("Game with title "+ id +" was not found")));
        return mapToDTO(game.get());
    }

    public List<GameDTO> getGamesByConsole(String console) {
        return gameRepository.findGameByConsole(console).stream().map(this::mapToDTO).toList();
    }

    public List<GameDTO> getGamesByStatus(String status) {
        return gameRepository.findGameByStatus(status).stream().map(this::mapToDTO).toList();
    }

    public void deleteGameById(Long id) {
        if(!gameRepository.existsById(id)){
            throw  new GameNotFoundException("No Game was found with id "+id);
        }
        gameRepository.deleteById(id);
    }

    public void deleteAllGames() {
        gameRepository.deleteAll();
    }

    public void deleteGameByTitle(String title) {
       if(!gameRepository.existsByTitle(title)){
           throw new GameNotFoundException("No game exists with title "+title);
       }
        gameRepository.deleteGameByTitle(title);
    }

    public void insertGames(List<GameDTO> games) {
        List<GameDTO> newGamesToAdd = new ArrayList<GameDTO>();
        for(GameDTO g : games){
            if(!getAllGames().contains(g)){
                newGamesToAdd.add(g);
            }
        }
        gameRepository.saveAll(newGamesToAdd.stream().map(this::mapFromDTO).toList());
    }
    @Transactional
    public void editGameById(GameDTO game, Long id)  {
        Optional<Game> entity = Optional.of(gameRepository.findById(id).orElseThrow(()->
                new GameNotFoundException("No game exists with id "+id)));
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
                ()-> new GameNotFoundException("No game was found with title "+ title))).get();
        entity.setConsole(game.getConsole());
        entity.setOpinion(game.getOpinion());
        entity.setStatus(game.getStatus());
        entity.setHoursPlayed(game.getHoursPlayed());
        entity.setStartDate(game.getStartDate());
        entity.setDateOfCompletion(game.getDateOfCompletion());
        gameRepository.save(entity);
    }

    private GameDTO mapToDTO(Game game){
            if (game==null){
                return null;
            }
        return new GameDTO(game.getTitle(), game.getConsole(), game.getStatus(), game.getHoursPlayed(),
                game.getOpinion(), game.getStartDate(), game.getDateOfCompletion(), game.getEstimatedPlayTime(),
                game.getImage(),game.getRawgId(), mapListToDTO(game.getAdditions()), mapToDTO(game.getParentGame()));
    }

    private Game mapFromDTO(GameDTO game){
        if (game==null){
            return null;
        }
        return new Game(game.getTitle(),
                game.getConsole(), game.getStatus(), game.getHoursPlayed(), game.getOpinion(),
                game.getStartDate(), game.getDateOfCompletion(), game.getEstimatedPlayTime(),
                game.getImage(), game.getRawgId(), mapListFromDTO(game.getAdditions()), mapFromDTO(game.getParentGame()));
    }

    private List<GameDTO> mapListToDTO(List<Game> gameList){
        List<GameDTO> gameDTOList = new ArrayList<>();
            for(Game g : gameList){
                GameDTO gDTO = mapToDTO(g);
                gameDTOList.add(gDTO);
            }
            return gameDTOList;
    }

    private List<Game> mapListFromDTO(List<GameDTO> gameDTOList){
        List<Game> gameList = new ArrayList<>();
        for(GameDTO gDTO : gameDTOList){
            Game g = mapFromDTO(gDTO);
            gameList.add(g);
        }
        return gameList;
    }

}
