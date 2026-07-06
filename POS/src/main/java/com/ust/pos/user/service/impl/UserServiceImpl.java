package com.ust.pos.user.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl extends BaseService implements UserService {

    public static final String USER_WITH_USERNAME_EMAIL = "User with username/email - ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public UserDto findByIdentifier(String identifier) {
        User user = userRepository.findByIdentifierAndDeletedFalse(identifier);
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
            if (Boolean.TRUE.equals(existingUser.getDeleted())) {
                userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " was deleted and cannot be created again.");
                userDto.setSuccess(false);
                return userDto;
            }
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIdentifier(userDto.getUsername());
        setCreatedDetails(user);
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
            if (!username.equalsIgnoreCase(existingUser.getUsername()) && (userRepository.findByIdentifierAndDeletedFalse(username) != null)) {
                userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " already exists");
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
    public void delete(String identifier) {
        User user = userRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(user);
        setModifiedDetails(user);
        userRepository.save(user);
    }

    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findAllByDeletedFalse(pageable);
        WsDto<UserDto> userDto = new WsDto<>();
        userDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userDto.setTotalRecords(userPage.getTotalElements());
        userDto.setTotalPage(userPage.getTotalPages());
        userDto.setSizePerPage(pageable.getPageSize());
        userDto.setPage(pageable.getPageNumber());
        return userDto;
    }

    @Override
    public WsDto<UserDto> findAll(Specification<User> example, Pageable pageable) {

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> page = userRepository.findAll(example, pageable);
        WsDto<UserDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPage(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}
