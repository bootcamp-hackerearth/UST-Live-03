package com.ust.pos.role.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.RoleService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RoleServiceImpl extends BaseService implements RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public RoleServiceImpl(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RoleDto findByIdentifier(String identifier) {
        Role role = roleRepository.findByIdentifier(identifier);

        if (role == null) {
            throw new ResourceNotFoundException(
                    "Role with identifier '" + identifier + "' not found");
        }

        return modelMapper.map(role, RoleDto.class);
    }

    @Override
    public RoleDto save(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifier(identifier);
        if (existingRole != null) {
            roleDto.setMessage(
                    existingRole.isDeleted()
                            ? " Role with identifier - " + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : " Role with identifier - " + identifier
                            + " already exists."
            );
            roleDto.setSuccess(false);
            return roleDto;
        }
        Role role = modelMapper.map(roleDto, Role.class);
        setCreatedDetails(role);
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
        setModifiedDetails(existingRole);
        roleRepository.save(existingRole);
        return roleDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        Role role = roleRepository.findByIdentifier(identifier);
        setModifiedDetails(role);
        softDelete(role);
    }

    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();

        Page<Role> rolePage = roleRepository.findByIsDeletedFalse(pageable);

        List<RoleDto> roleDtos = modelMapper.map(
                rolePage.getContent(),
                listType
        );

        WsDto<RoleDto> wsDto =
                new WsDto<>();

        wsDto.setContent(roleDtos);
        wsDto.setPage(rolePage.getNumber());
        wsDto.setSizePerPage(rolePage.getSize());
        wsDto.setTotalPages(rolePage.getTotalPages());
        wsDto.setTotalRecords(rolePage.getTotalElements());

        return wsDto;
    }

    @Override
    public WsDto<RoleDto> findAll(Specification<Role> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Page<Role> rolePage = roleRepository.findAll(example,pageable);
        WsDto<RoleDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(rolePage.getContent(), listType));
        wsDto.setTotalRecords(rolePage.getTotalElements());
        wsDto.setTotalPages(rolePage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}