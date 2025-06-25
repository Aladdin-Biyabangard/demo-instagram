package az.aladdin.blogplatform.model.dto.security;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {

    @NotBlank(message = "First name is required.")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name can only contain letters.")
    String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters.")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name can only contain letters.")
    String lastName;


    @NotBlank(message = "User name is required.")
    @Size(min = 2, max = 50, message = "User name must be between 2 and 50 characters.")
    @Pattern(regexp = "^[A-Za-z]+$", message = "User name can only contain letters.")
    String userName;

    @NotBlank
    @Size(max = 100, message = "Email cannot be longer than 100 characters.")
    @Email(message = "Invalid email format.")
    String email;

    String telephoneNumber;


    @NotNull(message = "new password can not be null")
    @Size(min = 6, max = 30)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{6,}$",
            message = "Invalid Password: Must be at least 6 characters long, with at least one uppercase letter, one lowercase letter, and one digit. Special characters (!@#$%^&*) are allowed but not required."
    )

    private String password;

    @NotBlank(message = "Password confirmation is required.")
    String passwordConfirm;

}
