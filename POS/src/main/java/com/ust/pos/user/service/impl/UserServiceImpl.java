package com.ust.pos.user.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.UserDto;
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
            if (Boolean.TRUE.equals(existingUser.getDeleted())) {
                userDto.setMessage("User with identifier - " + username + " has been soft deleted. Restore it by changing status.");
                userDto.setSuccess(false);
                return userDto;
            }
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setDeleted(false);
        user.setStatus(true);
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
    public boolean delete(String identifier) {

        User user = userRepository.findByIdentifier(identifier);

        if (user == null) {
            return false;
        }
        softDelete(user);
        setAuditFields(user, false);
        userRepository.save(user);
        return true;
    }

    @Override
    public PageDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findByDeletedFalse(pageable);
        PageDto<UserDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        pageDto.setTotalRecords(userPage.getTotalElements());
        pageDto.setTotalPages(userPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public PageDto<UserDto> findAll(Specification<User> spec, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findAll(spec, pageable);
        PageDto<UserDto> PageDto = new PageDto<>();
        PageDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        PageDto.setTotalRecords(userPage.getTotalElements());
        PageDto.setTotalPages(userPage.getTotalPages());
        PageDto.setSizePerPage(pageable.getPageSize());
        PageDto.setPage(pageable.getPageNumber());
        PageDto.setKeyword(keyword);
        return PageDto;
    }
}
