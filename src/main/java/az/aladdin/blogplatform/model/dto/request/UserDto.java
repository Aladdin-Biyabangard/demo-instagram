package az.aladdin.blogplatform.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {

    private String firstName;

    private String lastName;

    private String password;

    private String email;

}
