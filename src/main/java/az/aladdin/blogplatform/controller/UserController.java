package az.aladdin.blogplatform.controller;

import az.aladdin.blogplatform.model.dto.request.UserDto;
import az.aladdin.blogplatform.model.dto.response.UserResponseDto;
import az.aladdin.blogplatform.services.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userServiceImpl;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userServiceImpl.createUser(userDto));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "{userId}")
    public void deleteUser(@PathVariable String userId) {
        userServiceImpl.deleteUser(userId);
    }
}
