package com.nulhart.services;

import com.nulhart.dto.anime.*;
import com.nulhart.exceptions.anime.AnimeNotFoundException;
import com.nulhart.model.Anime;
import com.nulhart.myanimelist.MALClient;
import com.nulhart.repository.AnimeRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AnimeService {
    private AnimeRepository animeRepository;
    private MALClient malClient;

    public List<AnimeDTO> getAllAnime() {
        return animeRepository.findAll().stream().map(this::convertAnimeToDTO).toList();
    }

    @Transactional
    public void insertAnime(AnimeDTO anime) {
        MALAnimeResponse animeResponse = malClient.getMALAnime(anime.getTitle(), 1);
        Anime animeEntity = new Anime(anime.getTitle(), anime.getStatus(), anime.getEpisodesWatched(),
                anime.getScore());
        if(animeResponse!=null ){
            List<MALAnimeNode> animeNodes =  animeResponse.malAnimeNodeList();
            MALImage image;
            for(MALAnimeNode node : animeNodes){
                if(anime.getTitle().equals(node.title())) {
                    image = node.main_picture();
                    animeEntity.setMalId(node.id());
                    animeEntity.setImage(image.medium());
                    break;
                }
            }

            if(anime.getMalId()!= null) {
                MALDetailsNode details = malClient.getAnimeDetails(anime.getMalId(), "start_date,end_date" +
                        "num_episodes,related_anime");
                animeEntity.setStartDate(details.start_date());
                animeEntity.setEndDate(details.end_date());
                animeEntity.setNumberOfEpisodes(details.num_episodes());
                animeRepository.save(animeEntity);
                //take care of spinoffs and prequel and sequels
                List<RelatedMALDTO> relatedMALDTO = details.related_anime();
                List<Anime> sequels = new ArrayList<>();
                List<Anime> spinOffs = new ArrayList<>();
                Anime parent;
                for(RelatedMALDTO relatedMAL : relatedMALDTO){
                    Anime sequelEntity;
                    Anime spinOffEntity;
                    MALDetailsNode relatedDetails = malClient.getAnimeDetails(relatedMAL.node().id(),
                            "start_date,end_date" +
                            "num_episodes,related_anime");
                    if("sequel".equals(relatedMAL.relation_type()) ){
                        if(animeRepository.existsByMalId(relatedMAL.node().id())){
                            sequelEntity = animeRepository.getAnimeByMalId(relatedMAL.node().id());

                            sequels.add(sequelEntity);
                        }else{
                            sequelEntity = new Anime(relatedMAL.node().title(), "plan_to_watch",
                                    0,null);
                            sequelEntity.setMalId(relatedMAL.node().id());
                            sequelEntity.setImage(relatedMAL.node().main_picture().medium());
                            sequelEntity.setNumberOfEpisodes(relatedDetails.num_episodes());
                            sequelEntity.setParent(animeEntity);
                            sequelEntity.setStartDate(relatedDetails.start_date());
                            sequelEntity.setEndDate(relatedDetails.end_date());
                            sequels.add(sequelEntity);
                            AnimeDTO sequelToAddDTO = convertAnimeToDTO(sequelEntity);
                            insertAnime(sequelToAddDTO);
                        }
                    }else if("prequel".equals(relatedMAL.relation_type())){
                        if(animeRepository.existsByMalId(relatedMAL.node().id())){
                            parent = animeRepository.getAnimeByMalId(relatedMAL.node().id());
                            animeEntity.setParent(parent);
                        }else{
                            parent = new Anime(relatedMAL.node().title(),
                                    "plan_to_watch", 0, null);
                            parent.setMalId(relatedMAL.node().id());
                            parent.setImage(relatedMAL.node().main_picture().medium());
                            parent.setNumberOfEpisodes(relatedDetails.num_episodes());
                            parent.setStartDate(relatedDetails.start_date());
                            parent.setEndDate(relatedDetails.end_date());
                            AnimeDTO prequelToAdd = convertAnimeToDTO(parent);
                            insertAnime(prequelToAdd);
                            animeEntity.setParent(parent);
                        }
                    }if("side_story".equals(relatedMAL.relation_type())||
                            "spin-off".equals(relatedMAL.relation_type())&& animeEntity.getStatus().equals("dropped")){
                        if(animeRepository.existsByMalId(relatedMAL.node().id())){
                            spinOffEntity = animeRepository.getAnimeByMalId(relatedMAL.node().id());
                            spinOffs.add(spinOffEntity);
                        }else{
                            spinOffEntity = new Anime(relatedMAL.node().title(), "plan_to_watch",0,null);
                            spinOffEntity.setMalId(relatedMAL.node().id());
                            spinOffEntity.setImage(relatedMAL.node().main_picture().medium());
                            spinOffEntity.setNumberOfEpisodes(relatedDetails.num_episodes());
                            spinOffEntity.setStartDate(relatedDetails.start_date());
                            spinOffEntity.setEndDate(relatedDetails.end_date());
                            AnimeDTO spinoffToAdd =convertAnimeToDTO(spinOffEntity);
                            insertAnime(spinoffToAdd);
                            spinOffs.add(spinOffEntity);
                        }
                    }
                }
                animeEntity.setSequel(sequels);
                animeEntity.setSpinOff(spinOffs);
                animeRepository.save(animeEntity);
            }

        }
    }

    public AnimeDTO getAnimeByUuid(String uuid) {
        Anime animeEntity = animeRepository.findById(uuid).orElseThrow(()->
                new AnimeNotFoundException("Anime not found with Id: "+uuid));
        return convertAnimeToDTO(animeEntity);
    }

    public AnimeDTO getAnimeByTitle(String title) {
       Anime animeEntity=  animeRepository.findAnimeByTitleIs(title).orElseThrow(()->
                new AnimeNotFoundException("Anime not found with title: "+title));
        return convertAnimeToDTO(animeEntity);
    }

    @Transactional
    public void insertMultipleAnime(List<AnimeDTO> animeDTOList) {
      for(AnimeDTO animeDTO: animeDTOList){
          if(!animeRepository.existsByMalId(animeDTO.getMalId())){
              insertAnime(animeDTO);
          }
      }
    }


    private AnimeDTO convertAnimeToDTO(Anime anime){
        List<RelatedDTO> sequelResults = new ArrayList<>();
        List<RelatedDTO> spinOffResults = new ArrayList<>();
        RelatedDTO parentAnime = null;
        for(Anime sequel : anime.getSequel()){
           RelatedDTO sequelDTO = new RelatedDTO(sequel.getTitle(),
                   sequel.getStatus(), sequel.getNumberOfEpisodes(),sequel.getEpisodesWatched());
           sequelResults.add(sequelDTO);
        }
        for(Anime spinOff : anime.getSpinOff()){
           RelatedDTO spinOffDTO = new RelatedDTO(spinOff.getTitle(), spinOff.getStatus(),
                   spinOff.getNumberOfEpisodes(),
                   spinOff.getEpisodesWatched());
           spinOffResults.add(spinOffDTO);
        }
        if(anime.getParent() != null) {
            parentAnime = new RelatedDTO(anime.getParent().getTitle(),anime.getParent().getStatus(),
                    anime.getParent().getNumberOfEpisodes(), anime.getParent().getEpisodesWatched());
        }
       return new AnimeDTO(anime.getUUID(),anime.getTitle(), anime.getStatus(),anime.getNumberOfEpisodes(),
                anime.getEpisodesWatched(), anime.getScore(),anime.getImage(),
                sequelResults, spinOffResults,parentAnime, anime.getMalId(), anime.getStartDate(), anime.getEndDate());

    }

    public List<AnimeDTO> getAnimeByStatus(String status) {
        return animeRepository.findAnimeByStatus(status).stream().map(this::convertAnimeToDTO).toList();
    }

    public List<AnimeDTO> getSequels(String uuid) {
        List<AnimeDTO> results = new ArrayList<>();
        AnimeDTO animeDTO = getAnimeByUuid(uuid);
       return  extractAllFomRelatedList(animeDTO.getSequels());
    }

    public List<AnimeDTO> getSpinOffs(String uuid) {
        List<AnimeDTO> results = new ArrayList<>();
        AnimeDTO animeDTO = getAnimeByUuid(uuid);
        return extractAllFomRelatedList(animeDTO.getSpinOffs());
    }

    private List<AnimeDTO> extractAllFomRelatedList(List<RelatedDTO> list){
        List<AnimeDTO> results = new ArrayList<>();
        for(RelatedDTO relatedDTO : list){
            if(animeRepository.existsAnimeByTitle(relatedDTO.title())) {
                results.add(getAnimeByTitle(relatedDTO.title()));
            }
        }
        return results;

    }

    public AnimeDTO getPrequel(String uuid) {
        AnimeDTO anime = getAnimeByUuid(uuid);
        return getAnimeByTitle(anime.getParent().title());
    }

    public void deleteAllAnime() {
        animeRepository.deleteAll();
    }

    public void deleteByUuid(String uuid) {
       animeRepository.findById(uuid).orElseThrow(()->
                new AnimeNotFoundException("No anime found with id: "+uuid));
        animeRepository.deleteById(uuid);
    }

    @Transactional
    public void editAnimeByUuid(AnimeDTO anime, String uuid) {
        Anime animeEntity = animeRepository.findById(uuid).orElseThrow(
                ()-> new AnimeNotFoundException("No anime exists with id: "+uuid));
        animeEntity.setTitle(anime.getTitle());
        animeEntity.setStatus(anime.getStatus());
        animeEntity.setEpisodesWatched(anime.getEpisodesWatched());
        animeEntity.setNumberOfEpisodes(anime.getNumberOfEpisodes());
        animeEntity.setScore(anime.getScore());
        animeEntity.setMalId(anime.getMalId());
        animeEntity.setImage(anime.getImage());
        animeEntity.setStartDate(anime.getStartDate());
        animeEntity.setEndDate(anime.getEndDate());
        if(anime.getStatus().equals("watching")){
            MALDetailsNode malDetailsNode = malClient.getAnimeDetails(anime.getMalId(),"related_anime");
            for(RelatedMALDTO related:malDetailsNode.related_anime()){
                if(!animeRepository.existsByMalId(related.node().id())){
                    MALDetailsNode relatedDetails = malClient.getAnimeDetails(related.node().id(),
                            "start_date,end_date,num_episodes");
                    if("sequel".equals(related.relation_type())){
                        Anime sequel = new Anime(related.node().title(),"plan_to_watch",null,null);
                        sequel.setParent(animeEntity);
                        sequel.setNumberOfEpisodes(relatedDetails.num_episodes());
                        sequel.setImage(related.node().main_picture().medium());
                        sequel.setMalId(related.node().id());
                        sequel.setStartDate(relatedDetails.start_date());
                        sequel.setEndDate(relatedDetails.end_date());
                        if(animeEntity.getSequel() != null){
                            animeEntity.getSequel().add(sequel);
                        }else{
                            List<Anime> sequels = new ArrayList<>();
                            sequels.add(sequel);
                            animeEntity.setSequel(sequels);
                        }
                        animeRepository.save(sequel);
                    } else if ("prequel".equals(related.relation_type())) {
                        Anime prequel = new Anime(related.node().title(),"plan_to_watch",null,null);
                        List<Anime> prequelSequels = new ArrayList<>();
                        prequelSequels.add(animeEntity);
                        prequel.setSequel(prequelSequels);
                        prequel.setImage(related.node().main_picture().medium());
                        prequel.setMalId(related.node().id());
                        prequel.setNumberOfEpisodes(relatedDetails.num_episodes());
                        prequel.setStartDate(relatedDetails.start_date());
                        prequel.setEndDate(relatedDetails.end_date());
                        animeEntity.setParent(prequel);
                        animeRepository.save(prequel);
                    } else if ("spin-off".equals(related.relation_type()) || ("side_story".equals(related.relation_type()))
                    ||"character".equals(related.relation_type())){
                        Anime spinoff = new Anime(related.node().title(),
                                "plan_to_watch", null ,null);
                        spinoff.setParent(animeEntity);
                        spinoff.setMalId(related.node().id());
                        spinoff.setImage(related.node().main_picture().medium());
                        spinoff.setNumberOfEpisodes(relatedDetails.num_episodes());
                        spinoff.setStartDate(relatedDetails.start_date());
                        spinoff.setEndDate(relatedDetails.end_date());
                        if(animeEntity.getSpinOff()!= null){
                            animeEntity.getSpinOff().add(spinoff);
                        }else{
                            List<Anime> spinoffs = new ArrayList<>();
                            spinoffs.add(spinoff);
                        }
                        animeRepository.save(spinoff);
                    }
                }
            }
        }
        }

        @Transactional
    public void importMalByUser(String username) {
        String[] statuses= {"watching","on_hold","dropped","plan_to_watch","completed" };
        for(int i = 0; i<statuses.length;i++){
            int limit = 500;
            int offset =0;
            MALUserListResponse malUserListResponse = malClient.importMAL(username,
                    statuses[i],limit,offset );
            List<MALUserListDTO> malUserListDTOS = malUserListResponse.data();
            for(MALUserListDTO userListDTO : malUserListDTOS){
                Anime anime;  MALDetailsNode detailsNode = malClient.getAnimeDetails(userListDTO.node().id(),
                        "start_date,end_date,num_episodes,related_anime");
                if(animeRepository.existsByMalId(userListDTO.node().id())){
                     anime = animeRepository.getAnimeByMalId(userListDTO.node().id());
                    anime.setImage(userListDTO.node().main_picture().medium());
                    anime.setStatus(userListDTO.list_status().status());
                    anime.setScore(userListDTO.list_status().score());
                    anime.setEpisodesWatched(userListDTO.list_status().num_watched_episodes());
                    if(anime.getNumberOfEpisodes() == null || anime.getEndDate() == null) {
                        anime.setNumberOfEpisodes(detailsNode.num_episodes());
                        anime.setEndDate(detailsNode.end_date());
                    }
                }else {

                     anime = new Anime(userListDTO.node().title(),
                            userListDTO.list_status().status(), userListDTO.list_status().num_watched_episodes(),
                            userListDTO.list_status().score());
                    anime.setImage(userListDTO.node().main_picture().medium());
                    anime.setNumberOfEpisodes(detailsNode.num_episodes());
                    anime.setStartDate(detailsNode.start_date());
                    anime.setEndDate(detailsNode.end_date());
                    anime.setMalId(userListDTO.node().id());
                }
                    List<Anime> sequels = new ArrayList<>();
                    List<Anime> spinOffs = new ArrayList<>();
                    for(RelatedMALDTO relatedMALDTO :detailsNode.related_anime()){
                        MALDetailsNode relatedDetails = malClient.getAnimeDetails(relatedMALDTO.node().id(),
                                "end_date,start_date,num_episodes");
                        if("sequel".equals(relatedMALDTO.relation_type())) {
                            if(animeRepository.existsByMalId(relatedMALDTO.node().id())) {
                                Anime sequel = animeRepository.getAnimeByMalId(relatedMALDTO.node().id());
                                sequel.setParent(anime);
                                sequels.add(sequel);
                                animeRepository.save(sequel);
                            }else{
                                Anime sequel = new Anime(relatedMALDTO.node().title(),
                                        "plan_to_watch",
                                        null,
                                        null);
                                sequel.setParent(anime);
                                sequel.setImage(relatedMALDTO.node().main_picture().medium());
                                sequel.setNumberOfEpisodes(relatedDetails.num_episodes());
                                sequel.setStartDate(relatedDetails.start_date());
                                sequel.setEndDate(relatedDetails.end_date());
                                sequel.setMalId(relatedMALDTO.node().id());
                                sequels.add(sequel);
                                animeRepository.save(sequel);
                            }
                        }else if("prequel".equals(relatedMALDTO.relation_type())){
                            if(animeRepository.existsByMalId(relatedMALDTO.node().id())){
                                Anime prequel = animeRepository.getAnimeByMalId(relatedMALDTO.node().id());
                                prequel.getSequel().add(anime);
                                anime.setParent( prequel);
                                animeRepository.save(prequel);
                            }else {
                                Anime prequel =  new Anime(relatedMALDTO.node().title(),
                                        "plan_to_watch",null,null);
                                List<Anime> prequelSequel = new ArrayList<>();
                                prequelSequel.add(anime);
                                prequel.setSequel(prequelSequel);
                                anime.setParent(prequel);
                                prequel.setImage(relatedMALDTO.node().main_picture().medium());
                                prequel.setNumberOfEpisodes(relatedDetails.num_episodes());
                                prequel.setStartDate(relatedDetails.start_date());
                                prequel.setEndDate(relatedDetails.end_date());
                                prequel.setMalId(relatedMALDTO.node().id());
                                animeRepository.save(prequel);
                            }
                        }else if("spin-off".equals(relatedMALDTO.relation_type())||
                        "side_story".equals(relatedMALDTO.relation_type())||"character".equals(
                                relatedMALDTO.relation_type())){
                            if(animeRepository.existsByMalId(relatedMALDTO.node().id())){
                                Anime spinoff = animeRepository.getAnimeByMalId(relatedMALDTO.node().id());
                                spinoff.setParent(anime);
                                spinOffs.add(spinoff);
                                animeRepository.save(spinoff);
                            }else{
                                Anime spinoff = new Anime(relatedMALDTO.node().title(),
                                        "plan_to_watch",null, null);
                                spinoff.setImage(relatedMALDTO.node().main_picture().medium());
                                spinoff.setNumberOfEpisodes(relatedDetails.num_episodes());
                                spinoff.setStartDate(relatedDetails.start_date());
                                spinoff.setEndDate(relatedDetails.end_date());
                                spinoff.setMalId(relatedMALDTO.node().id());
                                spinoff.setParent(anime);
                                spinOffs.add(spinoff);
                                animeRepository.save(spinoff);
                            }
                        }
                    }
                    anime.setSpinOff(spinOffs);
                    anime.setSequel(sequels);
                    animeRepository.save(anime);
                }

            while(!malUserListResponse.paging().next().isBlank()){
                limit = limit +500;
                offset = offset+500;
                 malUserListResponse = malClient.importMAL(username,
                        statuses[i],limit,offset );
            }

        }
    }
}



