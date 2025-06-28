package az.aladdin.blogplatform.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAboutDto {

    private String userName;
    private String registrationDate;
}
