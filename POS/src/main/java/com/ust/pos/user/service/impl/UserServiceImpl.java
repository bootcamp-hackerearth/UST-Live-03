package com.ust.pos.user.service.impl;

import com.ust.pos.CommonService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl extends CommonService implements UserService {

    public static final String USER_WITH_USERNAME_EMAIL = "User with username - ";

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
        if (user == null) return null;
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        String username = userDto.getUsername();
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            if(existingUser.isDeleted()){
                userDto.setMessage("User with userEmail - " + username + " not available");
                userDto.setSuccess(false);
                return userDto;
            }
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + username + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        setAuditFields(user,true);
        userRepository.save(user);
        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto) {
        Optional<User> userOptional = userRepository.findById(userDto.getId());
        if (userOptional.isEmpty()) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " not found");
            userDto.setSuccess(false);
            return userDto;
        }
        User existingUser = userOptional.get();
        String username = userDto.getUsername();
        if (!username.equalsIgnoreCase(existingUser.getUsername())
                && userRepository.findByUsername(username) != null) {
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + username + " already exists");
            userDto.setSuccess(false);
            return userDto;
        }
        existingUser.setName(userDto.getName());
        existingUser.setPhoneNo(userDto.getPhoneNo());
        existingUser.setRoles(userDto.getRoles());
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        setAuditFields(existingUser,false);
        userRepository.save(existingUser);
        return userDto;
    }

    @Override
    public void delete(String username) {
        User user=userRepository.findByUsername(username.trim());
        softDelete(user);
        setAuditFields(user,false);    }

    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {}.getType();
        Page<User> userPage = userRepository.findByIsDeletedFalse(pageable);
        WsDto<UserDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());
        return userWsDto;
    }

    @Override
    public UserDto findByIdentifier(String identifier) {
        User user = userRepository.findByIdentifierAndIsDeletedFalse(identifier);
        if (user == null) {
            throw new ResourceNotFoundException("User with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserDetails(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setName(user.getName());
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPhoneNo(user.getPhoneNo());
        dto.setRoles(user.getRoles());
        return dto;
    }

    @Override
    public WsDto<UserDto> findAll(Specification<User> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> page = userRepository.findAll(example, pageable);
        WsDto<UserDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}