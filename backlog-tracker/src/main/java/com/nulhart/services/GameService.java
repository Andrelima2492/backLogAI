package com.nulhart.services;

import com.nulhart.dto.game.*;
import com.nulhart.exceptions.game.GameNotFoundException;
import com.nulhart.model.Game;
import com.nulhart.rawg.RawgClient;
import com.nulhart.repository.GameRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final RawgClient rawgClient;
    private final OpenAIService openAIService;


        public Page<GameDTO> getAllGames(int page, int size){
            Pageable pageable = PageRequest.of(page, size);
            Page<Game> gamePage = gameRepository.findAll(pageable);
            return gamePage.map(this::mapToDTO);
        }

        public void insertGame(GameDTO game) {
        if(gameRepository.existsByTitle(game.getTitle())){
            throw new DataIntegrityViolationException("a game already exists with title "+ game.getTitle());
        }
        RawgResponse response = rawgClient.searchGames(game.getTitle(),1,1);
        RawgDTO rawgDTO = response.results().get(0);
        Game gameEntity = mapFromDTO(game);
        Set<Game> additions = new HashSet<>();
        gameEntity.setAdditions(additions);
        if(rawgDTO != null){
            gameEntity.setEstimatedPlayTime(rawgDTO.playtime());
            gameEntity.setImage(rawgDTO.background_image());
            gameEntity.setRawgId(rawgDTO.id());
        }

        RawgResponse dlcResponse = rawgClient.getDLCs(gameEntity.getRawgId().toString(),1,5);
            for(RawgDTO dlcDTO : dlcResponse.results()){
                AdditionDTO dlc = new AdditionDTO(dlcDTO.name(), game.getConsole(), "not purchased",0,"",null,null,
                       dlcDTO.playtime(), dlcDTO.background_image(),game.getTitle());
                Game dlcEntity = mapGameFromAdditionDTO(dlc);
                dlcEntity.setParentGame(gameEntity);
                dlcEntity.setRawgId(dlcDTO.id());
                gameEntity.getAdditions().add(dlcEntity);

            }
            gameEntity.setTags(openAIService.getTags(gameEntity));
            gameRepository.save( gameEntity);

    }

    public GameDTO getGameByTitle(String title) {
        Optional<Game> game= Optional.of((Game) gameRepository.findGameByTitleIs(title).orElseThrow(
                ()-> new GameNotFoundException("Game with title "+ title +" was not found")));

        return mapToDTO(game.get());
    }

    public GameDTO getGameById(String id) {
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

    @Transactional
    public void deleteGameById(String id) {
        if(!gameRepository.existsById(id)){
            throw  new GameNotFoundException("No Game was found with id "+id);
        }
        gameRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllGames() {
        gameRepository.deleteAll();
    }
    @Transactional
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
        for(GameDTO gdto :newGamesToAdd ){
            this.insertGame(gdto);
        }
    }
    @Transactional
    public void editGameById(GameDTO game, String id)  {
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


    public List<GameDTO> getDLCs(Long rawgId){
            List<GameDTO> dlcs = new ArrayList<>();
            RawgResponse dlcResponse = rawgClient.getDLCs(rawgId.toString(),1,5);
            for(RawgDTO rawg:dlcResponse.results()){
              GameDTO dlcDTO = getGameByRawgId(rawg.id());
              dlcs.add(dlcDTO);
            }
            return dlcs;
    }
    private GameDTO mapToDTO(Game game){
            if (game==null){
                return null;
            }
            String titleParent="";
            if(game.getParentGame()!= null){
                titleParent=game.getParentGame().getTitle();
            }
        return new GameDTO(game.getUUID(),game.getTitle(), game.getConsole(), game.getStatus(), game.getHoursPlayed(),
                game.getOpinion(), game.getStartDate(), game.getDateOfCompletion(), game.getEstimatedPlayTime(),
                game.getImage(),titleParent, mapAdditionListToDTO(game.getAdditions()), game.getTags());
    }
    private AdditionDTO mapAdditionToDTO(Game game){
            if(game== null){
                return null;
            }
            return new AdditionDTO(game.getTitle(), game.getConsole(), game.getStatus(),game.getHoursPlayed(), game.getOpinion(),
                    game.getStartDate(), game.getDateOfCompletion(),
                    game.getEstimatedPlayTime(), game.getImage(),game.getParentGame().getTitle());
    }

    private Game mapGameFromAdditionDTO(AdditionDTO addition){
            if(addition==null){
                return null;
            }
            return new Game(addition.getTitle(),addition.getConsole(), addition.getStatus(), addition.getHoursPlayed(),
                    addition.getOpinion(),addition.getStartDate(), addition.getDateOfCompletion(), addition.getEstimatedPlayTime(),
                    addition.getImage());
    }

    private Set<AdditionDTO> mapAdditionListToDTO(Set<Game> additions){
        Set<AdditionDTO> additionDTOS = new HashSet<>();
        for(Game g : additions){
            AdditionDTO dto = mapAdditionToDTO(g);
            additionDTOS.add(dto);
        }
        return additionDTOS;
    }

    private Game mapFromDTO(GameDTO game){
        if (game==null){
            return null;
        }

        return new Game(game.getTitle(),
                game.getConsole(), game.getStatus(), game.getHoursPlayed(), game.getOpinion(),
                game.getStartDate(), game.getDateOfCompletion(), game.getEstimatedPlayTime(),
                game.getImage());
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

    private GameDTO createGameDTOFromSuggestionDTO(SuggestionDTO suggestionDTO){
            return new GameDTO(null,suggestionDTO.title(),suggestionDTO.console(),"not purchased", 0, "", null, null,
                    null,"", "", null, null);
    }

    private List<GameDTO> createGameDTOListFromSuggestionDTOList(List<SuggestionDTO> suggestionDTOS){
            List<GameDTO> results = new ArrayList<>();
            for (SuggestionDTO suggestionDTO: suggestionDTOS){
                results.add(createGameDTOFromSuggestionDTO(suggestionDTO));
            }
            return results;
    }


    public List<GameDTO> getLastFiveCompleted() {
            List<Game> lastCompletedEntities = gameRepository.findTop5ByDateOfCompletionNotNullOrderByDateOfCompletionDesc();
         return this.mapListToDTO(lastCompletedEntities);
    }

    public GameDTO getGameByRawgId(Long id) {
          Optional<Game> gameEntity =  Optional.of((Game) gameRepository.findGameByRawgId(id).orElseThrow(
                  ()-> new GameNotFoundException("No Game found with rawgid "+id)));
          return mapToDTO(gameEntity.get());


    }
}
