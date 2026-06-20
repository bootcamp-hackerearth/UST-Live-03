package com.ust.pos.user.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.UserService;
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
public class UserServiceImpl extends CommonService implements UserService {
    public static final String USER_WITH_USERNAME_EMAIL = "User with username/email - ";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto findByUserName(String username) {
        return modelMapper.map(userRepository.findByUsername(username), UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        String username = userDto.getUsername();
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            if (existingUser.isDeleted()) {
                userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " has been soft deleted.(Rollback by changing status");
                userDto.setSuccess(false);
                return userDto;
            }
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        setAuditFields(user, true);
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
            setAuditFields(existingUser, false);
            userRepository.save(existingUser);
        }
        return userDto;
    }

    @Override
    public boolean delete(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }
        softDelete(user);
        setAuditFields(user, false);
        userRepository.save(user);
        return true;
    }

    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findByDeletedFalse(pageable);
        WsDto<UserDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());
        return userWsDto;
    }
}