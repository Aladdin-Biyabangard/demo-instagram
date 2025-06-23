package az.aladdin.blogplatform.utility;

import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.dao.repository.UserRepository;
import az.aladdin.blogplatform.exception.ResourceNotFoundException;
import az.aladdin.blogplatform.exception.UnauthorizedException;
import az.aladdin.blogplatform.model.enums.user.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthHelper {


    private final UserRepository userRepository;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null && !authentication.isAuthenticated()) {
            log.error("No authenticated user found in the security context.");
            throw new UnauthorizedException("No authenticated user found");
        }

        String authenticatedUserName = authentication.getName();
        log.debug("Authenticated user email: {}", authenticatedUserName);

        return userRepository.findByUserNameAndStatus(authenticatedUserName , Status.ACTIVE)
                .orElseThrow(() -> {
                    log.error("User with email {} not found or is inactive", authenticatedUserName);
                    return new ResourceNotFoundException("USER_NOT_FOUND");
                });
    }

    public String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
