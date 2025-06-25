package az.aladdin.blogplatform.configuration.mappers;

import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.model.dto.response.UserFollowResponseDto;
import az.aladdin.blogplatform.model.dto.response.UserResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public UserResponseDto toResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail());
    }

    public List<UserResponseDto> toResponse(List<User> users) {
        return users.stream().map(this::toResponse).toList();
    }

    public UserFollowResponseDto mapToFollowResponseDto(User user) {
        return new UserFollowResponseDto(
                user.getId(),
                user.getUsername()
        );
    }

    public List<UserFollowResponseDto> mapToFollowResponseDto(List<User> users) {
        return users.stream().map(this::mapToFollowResponseDto).toList();
    }
}

