package az.aladdin.blogplatform.services.abstraction;

import az.aladdin.blogplatform.model.dto.request.OtpRequest;
import az.aladdin.blogplatform.model.dto.security.AuthRequestDto;
import az.aladdin.blogplatform.model.dto.security.AuthResponseDto;
import az.aladdin.blogplatform.model.dto.security.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    void register(RegisterRequest request);

    AuthResponseDto login(AuthRequestDto authRequest, HttpServletRequest request);

    AuthResponseDto verifyAndGetToken(OtpRequest request);

}
