package az.aladdin.blogplatform.controller;

import az.aladdin.blogplatform.model.dto.response.UserFollowResponseDto;
import az.aladdin.blogplatform.model.dto.response.UserFollowStatsDto;
import az.aladdin.blogplatform.services.abstraction.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "{userId}/follow/{userName}/toggle")
    public void toggleFollow(@PathVariable String userId,
                             @PathVariable String userName) {
        followService.toggleFollow(userId, userName);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "{userId}/followers")
    public List<UserFollowResponseDto> getUserFollowers(@PathVariable String userId) {
        return followService.getUserFollowers(userId);
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "{userId}/following")
    public List<UserFollowResponseDto> getUserFollowing(@PathVariable String userId) {
        return followService.getUserFollowing(userId);
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "{userId}/stats")
    public UserFollowStatsDto getUserFollowStats(@PathVariable String userId) {
        return followService.getUserFollowStats(userId);
    }

}
