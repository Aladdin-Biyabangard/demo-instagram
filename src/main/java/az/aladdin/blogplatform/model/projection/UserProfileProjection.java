package az.aladdin.blogplatform.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserProfileProjection {

    private String id;

    private String bio;

    private String profilePhotoUrl;

    private String profilePhotoKey;
}
