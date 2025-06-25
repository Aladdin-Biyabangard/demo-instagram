package az.aladdin.blogplatform.dao.repository;

import az.aladdin.blogplatform.dao.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
