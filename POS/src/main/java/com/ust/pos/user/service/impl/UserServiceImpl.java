package com.ust.pos.user.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.UserDto;
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
public class UserServiceImpl extends CommonService implements UserService {

    public static final String USER_WITH_USERNAME_EMAIL = "User with username/email - ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                    ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto findByUserName(String username) {
        User user = userRepository.findByUsernameAndIsDeleteFalse(username);
        if (user == null) {
            return null;
        }
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        String username = userDto.getUsername();
        User existingUser = userRepository.findByUsernameAndIsDeleteFalse(username);
        if (existingUser != null) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
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
            if (!username.equalsIgnoreCase(existingUser.getUsername()) &&
                    userRepository.findByUsernameAndIsDeleteFalse(username) != null) {

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
    public void delete(String username) {
        User user = userRepository.findByUsernameAndIsDeleteFalse(username);
        if (user != null) {
            user.setDelete(true);
            setAuditFields(user, false);
            userRepository.save(user);
        }
    }

    @Override
    public List<UserDto> findAll() {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        return modelMapper.map(userRepository.findByIsDeleteFalse(), listType);
    }

    @Override
    public Page<UserDto> findAll(Pageable pageable) {
        Page<User> userPage = userRepository.findByIsDeleteFalse(pageable);
        return userPage.map(product ->
                modelMapper.map(product, UserDto.class));
    }

    @Override
    public Page<UserDto> findAll(Pageable pageable, String search) {
        Page<User> users;

        if (search != null && !search.trim().isEmpty()) {
            Specification<User> specification = buildGlobalSearchSpec(User.class, search);
            users = userRepository.findAll(specification, pageable);
        } else {
            users = userRepository.findByIsDeleteFalse(pageable);
        }

        return users.map(user -> modelMapper.map(user, UserDto.class));
    }
}
