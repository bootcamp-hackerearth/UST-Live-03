package com.ust.pos.role.service.impl;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final String ROLE_WITH_IDENTIFIER = "Role with identifier - ";

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Override
    public RoleDto findByIdentifier(String identifier) {
        return modelMapper.map(roleRepository.findByIdentifier(identifier), RoleDto.class);
    }

    @Override
    public RoleDto save(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifier(identifier);

        if (existingRole != null) {
            if (Boolean.TRUE.equals(existingRole.getIsDeleted())) {
                roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " was deleted. Contact admin for further support or try with a different identifier.");
            } else {
                roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " already exists");
            }
            roleDto.setSuccess(false);
            return roleDto;
        }

        Role role = modelMapper.map(roleDto, Role.class);
        role.setIsDeleted(false);
        roleRepository.save(role);
        return roleDto;
    }

    @Override
    public RoleDto update(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifier(identifier);

        if (existingRole == null) {
            roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " not found");
            roleDto.setSuccess(false);
            return roleDto;
        }

        modelMapper.map(roleDto, existingRole);
        roleRepository.save(existingRole);
        return roleDto;
    }

    @Override
    public RoleDto delete(String identifier) {
        RoleDto roleDto = new RoleDto();
        Role role = roleRepository.findByIdentifier(identifier);

        if (role == null) {
            roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " not found");
            roleDto.setSuccess(false);
            return roleDto;
        }

        role.setIsDeleted(true);
        role.setStatus(false);
        roleRepository.save(role);
        roleDto.setSuccess(true);
        roleDto.setMessage("Role deleted successfully");
        return roleDto;
    }

    @Override
    public PaginatedResponseDto<RoleDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Page<Role> rolePage = roleRepository.findByIsDeleted(false, pageable);
        List<RoleDto> items = modelMapper.map(rolePage.getContent(), listType);
        PaginatedResponseDto<RoleDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(rolePage.getTotalElements());
        response.setTotalPages(rolePage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public List<RoleDto> findAllActive() {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        return modelMapper.map(roleRepository.findByStatusAndIsDeleted(true, false), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Role role = roleRepository.findByIdentifier(identifier);
        role.setStatus(status);
        roleRepository.save(role);
    }
}