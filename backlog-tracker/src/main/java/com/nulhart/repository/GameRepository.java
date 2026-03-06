package com.nulhart.repository;

import com.nulhart.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game,String> {

    Optional<Object> findGameByTitleIs(String title);

    List<Game> findGameByConsole(String console);

    List<Game> findGameByStatus(String status);

    List<Game> findTop5ByDateOfCompletionNotNullOrderByDateOfCompletionDesc();
    void deleteGameByTitle(String title);

    boolean existsByTitle(String title);

    Optional<Game> findGameByRawgId(Long rawgId);
}
