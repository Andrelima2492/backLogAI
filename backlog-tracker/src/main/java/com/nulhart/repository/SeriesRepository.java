package com.nulhart.repository;

import com.nulhart.model.Series;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series,String> {

    Optional<Series> findSeriesByImdbId(String imdbId);
    List<Series> findSeriesByStatus(String status);
    List<Series> findSeriesByYearWatched(Integer yearWatched);

}
