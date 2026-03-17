package com.nulhart.repository;

import com.nulhart.model.MALToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MALTokenRepository  extends JpaRepository<MALToken, Integer> {

    Optional<MALToken> getMALTokenByUsername(String username);
}
