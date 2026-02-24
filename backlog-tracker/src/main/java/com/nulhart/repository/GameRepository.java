package com.nulhart.repository;

import com.nulhart.dto.GameDTO;
import com.nulhart.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game,Long> {

    Optional<Object> findGameByTitleIs(String title);

    List<Game> findGameByConsole(String console);

    List<Game> findGameByStatus(String status);
}
