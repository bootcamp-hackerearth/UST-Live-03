package com.ust.pos.user.service.impl;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
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
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() +
                    " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto) {
        String username = userDto.getUsername();
        Optional<User> userOptional = userRepository.findById(userDto.getId());

        if (userOptional.isEmpty()) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL +
                    userDto.getUsername() + " not found");
            userDto.setSuccess(false);
            return userDto;
        } else {
            User existingUser = userOptional.get();
            if (!username.equalsIgnoreCase(existingUser.getUsername()) && (userRepository.findByUsername(username) != null)) {
                userDto.setMessage(USER_WITH_USERNAME_EMAIL
                        + userDto.getUsername() + " already exists");
                userDto.setSuccess(false);
                return userDto;

            }
            modelMapper.map(userDto, existingUser);
            userRepository.save(existingUser);
        }
        return userDto;
    }

    @Override
    public UserDto delete(String username) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String loggedInUsername = authentication.getName();
            User response = userRepository.findByUsername(username);
            UserDto user = modelMapper.map(response, UserDto.class);
            if (username.equals(loggedInUsername)) {
                user.setMessage("Cannot delete the logged in User");
                user.setSuccess(false);
                return user;
            }

            userRepository.deleteByUsername(username);
            return user;
        }
        return null;
    }

    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findAll(pageable);

        WsDto<UserDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());

        return userWsDto;
    }

    @Override
    public UserDto toggleStatus(String identifier) {
        User user = userRepository.findByUsername(identifier);
        user.setStatus(!user.isStatus());
        userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> findIfTrue() {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        return modelMapper.map(userRepository.findByStatusIsTrue(), listType);
    }

    @Override
    public UserDto getUserDetails(String username) {
        User user = userRepository.findByUsername(username);

        UserDto dto = new UserDto();
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setPhoneNo(user.getPhoneNo());
        dto.setRoles(user.getRoles());

        return dto;
    }

}