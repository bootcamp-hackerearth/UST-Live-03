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

@Service
public class UserServiceImpl extends BaseService implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto findByUserName(String username) {

        User user = userRepository.findByUsername(username);

        if (user == null || Boolean.TRUE.equals(user.getDeleted())) {
            return null;
        }

        return modelMapper.map(user, UserDto.class);
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

        user.setIdentifier(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        setCreatedDetails(user);

        userRepository.save(user);

        userDto.setSuccess(true);
        userDto.setMessage("User created successfully");

        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto) {

        Optional<User> userOptional = userRepository.findById(userDto.getId());

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        User existingUser = userOptional.get();

        if (Boolean.TRUE.equals(existingUser.getDeleted())) {
            throw new ResourceNotFoundException("User with id '" + userDto.getId() + "' is deleted");
        }

        if (!userDto.getUsername().equalsIgnoreCase(existingUser.getUsername())
                && userRepository.findByUsername(userDto.getUsername()) != null) {
            userDto.setMessage("Username already exists: " + userDto.getUsername());
            userDto.setSuccess(false);
            return userDto;
        }

        existingUser.setIdentifier(userDto.getUsername());
        modelMapper.map(userDto, existingUser);

        setModifiedDetails(existingUser);

        userRepository.save(existingUser);

        userDto.setSuccess(true);
        userDto.setMessage("User updated successfully");

        return userDto;
    }

    @Override
    @Transactional
    public void delete(String username) {

        User user = userRepository.findByUsername(username);

        if (user == null || Boolean.TRUE.equals(user.getDeleted())) {
            throw new ResourceNotFoundException(
                    "User with username '" + username + "' not found");
        }

        user.setDeleted(true);
        setModifiedDetails(user);
        userRepository.save(user);
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