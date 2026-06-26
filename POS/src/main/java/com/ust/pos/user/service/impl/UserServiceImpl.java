package com.ust.pos.user.service.impl;

import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final String USER_EXISTS_MSG =
            "User with username/email - ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto findByUserName(String username) {
        User user = userRepository.findByUsernameAndDeletedFalse(username);

        if (user == null) {
            UserDto dto = new UserDto();
            dto.setSuccess(false);
            dto.setMessage("User not found - " + username);
            return dto;
        }

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        String username = userDto.getUsername();
        User existingUser = userRepository.findByUsernameAndDeletedFalse(username);
        if (existingUser != null) {
            userDto.setMessage(USER_EXISTS_MSG + username + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }

        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
        userDto.setMessage("Registration successful");
        userDto.setSuccess(true);
        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto) {
        Optional<User> userOptional =
                userRepository.findById(userDto.getId());
        if (userOptional.isEmpty()) {
            userDto.setMessage(USER_EXISTS_MSG + userDto.getUsername() + " not found");
            userDto.setSuccess(false);
            return userDto;
        }

        User existingUser = userOptional.get();
        String username = userDto.getUsername();
        if (!username.equalsIgnoreCase(existingUser.getUsername())
                && userRepository.findByUsernameAndDeletedFalse(username) != null) {

            userDto.setMessage(USER_EXISTS_MSG + username + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }

        modelMapper.map(userDto, existingUser);
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            existingUser.setPassword(
                    passwordEncoder.encode(userDto.getPassword())
            );
        }
        userRepository.save(existingUser);
        userDto.setSuccess(true);
        userDto.setMessage("User updated successfully");
        return userDto;
    }

    @Override
    public void delete(String username) {
        User user =
                userRepository.findByUsernameAndDeletedFalse(username);
        if (user != null) {
            user.setDeleted(true);
            userRepository.save(user);
        }
    }

    @Override
    public List<UserDto> findAll() {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        return modelMapper.map(userRepository.findByDeletedFalse(), listType);
    }

    @Override
    public Page<UserDto> findAll(Pageable pageable, String search) {
        Page<User> userPage;
        if (search != null && !search.trim().isEmpty()) {
            userPage =
                    userRepository.findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseAndDeletedFalse(
                            search,
                            search,
                            pageable
                    );
        } else {
            userPage = userRepository.findByDeletedFalse(pageable);
        }
        return userPage.map(user ->
                modelMapper.map(user, UserDto.class)
        );
    }
}