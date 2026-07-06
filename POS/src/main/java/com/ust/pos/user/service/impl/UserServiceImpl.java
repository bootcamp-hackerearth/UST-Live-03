package com.ust.pos.user.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
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

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        String username = userDto.getUsername();
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            userDto.setMessage(
                    existingUser.isDeleted()
                            ? " User with identifier - " + userDto.getUsername()
                            + " already exists but was deleted, Please contact Administrator."
                            : " User with identifier - " + userDto.getUsername()
                            + " already exists."
            );
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        setCreatedDetails(user);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
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

        if (!username.equalsIgnoreCase(existingUser.getUsername())
                && userRepository.findByUsername(username) != null) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + username + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }

        modelMapper.map(userDto, existingUser);
        setModifiedDetails(existingUser);
        userRepository.save(existingUser);

        return userDto;
    }

    @Override
    @Transactional
    public void delete(String username) {

        User user = userRepository.findByUsername(username);
        setModifiedDetails(user);
        softDelete(user);
    }

    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();

        Page<User> userPage = userRepository.findByIsDeletedFalse(pageable);

        List<UserDto> userDtos = modelMapper.map(
                userPage.getContent(),
                listType
        );

        WsDto<UserDto> wsDto =
                new WsDto<>();

        wsDto.setContent(userDtos);
        wsDto.setPage(userPage.getNumber());
        wsDto.setSizePerPage(userPage.getSize());
        wsDto.setTotalPages(userPage.getTotalPages());
        wsDto.setTotalRecords(userPage.getTotalElements());

        return wsDto;
    }

    @Override
    public WsDto<UserDto> findAll(Specification<User> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findAll(example,pageable);
        WsDto<UserDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(userPage.getContent(), listType));
        wsDto.setTotalRecords(userPage.getTotalElements());
        wsDto.setTotalPages(userPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}