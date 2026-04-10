package com.nulhart.controller;

import com.nulhart.dto.series.SeriesDTO;
import com.nulhart.services.SeriesService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/series")
@AllArgsConstructor
public class SeriesController {
    private SeriesService seriesService;

     @GetMapping
    public Page<SeriesDTO> getAllSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
     ){
         return  seriesService.getAllSeries(page,size);
     }

     @GetMapping("/id/{id}")
    public SeriesDTO getSeriesById(@PathVariable  String id){
         return seriesService.getSeriesById(id);
     }

     @GetMapping("/imdbid/{imdbId}")
    public SeriesDTO getSeriesByIMDBId(@PathVariable String imdbId){
         return seriesService.getSeriesByImdbId(imdbId);
     }

     @GetMapping("/status/{status}")
    public List<SeriesDTO> getSeriesByStatus(@PathVariable String status){
         return seriesService.getSeriesByStatus(status);
     }


     @GetMapping("/watched/{year}")
    public List<SeriesDTO> getSeriesWatchedInYear(@PathVariable Integer year){
         return seriesService.getSeriesWatchedInYear(year);
     }

     @PostMapping
    public void insertSeries(@RequestBody SeriesDTO seriesDTO){
         seriesService.createSeries(seriesDTO);
     }

     @DeleteMapping
    public void deleteAll(){
         seriesService.deleteAllSeries();
     }

     @DeleteMapping("/id/{id}")
    public void deleteSeriesById(String id){
         seriesService.deleteSeriesById(id);
     }

     @PutMapping("/id/{id}")
    public void editSeries(@PathVariable String id, @RequestBody SeriesDTO seriesDTO){
         seriesService.editSeries(id, seriesDTO);
     }

}
