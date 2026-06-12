package com.ust.pos.user.service.impl;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    public static final String USER_WITH_USERNAME_EMAIL = "User with username/email - ";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDto findByUserName(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return modelMapper.map(user, UserDto.class);
        }
        return null;
    }

    @Override
    public UserDto findByIdentifier(String identifier) {
        User user = userRepository.findByIdentifier(identifier);
        if (user == null) return null;
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        String username = userDto.getUsername();
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIdentifier(userDto.getIdentifier());
        userRepository.save(user);
        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto) {

        User existingUser = userRepository.findByUsername(userDto.getUsername());

        if (existingUser == null) {
            userDto.setMessage("User not found");
            userDto.setSuccess(false);
            return userDto;
        }
        if (!existingUser.getUsername().equalsIgnoreCase(userDto.getUsername())) {
            User emailCheck = userRepository.findByUsername(userDto.getUsername());

            if (emailCheck != null) {
                userDto.setMessage("User with email " + userDto.getUsername() + " already exists");
                userDto.setSuccess(false);
                return userDto;
            }
        }
        var existingRoles = existingUser.getRoles();
        modelMapper.map(userDto, existingUser);
        if (userDto.getRoles() == null || userDto.getRoles().isEmpty()) {
            existingUser.getRoles().addAll(existingRoles);
        }

        userRepository.save(existingUser);

        userDto.setSuccess(true);
        userDto.setMessage("User updated successfully");
        return userDto;
    }

    @Transactional
    @Override
    public void delete(String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findAll(pageable);

        WsDto<UserDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());

        return userWsDto;
    }

    @Override
    public UserDto toggleStatus(String identifier) {
        User user = userRepository.findByIdentifier(identifier);
        user.setStatus(!user.isStatus());
        userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> findIfTrue() {
        Type listType = new TypeToken<List<UserDto>>() {

        }.getType();
        return modelMapper.map(userRepository.findByStatusIsTrue(), listType);
    }
}
