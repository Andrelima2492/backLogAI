package com.nulhart.services;

import com.nulhart.dto.manga.MangaDTO;
import com.nulhart.dto.manga.RelatedMangaDTO;
import com.nulhart.exceptions.manga.MangaNotFoundException;
import com.nulhart.model.Manga;
import com.nulhart.myanimelist.MALClient;
import com.nulhart.myanimelist.MALProperties;
import com.nulhart.repository.MALTokenRepository;
import com.nulhart.repository.MangaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MangaService {
    private final MangaRepository mangaRepository;
    private final MALProperties malProperties;
    private final MALClient malClient;
    private final MALTokenRepository malTokenRepository;

    
    
    public MangaDTO convertToDTO(Manga manga){
      Set<RelatedMangaDTO> sequels = new HashSet<>();
      RelatedMangaDTO parent = null;
      for(Manga sequel : manga.getSequels()){
          RelatedMangaDTO sequelDTO = new RelatedMangaDTO(sequel.getTitle(), sequel.getStatus(),
                  sequel.getNumberOfChapters(), sequel.getChaptersRead(), sequel.getNumberOfVolumes(), sequel.getVolumesRead()
          , manga.getMalId());
          sequels.add(sequelDTO);
      }
      if(manga.getParent() != null){
          parent = new RelatedMangaDTO(manga.getParent().getTitle(), manga.getParent().getStatus(),
                  manga.getParent().getNumberOfChapters(), manga.getParent().getChaptersRead(),
                  manga.getParent().getNumberOfVolumes(), manga.getParent().getVolumesRead(), manga.getMalId());
      }
      return new MangaDTO(manga.getId(), manga.getTitle(), manga.getStatus(),manga.getNumberOfChapters(), manga.getChaptersRead(),
              manga.getNumberOfVolumes(), manga.getVolumesRead(), manga.getScore(), manga.getStartDate(), manga.getEndDate(),
              manga.getMalId(), sequels, parent);
    }

    public Set<MangaDTO> getAllManga() {
        return mangaRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toSet());
    }

    public MangaDTO getMangaById(String id) {
        return convertToDTO(mangaRepository.findById(id).orElseThrow(()->
                new MangaNotFoundException("No manga found for id " + id)));
    }

    public Set<MangaDTO> getMangaByStatus(String status) {
        return mangaRepository.findMangaByStatus(status).stream().map(this::convertToDTO).collect(Collectors.toSet());
    }

    public MangaDTO getMangaByMALId(Integer id) {
        return convertToDTO(mangaRepository.getMangaByMalId(id).orElseThrow(()->
                new MangaNotFoundException("No manga found for MAL id " + id + " either check your id or try to add it")));
    }

    public Set<MangaDTO> getSequels(String id){
        Manga parent = mangaRepository.findById(id).orElseThrow(()->new MangaNotFoundException(
                "No manga found with id " + id));
        return parent.getSequels().stream().map(this::convertToDTO).collect(Collectors.toSet());
    }

    public MangaDTO getParent(String id) {
        Manga current = mangaRepository.findById(id).orElseThrow(()->
                new MangaNotFoundException("No manga found with id "+id));
        return convertToDTO(current.getParent());
    }

    public void deleteAllManga() {
        mangaRepository.deleteAll();
    }

    public void deleteById(String id) {
        Manga target = mangaRepository.findById(id).orElseThrow(()->
                new MangaNotFoundException("No manga found with id "+id));
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
        if(manga.getParent()!=null){
                Manga parent = mangaRepository.getMangaByMalId(manga.getParent().malId()).orElseThrow(
                        ()->new MangaNotFoundException("no manga found in system with mal id" + manga.getParent().malId())
                );
                mangaEntity.setParent(parent);
        }
        if(!manga.getSequels().isEmpty()){
            Set<Manga> sequels = new HashSet<>();
            for(RelatedMangaDTO sequelDTO : manga.getSequels()){
                Manga sequel = mangaRepository.getMangaByMalId(sequelDTO.malId()).orElseThrow(()
                ->new MangaNotFoundException("sequel with mal id "+ sequelDTO.malId()));
                sequels.add(sequel);
            }
        }
    }
}