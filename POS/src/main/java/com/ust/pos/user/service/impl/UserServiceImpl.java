package com.ust.pos.user.service.impl;

import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDto findByUserName(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return null;
        }
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        User existingUser = userRepository.findByUsername(userDto.getUsername());

        if (existingUser != null) {
            userDto.setMessage("User with username/email - " + userDto.getUsername() + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
        userDto.setSuccess(true);
        userDto.setMessage("User created successfully");
        return userDto;
    }

    @Override
    public UserDto update(String oldUsername, UserDto userDto) {
        User existingUser = userRepository.findByUsername(oldUsername);

        if (existingUser == null) {
            userDto.setMessage("User not found");
            userDto.setSuccess(false);
            return userDto;
        }

        if (!oldUsername.equalsIgnoreCase(userDto.getUsername())) {
            User emailCheck = userRepository.findByUsername(userDto.getUsername());

            if (emailCheck != null) {
                userDto.setMessage("User with username/email - " + userDto.getUsername() + " already exists");
                userDto.setSuccess(false);
                return userDto;
            }
        }
        existingUser.setName(userDto.getName());
        existingUser.setUsername(userDto.getUsername());
        existingUser.setPhoneNo(userDto.getPhoneNo());
        existingUser.setRoles(userDto.getRoles());
        userRepository.save(existingUser);
        userDto.setSuccess(true);
        userDto.setMessage("User updated successfully");
        return userDto;
    }

    @Override
    @Transactional
    public void delete(String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    public List<UserDto> findAll() {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        return modelMapper.map(userRepository.findAll(), listType);
    }
}