package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.dao.entities.RefreshToken;
import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.dao.entities.UserProfile;
import az.aladdin.blogplatform.dao.repository.RefreshTokenRepository;
import az.aladdin.blogplatform.dao.repository.UserProfileRepository;
import az.aladdin.blogplatform.dao.repository.UserRepository;
import az.aladdin.blogplatform.exception.ResourceAlreadyExistException;
import az.aladdin.blogplatform.exception.ResourceNotFoundException;
import az.aladdin.blogplatform.exception.UnauthorizedException;
import az.aladdin.blogplatform.model.dto.request.OtpRequest;
import az.aladdin.blogplatform.model.dto.security.AuthRequestDto;
import az.aladdin.blogplatform.model.dto.security.AuthResponseDto;
import az.aladdin.blogplatform.model.dto.security.RegisterRequest;
import az.aladdin.blogplatform.model.enums.user.Role;
import az.aladdin.blogplatform.model.enums.user.Status;
import az.aladdin.blogplatform.services.abstraction.AuthService;
import az.aladdin.blogplatform.services.abstraction.LoginHistoryService;
import az.aladdin.blogplatform.services.abstraction.OtpService;
import az.aladdin.blogplatform.utility.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpServiceImpl;
    private final LoginHistoryService loginHistoryServiceImpl;

    @Transactional
    public AuthResponseDto login(AuthRequestDto authRequest, HttpServletRequest request) {
        log.info("Attempting to authenticate user with email: {}", authRequest.getUserName());

        User user = authenticateUser(authRequest);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUserName(),
                            authRequest.getPassword()
                    )
            );

            log.info("Authentication successful for user: {}", authRequest.getUserName());

            String role = extractRoleName(user);
            AuthResponseDto response = getAccessTokenAndRefreshToken(user);
            response.setRole(role);
            loginHistoryServiceImpl.saveLoginHistory(user, request);
            log.info("Login history saved for user: {}", user.getUsername());
            return response;

        } catch (BadCredentialsException ex) {
            log.warn("Invalid credentials for user: {}", authRequest.getUserName());
            throw new UnauthorizedException("Invalid email or password.");
        }
    }

    @Transactional
    public void register(RegisterRequest request) {
        log.info("Starting registration process for username: {}", request.getUserName());
        validateUserRequest(request);

        User user = createUser(request);
        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .build();

        userRepository.save(user);
        userProfileRepository.save(userProfile);
        log.info("User and profile saved for username: {}", user.getUsername());

        otpServiceImpl.sendOtp(user.getUsername(), user.getEmail());
        log.info("OTP sent to email: {} for user: {}", user.getEmail(), user.getUsername());
    }

    public AuthResponseDto verifyAndGetToken(OtpRequest request) {
        log.info("Verifying OTP for user: {}", request.getUserName());
        Optional<User> optionalUser = userRepository.findUserByUserName(request.getUserName().trim());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            log.info("User found for verification: {}", user.getUsername());

            otpServiceImpl.verifyOtp(request);
            log.info("OTP verified for user: {}", user.getUsername());

            user.setStatus(Status.ACTIVE);
            userRepository.save(user);
            log.info("User status updated to ACTIVE for user: {}", user.getUsername());

            String role = extractRoleName(user);
            AuthResponseDto response = getAccessTokenAndRefreshToken(user);
            response.setRole(role);
            log.info("Access and refresh tokens generated for user: {}", user.getUsername());
            return response;
        } else {
            log.warn("User not found during OTP verification: {}", request.getUserName());
            return null;
        }
    }

    public AuthResponseDto getAccessTokenAndRefreshToken(User user) {
        log.info("Generating tokens for user: {}", user.getUsername());
        var jwtToken = jwtUtil.createToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(refreshToken);
        log.info("Refresh token saved for user: {}", user.getUsername());

        AuthResponseDto authResponse = AuthResponseDto.builder()
                .userName(user.getUsername())
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getId())
                .build();

        log.info("Tokens created and returned for user: {}", user.getUsername());
        return authResponse;
    }

    private User authenticateUser(AuthRequestDto authRequest) {
        log.debug("Searching active user with username: {}", authRequest.getUserName());
        return userRepository.findByUserNameAndStatus(authRequest.getUserName(), Status.ACTIVE)
                .orElseThrow(() -> {
                    log.error("User not found or inactive: {}", authRequest.getUserName());
                    return new ResourceNotFoundException("USER_NOT_FOUND_OR_INACTIVE");
                });
    }

    private void validateUserRequest(RegisterRequest request) {
        if (userRepository.existsUserByUserName(request.getUserName())) {
            log.error("User with username {} already exists", request.getUserName());
            throw new ResourceAlreadyExistException("USER_ALREADY_EXISTS");
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            log.error("Password and confirmation do not match for user: {}", request.getUserName());
            throw new IllegalArgumentException("PASSWORD_MISMATCHING");
        }

        log.info("User request validated for registration: {}", request.getUserName());
    }

    private User createUser(RegisterRequest request) {
        log.debug("Creating new user object for username: {}", request.getUserName());
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .userName(request.getUserName())
                .email(request.getEmail())
                .status(Status.PENDING)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
    }

    private String extractRoleName(User user) {
        String role = user.getRole().toString();
        log.debug("Extracted role {} for user: {}", role, user.getUsername());
        return role;
    }
}
