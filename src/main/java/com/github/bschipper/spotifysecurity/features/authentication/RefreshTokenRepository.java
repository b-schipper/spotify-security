package com.github.bschipper.spotifysecurity.features.authentication;

import com.github.bschipper.spotifysecurity.features.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Boolean existsRefreshTokenByUserId(Long userId);

    @Modifying
    void deleteByUser(User user);
}
