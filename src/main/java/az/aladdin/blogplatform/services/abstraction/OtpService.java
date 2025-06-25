package az.aladdin.blogplatform.services.abstraction;

import az.aladdin.blogplatform.model.dto.request.OtpRequest;

public interface OtpService {

    void sendOtp(String userName, String email);

    void verifyOtp(OtpRequest otpRequest);

}
