package com.ust.pos.user.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.User;
import com.ust.pos.models.UserRepository;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public UserDto findByUserName(String username) {
        User user = userRepository.findByUsernameAndDeletedFalse(username);

        if (user == null) {
            UserDto dto = new UserDto();
            dto.setSuccess(false);
            dto.setMessage("User not found");
            return dto;
        }
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        User existingUser = userRepository.findByUsername(userDto.getUsername());

        if (existingUser != null) {
            if (Boolean.TRUE.equals(existingUser.getDeleted())) {
                userDto.setMessage("User with identifier - " + userDto.getUsername() + " was deleted and cannot be created again.");
                userDto.setSuccess(false);
                return userDto;
            }
            userDto.setMessage(
                    "User with username/email - " + userDto.getUsername() + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }

        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        setCreatedDetails(user);
        userRepository.save(user);
        userDto.setSuccess(true);
        user.setIdentifier(userDto.getUsername());
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
                userDto.setMessage(
                        "User with username/email - " + userDto.getUsername() + " already exists");
                userDto.setSuccess(false);
                return userDto;
            }
        }
        existingUser.setName(userDto.getName());
        existingUser.setUsername(userDto.getUsername());
        existingUser.setPhoneNo(userDto.getPhoneNo());
        existingUser.setRoles(userDto.getRoles());
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
        if (user == null) return;
        softDelete(user);
        setModifiedDetails(user);
        userRepository.save(user);
    }

    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findAllByDeletedFalse(pageable);
        WsDto<UserDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
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
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }
}
