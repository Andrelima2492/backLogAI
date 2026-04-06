package com.nulhart.services;

import com.nulhart.dto.anime.MALDetailsNode;
import com.nulhart.dto.anime.RelatedMALDTO;
import com.nulhart.dto.manga.*;
import com.nulhart.exceptions.manga.MangaNotFoundException;
import com.nulhart.exceptions.tokens.TokenNotFoundException;
import com.nulhart.model.MALToken;
import com.nulhart.model.Manga;
import com.nulhart.myanimelist.MALClient;
import com.nulhart.myanimelist.MALProperties;
import com.nulhart.openai.OpenAIClient;
import com.nulhart.repository.MALTokenRepository;
import com.nulhart.repository.MangaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MangaService {
    private final MangaRepository mangaRepository;
    private final MALProperties malProperties;
    private final MALClient malClient;
    private final MALTokenRepository malTokenRepository;
    private final String regex = "^\\d{4}-\\d{2}-\\d{2}$";
    private final OpenAIService openAIService;


    public MangaDTO convertToDTO(Manga manga) {
        Set<RelatedMangaDTO> sequels = new HashSet<>();
        RelatedMangaDTO parent = null;
        for (Manga sequel : manga.getSequels()) {
            RelatedMangaDTO sequelDTO = new RelatedMangaDTO(sequel.getTitle(), sequel.getStatus(),
                    sequel.getNumberOfChapters(), sequel.getChaptersRead(), sequel.getNumberOfVolumes(), sequel.getVolumesRead()
                    , manga.getMalId());
            sequels.add(sequelDTO);
        }
        if (manga.getParent() != null) {
            parent = new RelatedMangaDTO(manga.getParent().getTitle(), manga.getParent().getStatus(),
                    manga.getParent().getNumberOfChapters(), manga.getParent().getChaptersRead(),
                    manga.getParent().getNumberOfVolumes(), manga.getParent().getVolumesRead(), manga.getMalId());
        }
        return new MangaDTO(manga.getId(), manga.getTitle(), manga.getStatus(), manga.getNumberOfChapters(), manga.getChaptersRead(),
                manga.getNumberOfVolumes(), manga.getVolumesRead(), manga.getScore(), manga.getStartDate(), manga.getEndDate(),
                manga.getMalId(), manga.getImage(), sequels, parent, manga.getTags());
    }

    public Set<MangaDTO> getAllManga() {
        return mangaRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toSet());
    }

    public MangaDTO getMangaById(String id) {
        return convertToDTO(mangaRepository.findById(id).orElseThrow(() ->
                new MangaNotFoundException("No manga found for id " + id)));
    }

    public Set<MangaDTO> getMangaByStatus(String status) {
        return mangaRepository.findMangaByStatus(status).stream().map(this::convertToDTO).collect(Collectors.toSet());
    }

    public MangaDTO getMangaByMALId(Integer id) {
        return convertToDTO(mangaRepository.getMangaByMalId(id).orElseThrow(() ->
                new MangaNotFoundException("No manga found for MAL id " + id + " either check your id or try to add it")));
    }

    public Set<MangaDTO> getSequels(String id) {
        Manga parent = mangaRepository.findById(id).orElseThrow(() -> new MangaNotFoundException(
                "No manga found with id " + id));
        return parent.getSequels().stream().map(this::convertToDTO).collect(Collectors.toSet());
    }

    public MangaDTO getParent(String id) {
        Manga current = mangaRepository.findById(id).orElseThrow(() ->
                new MangaNotFoundException("No manga found with id " + id));
        return convertToDTO(current.getParent());
    }

    public void deleteAllManga() {
        mangaRepository.deleteAll();
    }

    public void deleteById(String id) {
        Manga target = mangaRepository.findById(id).orElseThrow(() ->
                new MangaNotFoundException("No manga found with id " + id));
        mangaRepository.deleteById(id);
    }

    @Transactional
    public void editMangaById(MangaDTO manga, String id) {
        Manga mangaEntity = new Manga(manga.getTitle(), manga.getStatus());
        mangaEntity.setChaptersRead(manga.getChaptersRead());
        mangaEntity.setNumberOfChapters(manga.getNumberOfChapters());
        mangaEntity.setVolumesRead(manga.getVolumesRead());
        mangaEntity.setNumberOfVolumes(manga.getNumberOfVolumes());
        mangaEntity.setScore(manga.getScore());
        mangaEntity.setMalId(manga.getMalId());
        mangaEntity.setStartDate(manga.getStartDate());
        mangaEntity.setEndDate(manga.getEndDate());
        mangaEntity.setImage(manga.getImage());
        if("reading".equals(manga.getStatus()) && mangaEntity.getTags().isEmpty()){
            mangaEntity.setTags(openAIService.getTags(mangaEntity));
        }
        if (manga.getParent() != null) {
            Manga parent = mangaRepository.getMangaByMalId(manga.getParent().malId()).orElseThrow(
                    () -> new MangaNotFoundException("no manga found in system with mal id" + manga.getParent().malId())
            );
            mangaEntity.setParent(parent);
        }
        if (!manga.getSequels().isEmpty()) {
            Set<Manga> sequels = new HashSet<>();
            for (RelatedMangaDTO sequelDTO : manga.getSequels()) {
                Manga sequel = mangaRepository.getMangaByMalId(sequelDTO.malId()).orElseThrow(()
                        -> new MangaNotFoundException("sequel with mal id " + sequelDTO.malId()));
                sequels.add(sequel);
            }
        }
    }

    @Async
    @Transactional
    public void importMAL(String username) {
        MALToken malToken = malTokenRepository.getMALTokenByUsername(username).orElseThrow(() ->
                new TokenNotFoundException("There is no token for username " + username + "in database. Please login"));
        String token = malToken.getAccess_token();
        String[] statuses = {"reading", "on_hold", "dropped", "completed", "plan_to_read"};
        for (int i = 0; i < statuses.length; i++) {
            boolean over = false;
            int limit = 500;
            int offset = 0;

            MALUserListMangaResponse malMangaResponse = malClient.importManga(username, statuses[i],
                    limit, offset, token);
            while (!over) {
                throttle();
                List<MALUserListMangaDTO> data = malMangaResponse.data();
                for (MALUserListMangaDTO userList : data) {
                    System.out.println("for manga " + userList.node().title());
                    Manga manga;
                    MALMangaDetailsNode detailsNode = getDetailsSafe(userList.node().id(),
                            "start_date,end_date,num_volumes,num_chapters,related_manga");
                    throttle();
                    if (mangaRepository.existsMangaByMalId(userList.node().id())) {
                        manga = mangaRepository.getMangaByMalId(userList.node().id()).orElseThrow(
                                () -> new MangaNotFoundException("No manga found with MAL Id " + userList.node().id()));
                        manga.setImage(userList.node().main_picture().medium());
                        manga.setStatus(userList.list_status().status());
                        manga.setScore(userList.list_status().score());
                        manga.setChaptersRead(userList.list_status().num_chapters_read());
                        manga.setVolumesRead(userList.list_status().num_volumes_read());
                        if (manga.getNumberOfChapters() == null || manga.getNumberOfChapters() == 0) {
                            manga.setNumberOfChapters(detailsNode.num_chapters());
                        }
                        if (manga.getNumberOfVolumes() == null || manga.getNumberOfVolumes() == 0) {
                            manga.setNumberOfVolumes(detailsNode.num_volumes());
                        }
                        if (manga.getEndDate() == null&& detailsNode.end_date()!=null && detailsNode.end_date().matches(regex)) {
                            manga.setEndDate(LocalDate.parse(detailsNode.end_date()));
                        }
                    } else {
                        manga = mountMangaFromAPI(userList, detailsNode);
                    }
                    if(statuses[i].equals("reading") && manga.getTags().isEmpty()){
                        manga.setTags(openAIService.getTags(manga));
                    }
                    mangaRepository.save(manga);
                    if (!"dropped".equals(userList.list_status().status()) &&
                            !"plan_to_read".equals(userList.list_status().status())) {
                        for (RelatedMALDTO relatedMALDTO : detailsNode.related_manga()) {
                            if ("sequel".equals(relatedMALDTO.relation_type()) ||
                                    "prequel".equals(relatedMALDTO.relation_type()) ||
                                    "spin_off".equals(relatedMALDTO.relation_type())) {
                                System.out.println(" related " + relatedMALDTO.node().title());
                                MALMangaDetailsNode relatedDetails =getDetailsSafe(relatedMALDTO.node().id(),
                                        "start_date,end_date,num_volumes,num_chapters");
                                throttle();
                                if("sequel".equals(relatedMALDTO.relation_type())
                                ||"spin_off".equals(relatedMALDTO.relation_type())){
                                    if(mangaRepository.existsMangaByMalId(relatedMALDTO.node().id())){
                                        Manga sequel = mangaRepository.getMangaByMalId(relatedMALDTO.node().id())
                                                .orElseThrow(()->new MangaNotFoundException("no manga found in database with " +
                                                        "Mal id "+ relatedMALDTO.node().id()));
                                        sequel.setParent(manga);
                                        manga.getSequels().add(sequel);
                                        mangaRepository.save(sequel);
                                    }else{
                                        Manga sequel = mountRelatedFromAPI(relatedMALDTO, relatedDetails);
                                        sequel.setParent(manga);
                                        manga.getSequels().add(sequel);
                                        mangaRepository.save(sequel);
                                    }
                                }if("prequel".equals(relatedMALDTO.relation_type())){
                                    if(mangaRepository.existsMangaByMalId(relatedMALDTO.node().id())){
                                        Manga prequel = mangaRepository.getMangaByMalId(relatedMALDTO.node().id())
                                                .orElseThrow(()->new MangaNotFoundException("no manga found in database" +
                                                        "with MAL id "+ relatedMALDTO.node().id()));
                                        prequel.getSequels().add(manga);
                                        manga.setParent(prequel);
                                        mangaRepository.save(prequel);
                                    }else{
                                        Manga prequel = mountRelatedFromAPI(relatedMALDTO, relatedDetails);
                                        prequel.getSequels().add(manga);
                                        manga.setParent(prequel);
                                        mangaRepository.save(prequel);
                                    }

                                }
                            }

                        }
                        mangaRepository.save(manga);
                    }
                }if(malMangaResponse.paging().next()!=null){
                    offset = offset+500;
                    malMangaResponse = malClient.importManga(username,statuses[i],
                            limit,offset,token);
                    throttle();
                }else{
                    over=true;
                }


            }
        }
    }

    private void throttle() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Manga mountMangaFromAPI(MALUserListMangaDTO userList, MALMangaDetailsNode detailsNode) {
        Manga manga = new Manga(userList.node().title(), userList.list_status().status());
        manga.setImage(userList.node().main_picture().medium());
        manga.setScore(userList.list_status().score());
        manga.setVolumesRead(userList.list_status().num_volumes_read());
        manga.setNumberOfVolumes(detailsNode.num_volumes());
        manga.setNumberOfChapters(detailsNode.num_chapters());
        manga.setChaptersRead(userList.list_status().num_chapters_read());
        manga.setMalId(userList.node().id());
        if (detailsNode.start_date() != null && detailsNode.start_date().matches(regex)) {
            manga.setStartDate(LocalDate.parse(detailsNode.start_date()));
        }
        if (detailsNode.end_date() != null && detailsNode.end_date().matches(regex)) {
            manga.setEndDate(LocalDate.parse(detailsNode.end_date()));
        }
        return manga;
    }

    private Manga mountRelatedFromAPI(RelatedMALDTO relatedMALDTO, MALMangaDetailsNode relatedDetails){
        Manga manga = new Manga(relatedMALDTO.node().title(), "plan_to_read");
        manga.setScore(null);
        manga.setImage(relatedMALDTO.node().main_picture().medium());
        manga.setVolumesRead(0);
        manga.setNumberOfVolumes(relatedDetails.num_volumes());
        manga.setNumberOfChapters(relatedDetails.num_chapters());
        manga.setChaptersRead(0);
        manga.setMalId(relatedMALDTO.node().id());
        if (relatedDetails.start_date() != null && relatedDetails.start_date().matches(regex)) {
            manga.setStartDate(LocalDate.parse(relatedDetails.start_date()));
        }
        if (relatedDetails.end_date() != null && relatedDetails.end_date().matches(regex)) {
            manga.setEndDate(LocalDate.parse(relatedDetails.end_date()));
        }
        return manga;
    }

    private MALMangaDetailsNode getDetailsSafe(Integer id, String fields) {
        int retries = 3;
        for (int i = 0; i < retries; i++) {
            try {
                return malClient.getMangaDetails(id, fields);
            } catch (ResourceAccessException e) {
                System.out.println("Timout for manga " + id + " retry " + (i + 1));
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Skipping manga "+id +  " after retries");
        return null;
    }

}