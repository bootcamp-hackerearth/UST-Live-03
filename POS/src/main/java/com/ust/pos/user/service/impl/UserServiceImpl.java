package com.ust.pos.user.service.impl;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

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
        return modelMapper.map(userRepository.findByUsernameAndDeletedFalse(username), UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        String username = userDto.getUsername();
        User existingUser = userRepository.findByUsernameAndDeletedFalse(username);
        if (existingUser != null) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setDeleted(false);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
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
            if (!username.equalsIgnoreCase(existingUser.getUsername()) && (userRepository.findByUsernameAndDeletedFalse(username) != null)) {
                    userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " already exists");
                    userDto.setSuccess(false);
                    return userDto;
            }
            modelMapper.map(userDto, existingUser);
            userRepository.save(existingUser);
        }
        return userDto;
    }

    @Override
    public void delete(String username) {
        User user = userRepository.findByUsernameAndDeletedFalse(username);
        if(user != null)
        {
            user.setDeleted(true);
            userRepository.save(user);
        }
    }

    @Override
    public List<UserDto> findAll() {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        return modelMapper.map(userRepository.findByDeletedFalse(), listType);
    }

    @Override
    public Page<UserDto> findAll(Example<User> example, Pageable pageable) {
        Page<User> userPage = userRepository.findAll(example, pageable);
        return userPage.map(user -> modelMapper.map(user, UserDto.class));
    }


    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {
        Type listtype = new TypeToken<List<UserDto>>(){}.getType();
        Page<User> userPage = userRepository.findByDeletedFalse(pageable);

        WsDto<UserDto> userDtoWsDto = new WsDto<>();
        userDtoWsDto.setDtoList(modelMapper.map(userPage.getContent(), listtype));
        userDtoWsDto.setTotalRecords(userPage.getTotalElements());
        userDtoWsDto.setTotalPage(userPage.getTotalPages());
        userDtoWsDto.setPage(pageable.getPageNumber());

        return userDtoWsDto;
    }
}
