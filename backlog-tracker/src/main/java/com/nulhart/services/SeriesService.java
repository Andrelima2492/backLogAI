package com.nulhart.services;

import com.nulhart.dto.series.SeriesDTO;
import com.nulhart.exceptions.series.SeriesNotFoundException;
import com.nulhart.model.Series;
import com.nulhart.omdb.OMDBClient;
import com.nulhart.repository.SeriesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesService {
    private SeriesRepository seriesRepository;
    private OpenAIService openAIService;
    private OMDBClient omdbClient;
    
    public SeriesDTO mapToDTO(Series series){
        return new SeriesDTO(series.getId(), series.getTitle(), series.getStatus(), series.getNumberOfSeasons(),
                series.getSeasonsWatched(), series.getScore(), series.getImdbId(), series.getImage(), series.getYearsAired(),
                series.getYearWatched());
    }

    public Series mapToEntity(SeriesDTO seriesDTO){
        Series series = new Series(seriesDTO.getTitle(), seriesDTO.getStatus());
        series.setScore(seriesDTO.getScore());
        series.setImage(seriesDTO.getImage());
        series.setImdbId(seriesDTO.getImdbID());
        series.setSeasonsWatched(seriesDTO.getSeasonsWatched());
        series.setNumberOfSeasons(seriesDTO.getNumberOfSeasons());
        series.setYearsAired(seriesDTO.getYearsAired());
        series.setYearWatched(seriesDTO.getYearWatched());
        return  series;
    }
    public List<SeriesDTO> getAllSeries() {
        return seriesRepository.findAll().stream().map(this::mapToDTO).toList();
    }


    public SeriesDTO getSeriesById(String id) {
        return mapToDTO(seriesRepository.findById(id).orElseThrow(
                ()->new SeriesNotFoundException("No Series found with id "+id)));
    }

    public SeriesDTO getSeriesByImdbId(String imdbId) {
        return mapToDTO(seriesRepository.findSeriesByImdbId(imdbId).orElseThrow(
                ()->new SeriesNotFoundException("No Series found with imdbId "+ imdbId)));
    }

    public List<SeriesDTO> getSeriesByStatus(String status) {
        return seriesRepository.findSeriesByStatus(status).stream().map(this::mapToDTO).toList();
    }

    public List<SeriesDTO> getSeriesWatchedInYear(Integer year) {
        return seriesRepository.findSeriesByYearWatched(year).stream().map(this::mapToDTO).toList();
    }

    public void createSeries(SeriesDTO seriesDTO) {
        seriesRepository.save(mapToEntity(seriesDTO));
    }


    public void deleteAllSeries() {
        seriesRepository.deleteAll();
    }

    public void deleteSeriesById(String id) {
        seriesRepository.deleteById(id);
    }

    @Transactional
    public void editSeries(String id, SeriesDTO seriesDTO) {
        Series series = seriesRepository.findById(id).orElseThrow(
                ()->new SeriesNotFoundException("No Series found with id "+id));
        series.setStatus(seriesDTO.getStatus());
        series.setTitle(seriesDTO.getTitle());
        series.setScore(seriesDTO.getScore());
        series.setSeasonsWatched(seriesDTO.getSeasonsWatched());
        series.setYearsAired(seriesDTO.getYearsAired());
        series.setNumberOfSeasons(seriesDTO.getNumberOfSeasons());
        series.setImdbId(seriesDTO.getImdbID());
        series.setImage(seriesDTO.getImage());
        series.setYearWatched(seriesDTO.getYearWatched());
    }
}
