package com.nulhart.repository;

import com.nulhart.model.Manga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface MangaRepository extends JpaRepository<Manga, String> {

    Set<Manga> findMangaByStatus(String status);
    Optional<Manga> getMangaByMalId(Integer id);
}
