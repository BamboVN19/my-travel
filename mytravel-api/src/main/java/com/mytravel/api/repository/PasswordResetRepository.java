package com.mytravel.api.repository;

import com.mytravel.api.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, UUID> {
  Optional<PasswordReset> findTopByEmailOrderByCreatedAtDesc(String email);
  Optional<PasswordReset> findByEmailAndOtpCodeAndIsUsedFalse(String email, String otpCode);
}
