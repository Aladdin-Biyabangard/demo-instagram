package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.configuration.mappers.UserMapper;
import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.dao.repository.UserRepository;
import az.aladdin.blogplatform.exception.ResourceAlreadyExistException;
import az.aladdin.blogplatform.exception.ResourceNotFoundException;
import az.aladdin.blogplatform.model.dto.request.UserDto;
import az.aladdin.blogplatform.model.dto.response.UserResponseDto;
import az.aladdin.blogplatform.services.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto createUser(UserDto userDto) {
        existsUserByEmail(userDto.getEmail());
        User user = modelMapper.map(userDto, User.class);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }


    public void existsUserByEmail(String email) {
        if (userRepository.existsUserByEmail(email)) {
            throw new ResourceAlreadyExistException("User already exists!");
        }
    }

    public User findUserById(String id) {
        log.info("Finding user with ID: {}", id);
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found with ID: {}", id);
            return new ResourceNotFoundException("User not found");
        });
    }
}
