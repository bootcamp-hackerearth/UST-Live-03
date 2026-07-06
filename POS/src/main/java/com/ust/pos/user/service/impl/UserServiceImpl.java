package com.ust.pos.user.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
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

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ModelMapper modelMapper) {

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
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            if (existingUser.isDeleted()) {
                userDto.setMessage(USER_WITH_USERNAME_EMAIL + username +
                                " has been soft deleted. (Rollback by changing status)");
                userDto.setSuccess(false);
                return userDto;
            }
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        setCreatedDetails(user);
        userRepository.save(user);
        userDto.setSuccess(true);
        userDto.setMessage("User added successfully");
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
            String existingPassword = existingUser.getPassword();
            modelMapper.map(userDto, existingUser);
            existingUser.setPassword(existingPassword);
            setModifiedDetails(existingUser);
            userRepository.save(existingUser);
            userDto.setSuccess(true);
            userDto.setMessage("User updated successfully");
        }
        return userDto;
    }

    @Override
    public void delete(String identifier) {
        User user = userRepository.findByUsername(identifier);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        softDelete(user);
        setModifiedDetails(user);
        userRepository.save(user);
    }


    @Override
    public PaginationResponseDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {}.getType();
        PaginationResponseDto<UserDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<User> users = userRepository.findAll();
            response.setDtoList(modelMapper.map(users, listType));
            response.setTotalRecords(users.size());
            response.setTotalPages(1);
            response.setSizePerPage(users.size());
            response.setPage(0);
        } else {
            Page<User> userPage = userRepository.findByDeletedFalse(pageable);
            response.setDtoList(modelMapper.map(userPage.getContent(), listType));
            response.setTotalRecords(userPage.getTotalElements());
            response.setTotalPages(userPage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }

    @Override
    public PaginationResponseDto<UserDto> findAll(Specification<User> example, Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> page = userRepository.findAll(example, pageable);
        PaginationResponseDto<UserDto> paginationresponse = new PaginationResponseDto<>();
        paginationresponse.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationresponse.setTotalRecords(page.getTotalElements());
        paginationresponse.setTotalPages(page.getTotalPages());
        paginationresponse.setSizePerPage(pageable.getPageSize());
        paginationresponse.setPage(pageable.getPageNumber());
        return paginationresponse;
    }
}