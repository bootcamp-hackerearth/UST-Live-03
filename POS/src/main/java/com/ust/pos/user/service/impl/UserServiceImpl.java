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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends BaseService implements UserService {

    public static final String USER_WITH_USERNAME_EMAIL = "User with username/email - ";
    public static final String USER_WITH_USERNAME_EMAIL1 = "User with username/email - ";
    public static final String USER_WITH_USERNAME_EMAIL2 = "User with username/email - ";

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
            throw new ResourceNotFoundException("Data cannot found");
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
                            ? USER_WITH_USERNAME_EMAIL + username + " already exists but was deleted, Please contact Administrator"
                            : USER_WITH_USERNAME_EMAIL + username + " already exists"
            );

            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        setCreatedDetails(user);
        userRepository.save(user);
        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto) {
        String username = userDto.getUsername();
        Optional<User> userOptional = userRepository.findById(userDto.getId());

        if (userOptional.isEmpty()) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL1 + userDto.getUsername() + " not found");
            userDto.setSuccess(false);
            return userDto;
        } else {
            User existingUser = userOptional.get();
            if (!username.equalsIgnoreCase(existingUser.getUsername()) && userRepository.findByUsername(username) != null) {

                userDto.setMessage(USER_WITH_USERNAME_EMAIL2 + userDto.getUsername() + " already exists");
                userDto.setSuccess(false);
                return userDto;
            }

            modelMapper.map(userDto, existingUser);
            setModifiedDetails(existingUser);
            userRepository.save(existingUser);
        }
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

        WsDto<UserDto> userWsDto = new WsDto<>();
        userWsDto.setContent(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());

        return userWsDto;
    }

    @Override
    public boolean getCurrentUser(String username) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }

        String loggedInUsername = authentication.getName();

        return loggedInUsername.equals(username);
    }

    @Override
    public WsDto<UserDto> findAll(Specification<User> example, Pageable pageable) {

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> page = userRepository.findAll(example, pageable);

        WsDto<UserDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}
