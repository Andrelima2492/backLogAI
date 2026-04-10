package com.nulhart.controller;

import com.nulhart.dto.movie.MovieDTO;
import com.nulhart.services.MovieService;
import com.nulhart.services.OpenAIService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/movies")
@AllArgsConstructor
public class MovieController {
    private MovieService movieService;
    private OpenAIService openAIService;

    @GetMapping
    public Page<MovieDTO> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        return movieService.getAllMovies(page,size);
    }

    @GetMapping("/id/{id}")
    public MovieDTO getMovieById(@PathVariable String id){
        return movieService.getMovieById(id);
    }

    @GetMapping("/year/{year}")
    public Set<MovieDTO> getMoviesWatchedInYear(@PathVariable String year){
        return movieService.getMoviesByYear(year);
    }

    @GetMapping("/status/{status}")
    public Set<MovieDTO> getMoviesByStatus(@PathVariable String status){
        return movieService.getMoviesByStatus(status);
    }

    @GetMapping("imdbid/{imdbId}")
    public MovieDTO getMovieByImdbId(@PathVariable String imdbId){
        return movieService.getMovieByIMDBId(imdbId);
    }

    @GetMapping("director/{director}")
    public Set<MovieDTO> getMoviesByDirector(@PathVariable String director){
        return movieService.getMoviesByDirector(director);
    }
    @PostMapping
    public void createMovie(@RequestBody MovieDTO movieDTO){
        movieService.createMovie(movieDTO);
    }

    @PostMapping("/multiple")
    public void createMultipleMovies(@RequestBody List<MovieDTO> moviesDTO){
        movieService.createMultipleMovies(moviesDTO);
    }

    @DeleteMapping
    public void deleteAllMovies(){
        movieService.deleteAllMovies();
    }

    @DeleteMapping("/id/{id}")
    public void deleteMovie(@PathVariable String id){
        movieService.deleteMovieById(id);
    }

    @PutMapping("/id/{id}")
    public void editMovie(@RequestBody MovieDTO movieDTO, @PathVariable String id){
        movieService.editMovie(movieDTO,id);
    }
}
