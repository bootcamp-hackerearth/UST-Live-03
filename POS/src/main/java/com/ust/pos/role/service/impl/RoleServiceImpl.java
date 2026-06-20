package com.ust.pos.role.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.RoleService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RoleServiceImpl extends CommonService implements RoleService {
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public RoleServiceImpl(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RoleDto findByIdentifier(String identifier) {
        return modelMapper.map(roleRepository.findByIdentifier(identifier), RoleDto.class);
    }

    @Override
    public RoleDto save(RoleDto roleDto) {

        Role existing = roleRepository.findByIdentifier(roleDto.getIdentifier());

        if (existing != null) {
            if (existing.isDeleted()) {
                roleDto.setSuccess(false);
                roleDto.setMessage("Role '" + roleDto.getIdentifier() + "' has been soft deleted.(Rollback by changing status");
                return roleDto;
            }
            roleDto.setSuccess(false);
            roleDto.setMessage("Role '" + roleDto.getIdentifier() + "' already exists");
            return roleDto;
        }

        Role role = modelMapper.map(roleDto, Role.class);
        setAuditFields(role, true);
        roleRepository.save(role);

        roleDto.setSuccess(true);
        roleDto.setMessage("Role added successfully");
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
        setAuditFields(existingRole, false);
        roleRepository.save(existingRole);
        return roleDto;
    }

    @Override
    public boolean delete(String identifier) {
        Role role = roleRepository.findByIdentifier(identifier);
        if (role == null) {
            return false;
        }
        softDelete(role);
        setAuditFields(role, false);
        roleRepository.save(role);
        return true;
    }

    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        WsDto<RoleDto> wsDto = new WsDto<>();
        if (pageable == null) {
            List<Role> roles = roleRepository.findAll();
            wsDto.setDtoList(modelMapper.map(roles, listType));
            wsDto.setTotalRecords(roles.size());
            wsDto.setTotalPages(1);
            wsDto.setSizePerPage(roles.size());
            wsDto.setPage(0);
        } else {
            Page<Role> rolePage = roleRepository.findByDeletedFalse(pageable);
            wsDto.setDtoList(modelMapper.map(rolePage.getContent(), listType));
            wsDto.setTotalRecords(rolePage.getTotalElements());
            wsDto.setTotalPages(rolePage.getTotalPages());
            wsDto.setSizePerPage(pageable.getPageSize());
            wsDto.setPage(pageable.getPageNumber());
        }
        return wsDto;
    }
}