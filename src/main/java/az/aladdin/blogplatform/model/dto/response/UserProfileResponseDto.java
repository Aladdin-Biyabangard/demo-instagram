package az.aladdin.blogplatform.model.dto.response;

import az.aladdin.blogplatform.model.enums.user.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserProfileResponseDto {

    private String id;

    private String bio;

    private Gender gender;

    private String profilePhotoUrl;

    private String profilePhotoKey;
}
