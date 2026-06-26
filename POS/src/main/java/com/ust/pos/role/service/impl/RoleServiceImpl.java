package com.ust.pos.role.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.RoleService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class RoleServiceImpl extends BaseService implements RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public RoleServiceImpl(RoleRepository roleRepository,
                           ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RoleDto findByIdentifier(String identifier) {

        Role role = roleRepository.findByIdentifier(identifier);

        if (role == null || Boolean.TRUE.equals(role.getDeleted())) {
            RoleDto dto = new RoleDto();
            dto.setSuccess(false);
            dto.setMessage("Role not found");
            return dto;
        }

        RoleDto dto = modelMapper.map(role, RoleDto.class);
        dto.setSuccess(true);

        return dto;
    }

    @Override
    public RoleDto save(RoleDto roleDto) {

        if (roleDto.getIdentifier() == null ||
                roleDto.getIdentifier().trim().isEmpty()) {

            roleDto.setSuccess(false);
            roleDto.setMessage("Role identifier is required");
            return roleDto;
        }

        String identifier = roleDto.getIdentifier().trim();

        Role existingRole = roleRepository.findByIdentifier(identifier);

        if (existingRole != null &&
                !Boolean.TRUE.equals(existingRole.getDeleted())) {

            roleDto.setSuccess(false);
            roleDto.setMessage("Role already exists");
            return roleDto;
        }

        Role role = modelMapper.map(roleDto, Role.class);

        role.setIdentifier(identifier);
        role.setDeleted(false);

        setCreatedDetails(role);

        roleRepository.save(role);

        roleDto.setSuccess(true);
        roleDto.setMessage("Role saved successfully");

        return roleDto;
    }

    @Override
    public RoleDto update(RoleDto roleDto) {

        Role existingRole =
                roleRepository.findByIdentifier(roleDto.getIdentifier());

        if (existingRole == null ||
                Boolean.TRUE.equals(existingRole.getDeleted())) {

            roleDto.setSuccess(false);
            roleDto.setMessage("Role not found");
            return roleDto;
        }

        modelMapper.map(roleDto, existingRole);

        setModifiedDetails(existingRole);

        roleRepository.save(existingRole);

        roleDto.setSuccess(true);
        roleDto.setMessage("Role updated successfully");

        return roleDto;
    }

    @Override
    public void delete(String identifier) {

        Role role = roleRepository.findByIdentifier(identifier);

        if (role == null ||
                Boolean.TRUE.equals(role.getDeleted())) {
            return;
        }

        role.setDeleted(true);

        setModifiedDetails(role);

        roleRepository.save(role);
    }

    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();

        Page<Role> page =
                roleRepository.findByDeletedFalse(pageable);

        WsDto<RoleDto> ws = new WsDto<>();

        ws.setDtoList(
                modelMapper.map(page.getContent(), listType)
        );
        ws.setTotalRecords(page.getTotalElements());
        ws.setTotalPages(page.getTotalPages());
        ws.setPage(pageable.getPageNumber());
        ws.setSizePerPage(pageable.getPageSize());

        return ws;
    }
}