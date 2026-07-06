package com.ust.pos.user.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        User warehouse = userRepository.findByUsername(username);
        if (warehouse == null) {
            throw new ResourceNotFoundException("User with username '" + username + "' not found");
        }
        return modelMapper.map(warehouse, UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        String username = userDto.getUsername();
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            if (existingUser.isDeleted()) {
                userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() + " was previously deleted. " +
                        "Please contact backend team to restore."
                );
                userDto.setSuccess(false);
                return userDto;
            }
            userDto.setMessage(USER_WITH_USERNAME_EMAIL + userDto.getUsername() +
                    " already exists");
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
            setAuditFields(existingUser, false);
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

            User userEntity = userRepository.findByUsername(username);

            if (userEntity == null) {
                UserDto response = new UserDto();
                response.setMessage("User not found");
                response.setSuccess(false);
                return response;
            }

            UserDto userDto = modelMapper.map(userEntity, UserDto.class);

            if (username.equals(loggedInUsername)) {
                userDto.setMessage("Cannot delete the logged in User");
                userDto.setSuccess(false);
                return userDto;
            }
            softDelete(userEntity);
            setAuditFields(userEntity, false);
            userRepository.save(userEntity);

            userDto.setMessage("User deleted successfully");
            userDto.setSuccess(true);

            return userDto;
        }

        return null;
    }


    @Override
    public WsDto<UserDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> userPage = userRepository.findByDeletedFalse(pageable);

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
        setAuditFields(user, false);
        userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> findIfTrue() {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        return modelMapper.map(userRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }

    @Override
    public UserDto getUserDetails(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException("User with username " + username + " not found");
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setIdentifier(user.getIdentifier());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setPhoneNo(user.getPhoneNo());
        dto.setRoles(user.getRoles());

        return dto;
    }

    @Override
    public WsDto<UserDto> findAll(Specification<User> example, Pageable pageable) {

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<User> page = userRepository.findAll(example, pageable);

        WsDto<UserDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}