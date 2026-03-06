package com.nulhart.repository;

import com.nulhart.model.Anime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnimeRepository extends JpaRepository<Anime, String> {
    Optional<Anime> findAnimeByTitleIs(String title);

   List<Anime> findAnimeByStatus(String status);

    boolean existsByMalId(Integer malId);

    Anime getAnimeByMalId(Integer malId);

    boolean existsAnimeByTitle(String title);
}
