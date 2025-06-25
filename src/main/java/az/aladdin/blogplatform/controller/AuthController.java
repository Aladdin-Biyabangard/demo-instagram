package az.aladdin.blogplatform.controller;

import az.aladdin.blogplatform.model.dto.request.OtpRequest;
import az.aladdin.blogplatform.model.dto.security.AuthRequestDto;
import az.aladdin.blogplatform.model.dto.security.AuthResponseDto;
import az.aladdin.blogplatform.model.dto.security.RegisterRequest;
import az.aladdin.blogplatform.services.abstraction.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authServiceImpl;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "register")
    public void register(@RequestBody RegisterRequest request) {
        authServiceImpl.register(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "login")
    public AuthResponseDto login(HttpServletRequest request, @RequestBody AuthRequestDto authRequest) {
        return authServiceImpl.login(authRequest, request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("verify")
    public AuthResponseDto verifyAndGetToken(@RequestBody OtpRequest request) {
        return authServiceImpl.verifyAndGetToken(request);
    }

}
