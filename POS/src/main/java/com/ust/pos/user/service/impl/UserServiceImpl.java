package com.ust.pos.user.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class UserServiceImpl extends BaseService implements UserService {
    public static final String USER_WITH_USERNAME_EMAIL =
            "User with username/email - ";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ModelMapper modelMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto findByUserName(String username) {
        User user = userRepository.findByUsernameAndDeletedFalse(username);
        if (user == null) {
            throw new ResourceNotFoundException(
                    "User not found with username: " + username
            );
        }
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        String username = userDto.getUsername();
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            if (Boolean.TRUE.equals(existingUser.getDeleted())) {
                userDto.setMessage(
                        USER_WITH_USERNAME_EMAIL + username + " was deleted and cannot be recreated"
                );
            } else {
                userDto.setMessage(
                        USER_WITH_USERNAME_EMAIL + username + " already exists"
                );
            }
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        setCreatedDetails(user);
        userRepository.save(user);
        userDto.setSuccess(true);
        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto) {
        Optional<User> userOptional = userRepository.findById(userDto.getId());
        if (userOptional.isEmpty()) {
            userDto.setMessage(
                    USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " not found"
            );
            userDto.setSuccess(false);
            return userDto;
        }
        User existingUser = userOptional.get();
        if (Boolean.TRUE.equals(existingUser.getDeleted())) {
            userDto.setMessage(
                    USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " not found"
            );
            userDto.setSuccess(false);
            return userDto;
        }
        String username = userDto.getUsername();
        if (!username.equalsIgnoreCase(existingUser.getUsername())
                && userRepository.findByUsername(username) != null) {
            userDto.setMessage(
                    USER_WITH_USERNAME_EMAIL + username + " already exists"
            );
            userDto.setSuccess(false);
            return userDto;
        }
        modelMapper.map(userDto, existingUser);
        if (userDto.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        setModifiedDetails(existingUser);
        userRepository.save(existingUser);
        userDto.setSuccess(true);
        return userDto;
    }

    @Override
    public boolean delete(String username) {
        User user = userRepository.findByUsernameAndDeletedFalse(username);
        if (user != null) {
            softDelete(user);
            setModifiedDetails(user);
            userRepository.save(user);
        }
        return true;
    }

    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findByDeletedFalse(pageable);
        WsDto<UserDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        wsDto.setTotalRecords(userPage.getTotalElements());
        wsDto.setTotalPages(userPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public WsDto<UserDto> findAll(Specification<User> example, Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> page = userRepository.findAll(example, pageable);

        WsDto<UserDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }
}