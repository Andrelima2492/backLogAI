package com.nulhart.services;

import com.nulhart.dto.anime.*;
import com.nulhart.exceptions.anime.AnimeNotFoundException;
import com.nulhart.exceptions.tokens.TokenNotFoundException;
import com.nulhart.model.Anime;
import com.nulhart.model.MALToken;
import com.nulhart.myanimelist.MALClient;
import com.nulhart.myanimelist.MALProperties;
import com.nulhart.repository.AnimeRepository;
import com.nulhart.repository.MALTokenRepository;
import com.nulhart.util.PKCEUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class AnimeService {
    private final MALProperties mALProperties;
    private AnimeRepository animeRepository;
    private MALClient malClient;
    private MALTokenRepository malTokenRepository;
    private final String regex = "^\\d{4}-\\d{2}-\\d{2}$";

    public List<AnimeDTO> getAllAnime() {
        return animeRepository.findAll().stream().map(this::convertAnimeToDTO).toList();
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


    public AnimeDTO getAnimeByMalId(Integer id){
        return convertAnimeToDTO(
                animeRepository.getAnimeByMalId(id).orElseThrow(()->new AnimeNotFoundException("" +
                        "No anime found with MAL id "+id)));


    }

    private AnimeDTO convertAnimeToDTO(Anime anime){
        Set<RelatedDTO> sequelResults = new HashSet<>();
        Set<RelatedDTO> spinOffResults = new HashSet<>();
        RelatedDTO parentAnime = null;
        for(Anime sequel : anime.getSequel()){
           RelatedDTO sequelDTO = new RelatedDTO(sequel.getTitle(),
                   sequel.getStatus(), sequel.getNumberOfEpisodes(),sequel.getEpisodesWatched());
           sequelResults.add(sequelDTO);
        }

        if(anime.getParent() != null) {
            parentAnime = new RelatedDTO(anime.getParent().getTitle(),anime.getParent().getStatus(),
                    anime.getParent().getNumberOfEpisodes(), anime.getParent().getEpisodesWatched());
        }
       return new AnimeDTO(anime.getId(),anime.getTitle(), anime.getStatus(),anime.getNumberOfEpisodes(),
                anime.getEpisodesWatched(), anime.getScore(),anime.getImage(),
                sequelResults,parentAnime, anime.getMalId(), anime.getStartDate(), anime.getEndDate());

    }

    public List<AnimeDTO> getAnimeByStatus(String status) {
        return animeRepository.findAnimeByStatus(status).stream().map(this::convertAnimeToDTO).toList();
    }

    public Set<AnimeDTO> getSequels(String uuid) {
        List<AnimeDTO> results = new ArrayList<>();
        AnimeDTO animeDTO = getAnimeByUuid(uuid);
       return  extractAllFomRelatedList(animeDTO.getSequels());
    }


    private Set<AnimeDTO> extractAllFomRelatedList(Set<RelatedDTO> list){
        Set<AnimeDTO> results = new HashSet<>();
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
            MALDetailsNode malDetailsNode = getDetailsSafe(anime.getMalId(),"related_anime");
            for(RelatedMALDTO related:malDetailsNode.related_anime()){
                if(!animeRepository.existsByMalId(related.node().id())){
                    MALDetailsNode relatedDetails = getDetailsSafe(related.node().id(),
                            "start_date,end_date,num_episodes");
                    if("sequel".equals(related.relation_type())
                    ||"side_story".equals(related.relation_type())||"spin_off".equals(related.relation_type())){
                        Anime sequel = new Anime(related.node().title(),"plan_to_watch",null,null);
                        sequel.setParent(animeEntity);
                        sequel.setNumberOfEpisodes(relatedDetails.num_episodes());
                        sequel.setImage(related.node().main_picture().medium());
                        sequel.setMalId(related.node().id());
                        if(relatedDetails.start_date()!= null && relatedDetails.start_date().matches(regex)){
                            LocalDate startDate = LocalDate.parse(relatedDetails.start_date());
                            sequel.setStartDate(startDate);
                        }else{
                            sequel.setStartDate(null);
                        }
                        if(relatedDetails.end_date()!= null && relatedDetails.end_date().matches(regex)){
                            LocalDate endDate = LocalDate.parse(relatedDetails.end_date());
                            sequel.setEndDate(endDate);
                        }else{
                            sequel.setEndDate(null);
                        }

                          animeEntity.getSequel().add(sequel);
                        animeRepository.save(sequel);
                    } else if ("prequel".equals(related.relation_type())) {
                        Anime prequel = new Anime(related.node().title(),"plan_to_watch",null,null);
                        prequel.getSequel().add(animeEntity);
                        prequel.setImage(related.node().main_picture().medium());
                        prequel.setMalId(related.node().id());
                        prequel.setNumberOfEpisodes(relatedDetails.num_episodes());
                        if(relatedDetails.start_date()!= null && relatedDetails.start_date().matches(regex)){
                            LocalDate startDate =  LocalDate.parse(relatedDetails.start_date());
                            prequel.setStartDate(startDate);
                        }else{
                            prequel.setStartDate(null);
                        }
                        if(relatedDetails.end_date()!= null && relatedDetails.end_date().matches(regex)){
                            LocalDate endDate = LocalDate.parse(relatedDetails.end_date());
                            prequel.setEndDate(endDate);
                        }else{
                            prequel.setEndDate(null);
                        }

                        animeEntity.setParent(prequel);
                        animeRepository.save(prequel);
                    }
                }
            }
        }
        }

        @Async
        @Transactional
    public void importMalByUser(String username) {
        System.out.println("Service Thread "+ Thread.currentThread().getName());
        com.nulhart.model.MALToken malToken = malTokenRepository.getMALTokenByUsername(username).orElseThrow(()->
                new TokenNotFoundException("Token for user  "+username +" doesnt exist"));
        String token = malToken.getAccess_token();
        String[] statuses= {"watching","on_hold","dropped","completed","plan_to_watch" };
        for(int i = 0; i<statuses.length;i++){
            boolean over = false;

                int limit = 500;
                int offset = 0;
                MALUserListResponse malUserListResponse = malClient.importMAL(username,
                        statuses[i], limit, offset, token);
            while(over == false) {
                throttle();
                List<MALUserListDTO> malUserListDTOS = malUserListResponse.data();
                for (MALUserListDTO userListDTO : malUserListDTOS) {
                    System.out.println("for anime " + userListDTO.node().title());
                    Anime anime;
                    MALDetailsNode detailsNode = getDetailsSafe(userListDTO.node().id(),
                            "start_date,end_date,num_episodes,related_anime");
                    throttle();
                    if (animeRepository.existsByMalId(userListDTO.node().id())) {
                        anime = animeRepository.getAnimeByMalId(userListDTO.node().id()).orElseThrow(
                                ()->new AnimeNotFoundException("No anime found with MAL id "+userListDTO.node().id())
                        );
                        anime.setImage(userListDTO.node().main_picture().medium());
                        anime.setStatus(userListDTO.list_status().status());
                        anime.setScore(userListDTO.list_status().score());
                        anime.setEpisodesWatched(userListDTO.list_status().numWatchedEpisodes());
                        if (anime.getNumberOfEpisodes() == null || anime.getEndDate() == null) {
                            anime.setNumberOfEpisodes(detailsNode.num_episodes());
                            if (detailsNode.end_date() != null && detailsNode.end_date().matches(regex)) {
                                LocalDate endDate = LocalDate.parse(detailsNode.end_date());
                                anime.setEndDate(endDate);
                            }
                        }
                    } else {

                        anime = new Anime(userListDTO.node().title(),
                                userListDTO.list_status().status(), userListDTO.list_status().numWatchedEpisodes(),
                                userListDTO.list_status().score());
                        anime.setImage(userListDTO.node().main_picture().medium());
                        anime.setNumberOfEpisodes(detailsNode.num_episodes());
                        if (detailsNode.start_date() != null && detailsNode.start_date().matches(regex)) {
                            LocalDate startDate = LocalDate.parse(detailsNode.start_date());
                            anime.setStartDate(startDate);
                        } else {
                            anime.setStartDate(null);
                        }
                        if (detailsNode.end_date() != null && detailsNode.end_date().matches(regex)) {
                            LocalDate endDate = LocalDate.parse(detailsNode.end_date());
                            anime.setEndDate(endDate);
                        } else {
                            anime.setEndDate(null);
                        }
                        anime.setMalId(userListDTO.node().id());
                    }
                    animeRepository.save(anime);
                    if (!"dropped".equals(userListDTO.list_status().status()) &&
                            !"plan_to_watch".equals(userListDTO.list_status().status())) {
                        for (RelatedMALDTO relatedMALDTO : detailsNode.related_anime()) {
                            if ("sequel".equals(relatedMALDTO.relation_type()) ||
                                    "prequel".equals(relatedMALDTO.relation_type()) || "spin_off".equals(relatedMALDTO.relation_type())
                                    || "side_story".equals(relatedMALDTO.relation_type())) {
                                System.out.println(" related " + relatedMALDTO.node().title());
                                MALDetailsNode relatedDetails = getDetailsSafe(relatedMALDTO.node().id(),
                                        "end_date,start_date,num_episodes");
                                throttle();
                                if ("sequel".equals(relatedMALDTO.relation_type())
                                ||"spin_off".equals(relatedMALDTO.relation_type())||
                                        "side_story".equals(relatedMALDTO.relation_type())) {
                                    if (animeRepository.existsByMalId(relatedMALDTO.node().id())) {
                                        Anime sequel = animeRepository.getAnimeByMalId(relatedMALDTO.node().id()).orElseThrow(
                                                ()->new AnimeNotFoundException("No anime found with id "+
                                                        relatedMALDTO.node().id())

                                        );
                                        sequel.setParent(anime);
                                        anime.getSequel().add(sequel);
                                        animeRepository.save(sequel);
                                    } else {
                                        Anime sequel = new Anime(relatedMALDTO.node().title(),
                                                "plan_to_watch",
                                                null,
                                                null);
                                        sequel.setParent(anime);
                                        sequel.setImage(relatedMALDTO.node().main_picture().medium());
                                        sequel.setNumberOfEpisodes(relatedDetails.num_episodes());
                                        if (relatedDetails.start_date() != null && relatedDetails.start_date().matches(regex)) {
                                            LocalDate startDate = LocalDate.parse(relatedDetails.start_date());
                                            sequel.setStartDate(startDate);
                                        } else {
                                            sequel.setStartDate(null);
                                        }
                                        if (relatedDetails.end_date() != null && relatedDetails.end_date().matches(regex)) {
                                            LocalDate endDate = LocalDate.parse(relatedDetails.end_date());
                                            sequel.setEndDate(endDate);
                                        } else {
                                            sequel.setEndDate(null);
                                        }
                                        sequel.setMalId(relatedMALDTO.node().id());
                                        anime.getSequel().add(sequel);
                                        animeRepository.save(sequel);
                                    }
                                } else if ("prequel".equals(relatedMALDTO.relation_type())) {
                                    if (animeRepository.existsByMalId(relatedMALDTO.node().id())) {
                                        Anime prequel = animeRepository.getAnimeByMalId(relatedMALDTO.node().id()).
                                                orElseThrow(()->new AnimeNotFoundException("No anime found with id "+relatedMALDTO.node().id()));
                                        prequel.getSequel().add(anime);
                                        anime.setParent(prequel);
                                        animeRepository.save(prequel);
                                    } else {
                                        Anime prequel = new Anime(relatedMALDTO.node().title(),
                                                "plan_to_watch", null, null);
                                        prequel.getSequel().add(anime);
                                        anime.setParent(prequel);
                                        prequel.setImage(relatedMALDTO.node().main_picture().medium());
                                        prequel.setNumberOfEpisodes(relatedDetails.num_episodes());
                                        if (relatedDetails.start_date() != null && relatedDetails.start_date().matches(regex)) {
                                            LocalDate startDate = LocalDate.parse(relatedDetails.start_date());
                                            prequel.setStartDate(startDate);
                                        } else {
                                            prequel.setStartDate(null);
                                        }
                                        if (relatedDetails.end_date() != null && relatedDetails.end_date().matches(regex)) {
                                            LocalDate endDate = LocalDate.parse(relatedDetails.end_date());
                                            prequel.setEndDate(endDate);
                                        } else {
                                            prequel.setEndDate(null);
                                        }
                                        prequel.setMalId(relatedMALDTO.node().id());
                                        animeRepository.save(prequel);
                                    }
                                }
                            }
                        }

                        animeRepository.save(anime);
                    }
                }
            if(malUserListResponse.paging().next()!= null) {

                offset = offset + 500;
                malUserListResponse = malClient.importMAL(username,
                        statuses[i], limit, offset, token);
                throttle();
            }else {
                over = true;
            }
            }
            }


    }

    public void login(HttpServletResponse response, HttpSession session) throws IOException {
        try {
            String verifier = PKCEUtil.generateCodeVerifier();
            session.setAttribute("mal_verifier", verifier);

            String url = "https://myanimelist.net/v1/oauth2/authorize"+
                    "?response_type=code"+
                    "&client_id="+mALProperties.getApiKey()+
                    "&state=xyz"+
                    "&redirect_uri=http://localhost:8080/auth/mal/callback"+
                    "&code_challenge="+verifier+
                    "&code_challenge_method=plain";
            response.sendRedirect(url);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void exchangeCodeForToken(String code, String verifier, HttpSession session) {
            MALTokenResponse tokenResponse = malClient.exchangeCodeForToken(code, verifier);
            MALToken malToken = new MALToken();
            malToken.setAccess_token(tokenResponse.access_token());
            malToken.setRefresh_token(tokenResponse.refresh_token());
            malToken.setExpires(tokenResponse.expires_in());
            malToken.setUsername("AndreLima92");
            malTokenRepository.save(malToken);
    }

    private void throttle(){
        try{
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public MALDetailsNode getDetailsSafe(Integer id, String fields){
        int retries = 3;
        for (int i =0; i < retries; i++){
            try{
                return malClient.getAnimeDetails(id,fields);
            }catch (ResourceAccessException e){
                System.out.println("Timout for anime "+ id +" retry "+ (i+1));
            }
            try{
                Thread.sleep(1500);

            }catch (InterruptedException ex){
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Skipping anime "+id +  " after retries");
        return null;
    }


}

