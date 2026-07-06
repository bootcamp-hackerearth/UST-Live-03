package com.ust.pos.user.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
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
@Transactional
public class UserServiceImpl extends BaseService implements UserService {

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
                userDto.setMessage(USER_WITH_USERNAME_EMAIL + username + " has been soft deleted. (Rollback by changing status)");
                userDto.setSuccess(false);
                return userDto;
            }
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + username + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIdentifier(userDto.getUsername());
        setCreatedDetails(user);
        userRepository.save(user);
        userDto.setSuccess(true);
        userDto.setMessage("User created successfully");
        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto) {
        String username = userDto.getUsername();
        Optional<User> userOptional = userRepository.findById(userDto.getId());
        if (userOptional.isEmpty()) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + username + " not found");
            userDto.setSuccess(false);
            return userDto;
        }
        User existingUser = userOptional.get();
        if (!username.equalsIgnoreCase(existingUser.getUsername()) && userRepository.findByUsername(username) != null) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + username + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        modelMapper.map(userDto, existingUser);
        setModifiedDetails(existingUser);
        userRepository.save(existingUser);
        userDto.setSuccess(true);
        userDto.setMessage("User updated successfully");
        return userDto;
    }

    @Override
    public void delete(String identifier) {
        User user = userRepository.findByUsername(identifier);
        softDelete(user);
        setModifiedDetails(user);
        userRepository.save(user);
    }

    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        WsDto<UserDto> wsDto = new WsDto<>();
        if (pageable == null) {
            List<UserDto> userDtoList = modelMapper.map(userRepository.findByDeletedFalse(pageable), listType);
            wsDto.setDtoList(userDtoList);
            wsDto.setTotalRecords(userDtoList.size());
            return wsDto;
        }
        Page<User> userPage = userRepository.findByDeletedFalse(pageable);
        wsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setTotalPages(userPage.getTotalPages());
        wsDto.setTotalRecords(userPage.getTotalElements());
        return wsDto;
    }

    @Override
    public WsDto<UserDto> findAll(Specification<User> specification,
                                  Pageable pageable) {

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();

        Page<User> page =
                userRepository.findAll(specification, pageable);

        WsDto<UserDto> wsDto = new WsDto<>();

        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}