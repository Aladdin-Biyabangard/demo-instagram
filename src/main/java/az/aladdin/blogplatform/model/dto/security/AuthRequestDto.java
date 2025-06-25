package az.aladdin.blogplatform.model.dto.security;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRequestDto {
    @NotBlank(message = "User name is required")
    String userName;
    @NotBlank(message = "Password is required")
    String password;
}
