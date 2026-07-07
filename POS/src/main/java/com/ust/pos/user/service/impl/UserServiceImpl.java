package com.ust.pos.user.service.impl;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String USER_WITH_USERNAME_EMAIL = "User with username/email - ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public UserDto findByUserName(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(USER_WITH_USERNAME_EMAIL + username + " not found");
        }
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        String username = userDto.getUsername();
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            if (Boolean.TRUE.equals(existingUser.getIsDeleted())) {
                userDto.setMessage("This user was deleted. Contact admin for further support or try with another username.");
            } else {
                userDto.setMessage(USER_WITH_USERNAME_EMAIL + username + " already exists");
            }
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIsDeleted(false);
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
        if (!username.equalsIgnoreCase(existingUser.getUsername()) && (userRepository.findByUsername(username) != null)) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + username + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        modelMapper.map(userDto, existingUser);
        userRepository.save(existingUser);
        return userDto;
    }

    @Override
    public UserDto delete(String username) {
        UserDto userDto = new UserDto();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + username + " not found");
            userDto.setSuccess(false);
            return userDto;
        }
        user.setIsDeleted(true);
        user.setStatus(false);
        userRepository.save(user);
        userDto.setSuccess(true);
        userDto.setMessage("User deleted successfully");
        return userDto;
    }

    @Override
    public PaginatedResponseDto<UserDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findByIsDeleted(false, pageable);
        List<UserDto> items = modelMapper.map(userPage.getContent(), listType);
        PaginatedResponseDto<UserDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(userPage.getTotalElements());
        response.setTotalPages(userPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public List<UserDto> findAllActive() {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        return modelMapper.map(userRepository.findByStatusAndIsDeleted(true, false), listType);
    }

    @Override
    public void changeStatus(String username, boolean status) {
        User user = userRepository.findByUsername(username);
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    public PaginatedResponseDto<UserDto> findAll(Specification<User> example, Pageable pageable) {

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> page = userRepository.findAll(example, pageable);

        PaginatedResponseDto<UserDto> paginatedResponseDto = new PaginatedResponseDto<>();
        paginatedResponseDto.setItems(modelMapper.map(page.getContent(), listType));
        paginatedResponseDto.setTotalRecords(page.getTotalElements());
        paginatedResponseDto.setTotalPages(page.getTotalPages());
        paginatedResponseDto.setSizePerPage(pageable.getPageSize());
        paginatedResponseDto.setPage(pageable.getPageNumber());

        return paginatedResponseDto;
    }
}