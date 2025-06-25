package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.dao.entities.Otp;
import az.aladdin.blogplatform.dao.repository.OtpRepository;
import az.aladdin.blogplatform.exception.ResourceNotFoundException;
import az.aladdin.blogplatform.model.dto.request.OtpRequest;
import az.aladdin.blogplatform.model.enums.EmailTemplate;
import az.aladdin.blogplatform.services.abstraction.EmailService;
import az.aladdin.blogplatform.services.abstraction.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class OtpServiceImpl implements OtpService {


    private final OtpRepository otpRepository;
    private final EmailService emailServiceImpl;


    public void sendOtp(String userName, String email) {
        log.info("Operation of sending otp started for user with user name {}", userName);
        String code = generateOtp(userName);
        Map<String, String> placeholders = Map.of("userName", userName, "code", code);
        emailServiceImpl.sendEmail(email, EmailTemplate.VERIFICATION, placeholders);
        log.info("Otp code sent to user user name {}", userName);
    }

    public void verifyOtp(OtpRequest otpRequest) {
        Otp otp = otpRepository.findByCodeAndUserName(otpRequest.getOtpCode(), otpRequest.getUserName()).orElseThrow(
                () -> new ResourceNotFoundException("OTP_NOT_FOUND"));
        if (otp.getExpirationTime().isBefore(LocalDateTime.now())) {
            log.error("Otp is expired for user with email {}", otpRequest.getUserName());
            throw new IllegalArgumentException("OTP_EXPIRED");
        }
    }


    private String generateOtp(String userName) {
        log.info("Operation of generating otp started for user {}", userName);
        SecureRandom random = new SecureRandom();
        Integer code = 100_000 + random.nextInt(900_000);
        Otp otp = Otp.builder()
                .userName(userName)
                .code(code)
                .expirationTime(LocalDateTime.now().plusMinutes(15))
                .build();
        otpRepository.save(otp);
        log.info("Otp generated for user with user name {}", userName);
        return String.valueOf(code);
    }
}
