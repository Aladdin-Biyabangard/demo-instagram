package az.aladdin.blogplatform.services.abstraction;

import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.model.dto.request.UserDto;
import az.aladdin.blogplatform.model.dto.response.UserResponseDto;

public interface UserService {
    UserResponseDto createUser(UserDto userDto);

    User findUserById(String id);

    void deleteUser(String userId);

    void removeUserFromFollowersAndFollowing(User user);
}

