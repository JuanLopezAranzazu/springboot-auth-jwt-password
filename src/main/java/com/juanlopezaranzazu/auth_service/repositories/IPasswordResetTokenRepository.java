package com.juanlopezaranzazu.auth_service.repositories;

import com.juanlopezaranzazu.auth_service.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token); // Busca un token por su valor
}