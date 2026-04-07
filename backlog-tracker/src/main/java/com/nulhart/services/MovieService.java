package com.nulhart.services;

import com.nulhart.dto.movie.MovieDTO;
import com.nulhart.exceptions.movies.MovieNotFoundException;
import com.nulhart.model.Movie;
import com.nulhart.repository.MovieRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MovieService {
    private MovieRepository movieRepository;

    private MovieDTO mapToDTO(Movie movie){
        return new MovieDTO(movie.getId(), movie.getTitle(), movie.getStatus(), movie.getReleaseDate(),
                movie.getWatchYear(), movie.getImage(),movie.getDirector(),movie.getImdbId(), movie.getScore());
    }

    private Movie mapToEntity(MovieDTO movieDTO){
        return new Movie(movieDTO.getTitle(), movieDTO.getStatus(), movieDTO.getReleaseDate(),
                movieDTO.getWatchYear(), movieDTO.getImage(), movieDTO.getDirector(), movieDTO.getImdbId());
    }
    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public MovieDTO getMovieById(String id) {
        return mapToDTO(movieRepository.findById(id).orElseThrow(
                ()->new MovieNotFoundException("No movie found with id "+id)));
    }

    public Set<MovieDTO> getMoviesByYear(String year) {
        return movieRepository.findMoviesByWatchYear(year).stream().map(this::mapToDTO).collect(Collectors.toSet());
    }

    public Set<MovieDTO> getMoviesByStatus(String status) {
        return movieRepository.findMoviesByStatus(status).stream().map(this::mapToDTO).collect(Collectors.toSet());
    }

    public MovieDTO getMovieByIMDBId(Integer imdbId) {
        return mapToDTO(movieRepository.findMovieByImdbId(imdbId).orElseThrow(()->
                new MovieNotFoundException("No movie found with imdb id "+ imdbId)));
    }

    public void createMovie(MovieDTO movieDTO) {
        movieRepository.save(mapToEntity(movieDTO));
    }

    public void createMultipleMovies(List<MovieDTO> moviesDTO) {
        movieRepository.saveAll(moviesDTO.stream().map(this::mapToEntity).toList());
    }

    public Set<MovieDTO> getMoviesByDirector(String director) {
        return movieRepository.findMoviesByDirector(director).stream().map(this::mapToDTO).collect(Collectors.toSet());
    }

    public void deleteAllMovies() {
        movieRepository.deleteAll();
    }

    public void deleteMovieById(String id) {
        movieRepository.deleteById(id);

    }

    public void editMovie(MovieDTO movieDTO, String id) {
        Movie movie = movieRepository.findById(id).orElseThrow(()->
                new MovieNotFoundException("No movie found with id "+id));
        movie.setDirector(movieDTO.getDirector());
        movie.setScore(movieDTO.getScore());
        movie.setImdbId(movieDTO.getImdbId());
        movie.setStatus(movieDTO.getStatus());
        movie.setTitle(movieDTO.getTitle());
        movie.setImage(movieDTO.getImage());
        movie.setReleaseDate(movieDTO.getReleaseDate());
        movie.setWatchYear(movieDTO.getWatchYear());

    }
}
