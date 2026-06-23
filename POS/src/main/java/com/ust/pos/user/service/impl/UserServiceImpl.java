package com.ust.pos.user.service.impl;

import com.ust.pos.CommonService;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends CommonService implements UserService {

    private static final String USER_WITH_USERNAME = "User with username - ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository,
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
    public UserDto findByIdentifier(String identifier) {
        return modelMapper.map(userRepository.findByIdentifier(identifier), UserDto.class);
    }

    @Override
    public UserDto save(UserDto dto) {

        if (dto == null || dto.getUsername() == null) {
            throw new IllegalArgumentException("Username is required");
        }

        String username = dto.getUsername();
        User existing = userRepository.findByUsername(username);

        if (existing != null) {
            if (!existing.isDeleted()) {
                dto.setSuccess(false);
                dto.setMessage(USER_WITH_USERNAME + username + " already exists");
                return dto;
            }

            dto.setSuccess(false);
            dto.setMessage(USER_WITH_USERNAME + username + " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        User user = modelMapper.map(dto, User.class);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        setAuditFields(user, true);

        userRepository.save(user);

        dto.setSuccess(true);
        dto.setMessage("User created successfully");

        return dto;
    }

    @Override
    public UserDto update(UserDto dto) {

        String username = dto.getUsername();
        User existing = userRepository.findByUsername(username);

        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage(USER_WITH_USERNAME + username + " not found");
            return dto;
        }

        if (existing.isDeleted()) {
            dto.setSuccess(false);
            dto.setMessage(USER_WITH_USERNAME + username + " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        var roles = existing.getRoles();

        modelMapper.map(dto, existing);

        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            if (existing.getRoles() == null) {
                existing.setRoles(new ArrayList<>());
            }
            if (roles != null) {
                existing.getRoles().addAll(roles);
            }
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        setAuditFields(existing, false);

        userRepository.save(existing);

        dto.setSuccess(true);
        dto.setMessage("User updated successfully");

        return dto;
    }

    @Transactional
    @Override
    public void delete(String username) {

        User user = userRepository.findByUsername(username);

        if (user != null) {
            softDelete(user);
            setAuditFields(user, false);
            userRepository.save(user);
        }
    }

    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> page = userRepository.findByDeletedFalse(pageable);

        WsDto<UserDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public UserDto toggleStatus(String identifier) {

        User user = userRepository.findByIdentifier(identifier);

        if (user == null) {
            UserDto dto = new UserDto();
            dto.setSuccess(false);
            dto.setMessage(USER_WITH_USERNAME + identifier + " not found");
            return dto;
        }

        user.setStatus(!user.isStatus());
        setAuditFields(user, false);

        userRepository.save(user);

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> findIfTrue() {

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();

        return modelMapper.map(
                userRepository.findByStatusIsTrueAndDeletedFalse(),
                listType
        );
    }
}