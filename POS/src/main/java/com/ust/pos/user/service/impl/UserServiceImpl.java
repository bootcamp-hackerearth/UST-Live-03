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
import java.util.Optional;

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
        return user != null ? modelMapper.map(user, UserDto.class) : null;
    }

    @Override
    public UserDto save(UserDto userDto) {
        User existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser != null) {
            userDto.setMessage("User already exists with username/email: " + userDto.getUsername());
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
        userDto.setSuccess(true);
        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto) {
        Optional<User> userOptional = userRepository.findById(userDto.getId());
        if (userOptional.isEmpty()) {
            userDto.setMessage("User not found");
            userDto.setSuccess(false);
            return userDto;
        }
        User existingUser = userOptional.get();
        if (!userDto.getUsername().equalsIgnoreCase(existingUser.getUsername()) && userRepository.findByUsername(userDto.getUsername()) != null) {
            userDto.setMessage("Username already exists: " + userDto.getUsername());
            userDto.setSuccess(false);
            return userDto;
        }
        modelMapper.map(userDto, existingUser);
        userRepository.save(existingUser);
        userDto.setSuccess(true);
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