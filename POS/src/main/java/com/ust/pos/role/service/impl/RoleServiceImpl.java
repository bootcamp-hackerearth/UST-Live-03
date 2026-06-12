package com.ust.pos.role.service.impl;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.RoleService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RoleDto findByIdentifier(String identifier) {

        Role role = roleRepository.findByIdentifier(identifier);

        if (role == null) {
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

        String identifier = roleDto.getIdentifier().trim();

        Role existingRole = roleRepository.findByIdentifier(identifier);

        if (existingRole != null) {
            roleDto.setSuccess(false);
            roleDto.setMessage("Role already exists");
            return roleDto;
        }

        Role role = modelMapper.map(roleDto, Role.class);
        roleRepository.save(role);

        roleDto.setSuccess(true);
        roleDto.setMessage("Role saved successfully");

        return roleDto;
    }

    @Override
    public RoleDto update(RoleDto roleDto) {

        String identifier = roleDto.getIdentifier();

        Role existingRole = roleRepository.findByIdentifier(identifier);

        if (existingRole == null) {
            roleDto.setSuccess(false);
            roleDto.setMessage("Role not found");
            return roleDto;
        }

        modelMapper.map(roleDto, existingRole);
        roleRepository.save(existingRole);

        roleDto.setSuccess(true);
        roleDto.setMessage("Role updated successfully");

        return roleDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        roleRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();

        Page<Role> page = roleRepository.findAll(pageable);

        WsDto<RoleDto> ws = new WsDto<>();
        ws.setDtoList(modelMapper.map(page.getContent(), listType));
        ws.setTotalRecords(page.getTotalElements());
        ws.setTotalPages(page.getTotalPages());
        ws.setPage(pageable.getPageNumber());
        ws.setSizePerPage(pageable.getPageSize());

        return ws;
    }
}