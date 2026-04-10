package com.nulhart.services;

import com.nulhart.dto.movie.MovieDTO;
import com.nulhart.dto.movie.OMDBResponse;
import com.nulhart.exceptions.movies.MovieNotFoundException;
import com.nulhart.model.Movie;
import com.nulhart.omdb.OMDBClient;
import com.nulhart.repository.MovieRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MovieService {
    private MovieRepository movieRepository;
    private OMDBClient omdbClient;
    private OpenAIService openAIService;

    private MovieDTO mapToDTO(Movie movie){
        return new MovieDTO(movie.getId(), movie.getTitle(), movie.getStatus(), movie.getReleaseDate(),
                movie.getWatchYear(), movie.getImage(),movie.getDirector(),movie.getImdbId(), movie.getScore(),
                movie.getTags());
    }

    private Movie mapToEntity(MovieDTO movieDTO){
        return new Movie(movieDTO.getTitle(), movieDTO.getStatus(), movieDTO.getReleaseDate(),
                movieDTO.getWatchYear(), movieDTO.getImage(), movieDTO.getDirector(), movieDTO.getImdbId(), movieDTO.getScore());
    }
    public Page<MovieDTO> getAllMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> pageMovie = movieRepository.findAll(pageable);
        return pageMovie.map(this::mapToDTO);

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

    public MovieDTO getMovieByIMDBId(String imdbId) {
        return mapToDTO(movieRepository.findMovieByImdbId(imdbId).orElseThrow(()->
                new MovieNotFoundException("No movie found with imdb id "+ imdbId)));
    }

    public void createMovie(MovieDTO movieDTO) {
        Movie movie = mapToEntity(movieDTO);
        OMDBResponse movieResponse =omdbClient.searchMovie(movie.getTitle());
        movie.setImdbId(movieResponse.imdbID());
        movie.setImage(movieResponse.Poster());
        movie.setDirector(movieResponse.Director());
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        movie.setReleaseDate(LocalDate.parse(movieResponse.Released(), formatter));
        movie.setTags(openAIService.getTags(movie));
        movieRepository.save(movie);
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

    @Transactional
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
