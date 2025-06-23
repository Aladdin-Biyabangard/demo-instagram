package az.aladdin.blogplatform.controller;

import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.services.abstraction.LoginHistoryService;
import az.aladdin.blogplatform.services.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/histories")
@RequiredArgsConstructor
public class LoginHistoryController {

    private final LoginHistoryService loginHistoryServiceImpl;
    private final UserServiceImpl userServiceImpl;


    @PostMapping(path = "users/{userId}")
    public void saveLoginHistory(@PathVariable String userId, HttpServletRequest request) {
        User user = userServiceImpl.findUserById(userId);
        loginHistoryServiceImpl.saveLoginHistory(user, request);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "users/{userId}")
    public List<String> getDevicesInfo(@PathVariable String userId) {
        return loginHistoryServiceImpl.getDevicesInfo(userId);
    }


}
