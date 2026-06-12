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
@Transactional
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
        return modelMapper.map(userRepository.findByUsername(username), UserDto.class);
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
            if (!username.equalsIgnoreCase(existingUser.getUsername()) && userRepository.findByUsername(username) != null) {
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
    public void delete(String identifier) {
        userRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        if (pageable == null) {
            List<UserDto> userDtoList = modelMapper.map(userRepository.findAll(), listType);
            WsDto<UserDto> response = new WsDto<>();
            response.setDtoList(userDtoList);
            response.setTotalRecords(userDtoList.size());
            return response;
        }
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserDto> userDtoList = modelMapper.map(userPage.getContent(), listType);
        WsDto<UserDto> wsDto = new WsDto<>();
        wsDto.setDtoList(userDtoList);
        wsDto.setPage(userPage.getNumber());
        wsDto.setSizePerPage(userPage.getSize());
        wsDto.setTotalPages(userPage.getTotalPages());
        wsDto.setTotalRecords(userPage.getTotalElements());
        return wsDto;
    }

}