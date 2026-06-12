package com.ust.pos.role.service.impl;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RoleDto;
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
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RoleDto findByIdentifier(String identifier) {
        return modelMapper.map(roleRepository.findByIdentifier(identifier), RoleDto.class);
    }

    @Override
    public RoleDto toggleStatus(String identifier, boolean status) {
        Role role = roleRepository.findByIdentifier(identifier);
        if (role != null) {
            role.setStatus(!role.isStatus());
            roleRepository.save(role);
        }
        return modelMapper.map(role, RoleDto.class);
    }

    @Override
    public RoleDto save(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifier(identifier);
        if (existingRole != null) {
            roleDto.setMessage("Role with identifier - " + identifier + " already exists");
            roleDto.setSuccess(false);
            return roleDto;
        }
        Role role = modelMapper.map(roleDto, Role.class);
        roleRepository.save(role);
        return roleDto;
    }

    @Override
    public RoleDto update(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifier(identifier);
        if (existingRole == null) {
            roleDto.setMessage("Role with identifier - " + identifier + " not found");
            roleDto.setSuccess(false);
            return roleDto;
        }
        modelMapper.map(roleDto, existingRole);
        roleRepository.save(existingRole);
        return roleDto;
    }

    @Override
    public void delete(String identifier) {
        roleRepository.deleteByIdentifier(identifier);
    }


    @Override
    public PaginationResponseDto<RoleDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {}.getType();
        PaginationResponseDto<RoleDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Role> roles = roleRepository.findAll();
            response.setDtoList(modelMapper.map(roles, listType));
            response.setTotalRecords((long) roles.size());
            response.setTotalPages(1);
            response.setSizePerPage(roles.size());
            response.setPage(0);
        } else {
            Page<Role> rolePage = roleRepository.findAll(pageable);
            response.setDtoList(modelMapper.map(rolePage.getContent(), listType));
            response.setTotalRecords(rolePage.getTotalElements());
            response.setTotalPages(rolePage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }
}