package com.ust.pos.user.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.User;
import com.ust.pos.modell.UserRepository;
import com.ust.pos.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseService implements UserService {

    private static final String USER_ALREADY_EXISTS_MESSAGE = "User with username/email - ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public UserDto findByUserName(String username) {
        User user = userRepository.findByUsernameAndDeletedFalse(username);
        if (user == null) {
            throw new ResourceNotFoundException("user with identifier '" + username + "' not found");
        }
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        User existingUser = userRepository.findByUsername(userDto.getUsername());

        if (existingUser != null) {
            if (Boolean.TRUE.equals(existingUser.getDeleted())) {
                userDto.setMessage(USER_ALREADY_EXISTS_MESSAGE
                        + userDto.getUsername()
                        + " already exists (Soft-Deleted)");
                userDto.setSuccess(false);
                return userDto;
            }

            userDto.setMessage(
                    USER_ALREADY_EXISTS_MESSAGE
                            + userDto.getUsername()
                            + " already exists"
            );
            userDto.setSuccess(false);
            return userDto;
        }

        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if (user.getStatus() == null) {
            user.setStatus(true);
        }

        setCreatedDetails(user);
        userRepository.save(user);
        userDto.setSuccess(true);
        userDto.setMessage("User created successfully");
        return userDto;
    }

    @Override
    public UserDto update(String oldUsername, UserDto userDto) {
        User existingUser = userRepository.findByUsernameAndDeletedFalse(oldUsername);

        if (existingUser == null) {
            userDto.setMessage("User not found");
            userDto.setSuccess(false);
            return userDto;
        }

        if (!oldUsername.equalsIgnoreCase(userDto.getUsername())) {
            User emailCheck = userRepository.findByUsername(userDto.getUsername());
            if (emailCheck != null) {
                userDto.setMessage(USER_ALREADY_EXISTS_MESSAGE + userDto.getUsername() + " already exists");
                userDto.setSuccess(false);
                return userDto;
            }
        }

        String originalCreatedBy = existingUser.getCreatedBy();
        java.time.LocalDateTime originalCreatedOn = existingUser.getCreatedOn();

        existingUser.setName(userDto.getName());
        existingUser.setUsername(userDto.getUsername());
        existingUser.setPhoneNo(userDto.getPhoneNo());
        existingUser.setRoles(userDto.getRoles());

        existingUser.setCreatedBy(originalCreatedBy);
        existingUser.setCreatedOn(originalCreatedOn);

        setModifiedDetails(existingUser);
        userRepository.save(existingUser);
        userDto.setSuccess(true);
        userDto.setMessage("User updated successfully");
        return userDto;
    }

    @Override
    @Transactional
    public void delete(String username) {
        User user = userRepository.findByUsernameAndDeletedFalse(username);
        if (user != null) {
            softDelete(user);
            setModifiedDetails(user);
            userRepository.save(user);
        }
    }

    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findAllByDeletedFalse(pageable);

        WsDto<UserDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPage(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());

        return userWsDto;
    }

    @Override
    public WsDto<UserDto> findAll(Specification<User> example, Pageable pageable) {

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> page = userRepository.findAll(example, pageable);

        WsDto<UserDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPage(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}