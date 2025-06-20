package az.aladdin.blogplatform.controller;

import az.aladdin.blogplatform.model.dto.request.UserDto;
import az.aladdin.blogplatform.model.dto.response.UserResponseDto;
import az.aladdin.blogplatform.services.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userServiceImpl;

    @PostMapping(path = "")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userServiceImpl.createUser(userDto));
    }
}
