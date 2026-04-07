package com.nulhart.repository;

import com.nulhart.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface MovieRepository extends JpaRepository<Movie, String > {
    Set<Movie> findMoviesByWatchYear(String year);
    Set<Movie> findMoviesByStatus(String status);
    Optional<Movie> findMovieByImdbId(String imdbId);
    Set<Movie> findMoviesByDirector(String director);
}
