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
        return modelMapper.map(
                roleRepository.findByIdentifierAndDeletedFalse(identifier),
                RoleDto.class
        );
    }

    @Override
    public RoleDto save(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifier(identifier);
        if (existingRole != null) {
            if (Boolean.TRUE.equals(existingRole.getDeleted())) {
                roleDto.setMessage("Role - " + identifier + " was deleted and cannot be recreated");
            } else {
                roleDto.setMessage("Role with identifier - " + identifier + " already exists");
            }
            roleDto.setSuccess(false);
            return roleDto;
        }
        Role role = modelMapper.map(roleDto, Role.class);
        setCreatedDetails(role);
        roleRepository.save(role);
        roleDto.setSuccess(true);
        return roleDto;
    }

    @Override
    public RoleDto update(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingRole == null) {
            roleDto.setMessage("Role with identifier - " + identifier + " not found");
            roleDto.setSuccess(false);
            return roleDto;
        }
        modelMapper.map(roleDto, existingRole);
        setModifiedDetails(existingRole);
        roleRepository.save(existingRole);
        roleDto.setSuccess(true);
        return roleDto;
    }

    @Override
    public void delete(String identifier) {
        Role role = roleRepository.findByIdentifierAndDeletedFalse(identifier);
        if (role != null) {
            softDelete(role);
            setModifiedDetails(role);
            roleRepository.save(role);
        }
    }

    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Page<Role> rolePage = roleRepository.findByDeletedFalse(pageable);
        WsDto<RoleDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(rolePage.getContent(), listType));
        wsDto.setTotalRecords(rolePage.getTotalElements());
        wsDto.setTotalPages(rolePage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }
}