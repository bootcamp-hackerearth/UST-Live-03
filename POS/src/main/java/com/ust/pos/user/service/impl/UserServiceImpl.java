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
import java.util.Optional;

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
    public UserDto findByIdentifier(String identifier) {
        User user = userRepository.findByIdentifier(identifier);
        if (user == null) {
            return null;
        }
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        String username = userDto.getUsername();
        User existingUser = userRepository.findByIdentifier(username);
        if (existingUser != null) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIdentifier(userDto.getUsername());
        userRepository.save(user);
        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto) {
        String username = userDto.getUsername();
        Optional<User> userOptional = userRepository.findById(userDto.getId());
        if (userOptional.isEmpty()) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " not found");
            userDto.setSuccess(false);
            return userDto;
        } else {
            User existingUser = userOptional.get();
            if (!username.equalsIgnoreCase(existingUser.getUsername()) && (userRepository.findByIdentifier(username) != null)) {
                userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " already exists");
                userDto.setSuccess(false);
                return userDto;
            }
            modelMapper.map(userDto, existingUser);
            userRepository.save(existingUser);
        }
        return userDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        userRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findAll(pageable);
        WsDto<UserDto> userDto = new WsDto<>();
        userDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userDto.setTotalRecords(userPage.getTotalElements());
        userDto.setTotalPage(userPage.getTotalPages());
        userDto.setSizePerPage(pageable.getPageSize());
        userDto.setPage(pageable.getPageNumber());
        return userDto;
    }

}
