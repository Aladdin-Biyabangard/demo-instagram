package az.aladdin.blogplatform.dao.repository;

import az.aladdin.blogplatform.dao.entities.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, String> {


    Optional<Otp> findByCodeAndUserName(Integer otpCode, String email);
}
