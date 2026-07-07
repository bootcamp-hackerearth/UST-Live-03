package com.ust.pos.user.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.PaginationResponseDto;
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

    private static final String CONST_USER = "User ";
    private static final String DELETED_MESSAGE =
            " has been deleted. Please contact the administrator.";

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
        userDto.setIdentifier(username);
        User existingUser =
                userRepository.findByUsername(username);

        if (existingUser != null) {
            if (existingUser.isDeleted()) {
                userDto.setMessage(
                        CONST_USER + username +
                                DELETED_MESSAGE
                );
                userDto.setSuccess(false);
                return userDto;
            }
            userDto.setMessage(
                    "Email " + username + " already exists"
            );
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(
                passwordEncoder.encode(userDto.getPassword())
        );
        setCreatedDetails(user);
        userRepository.save(user);

        userDto.setMessage("User created successfully");
        userDto.setSuccess(true);

        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto) {

        Optional<User> userOptional =
                userRepository.findById(userDto.getId());

        if (userOptional.isEmpty()) {
            userDto.setMessage(
                    "Email - " + userDto.getUsername() + " not found"
            );
            userDto.setSuccess(false);
            return userDto;
        }

        User existingUser = userOptional.get();

        if (existingUser.isDeleted()) {
            userDto.setMessage(
                    CONST_USER + existingUser.getUsername()
                            + DELETED_MESSAGE
            );
            userDto.setSuccess(false);
            return userDto;
        }

        String username = userDto.getUsername();

        boolean isUsernameChanged =
                !username.equalsIgnoreCase(
                        existingUser.getUsername()
                );

        if (isUsernameChanged) {
            User duplicateUser =
                    userRepository.findByUsername(username);

            if (duplicateUser != null) {
                if (duplicateUser.isDeleted()) {
                    userDto.setMessage(
                            CONST_USER + username
                                    + DELETED_MESSAGE
                    );
                    userDto.setSuccess(false);
                    return userDto;
                }
                userDto.setMessage(
                        "Email - " + username + " already exists"
                );
                userDto.setSuccess(false);
                return userDto;
            }
        }

        String existingPassword =
                existingUser.getPassword();
        modelMapper.map(userDto, existingUser);
        existingUser.setPassword(existingPassword);
        setModifiedDetails(existingUser);
        userRepository.save(existingUser);

        userDto.setMessage("User updated successfully");
        userDto.setSuccess(true);

        return userDto;
    }

    @Override
    @Transactional
    public void delete(String username) {
        User user = userRepository.findByUsername(username);
        softDelete(user);
        setModifiedDetails(user);
        userRepository.save(user);
    }

    @Override
    public PaginationResponseDto<UserDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findByIsDeletedFalse(pageable);

        List<UserDto> userDtoList =
                modelMapper.map(
                        userPage.getContent(),
                        listType
                );

        PaginationResponseDto<UserDto> paginationResponseDto =
                new PaginationResponseDto<>();

        paginationResponseDto.setDtoList(userDtoList);
        paginationResponseDto.setPage(userPage.getNumber());
        paginationResponseDto.setSizePerPage(userPage.getSize());
        paginationResponseDto.setTotalPages(userPage.getTotalPages());
        paginationResponseDto.setTotalRecords(userPage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public PaginationResponseDto<UserDto> findAll(Specification<User> example, Pageable pageable) {

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> page = userRepository.findAll(example, pageable);

        PaginationResponseDto<UserDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationResponseDto.setTotalRecords(page.getTotalElements());
        paginationResponseDto.setTotalPages(page.getTotalPages());
        paginationResponseDto.setSizePerPage(pageable.getPageSize());
        paginationResponseDto.setPage(pageable.getPageNumber());

        return paginationResponseDto;
    }
}
