package az.aladdin.blogplatform.model.dto.security;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    String userName;
    String role;
    String accessToken;
    String refreshToken;
}
