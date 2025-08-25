package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Integer> {
  }