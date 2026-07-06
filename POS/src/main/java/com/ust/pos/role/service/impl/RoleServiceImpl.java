package com.ust.pos.role.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class RoleServiceImpl extends BaseService implements RoleService {

    public static final String ROLE_WITH_IDENTIFIER = "Role with identifier - ";
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public RoleServiceImpl(
            RoleRepository roleRepository,
            ModelMapper modelMapper) {

        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RoleDto findByIdentifier(String identifier) {
        return modelMapper.map(roleRepository.findByIdentifier(identifier), RoleDto.class);
    }

    @Override
    public RoleDto toggleStatus(String identifier, boolean status) {
        Role role = roleRepository.findByIdentifier(identifier);
        if (role != null) {
            role.setStatus(!role.isStatus());
            setModifiedDetails(role);
            roleRepository.save(role);
        }
        return modelMapper.map(role, RoleDto.class);
    }

    @Override
    public RoleDto save(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifier(identifier);
        if (existingRole != null) {
            if (existingRole.isDeleted()) {
                roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " has been soft deleted.");
                roleDto.setSuccess(false);
                return roleDto;
            }
            roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " already exists");
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
            roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " not found");
            roleDto.setSuccess(false);
            return roleDto;
        }
        modelMapper.map(roleDto, existingRole);
        setModifiedDetails(existingRole);
        roleRepository.save(existingRole);
        return roleDto;
    }

    @Override
    public void delete(String identifier) {
        Role role = roleRepository.findByIdentifier(identifier);
        if (role == null) {
            throw new EntityNotFoundException("Role not found");
        }
        softDelete(role);
        setModifiedDetails(role);
        roleRepository.save(role);
    }


    @Override
    public PaginationResponseDto<RoleDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {}.getType();
        PaginationResponseDto<RoleDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Role> roles = roleRepository.findAll();
            response.setDtoList(modelMapper.map(roles, listType));
            response.setTotalRecords(roles.size());
            response.setTotalPages(1);
            response.setSizePerPage(roles.size());
            response.setPage(0);
        } else {
            Page<Role> rolePage = roleRepository.findByDeletedFalse(pageable);
            response.setDtoList(modelMapper.map(rolePage.getContent(), listType));
            response.setTotalRecords(rolePage.getTotalElements());
            response.setTotalPages(rolePage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }

    @Override
    public PaginationResponseDto<RoleDto> findAll(Specification<Role> example, Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Page<Role> page = roleRepository.findAll(example, pageable);
        PaginationResponseDto<RoleDto> paginationresponse = new PaginationResponseDto<>();
        paginationresponse.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationresponse.setTotalRecords(page.getTotalElements());
        paginationresponse.setTotalPages(page.getTotalPages());
        paginationresponse.setSizePerPage(pageable.getPageSize());
        paginationresponse.setPage(pageable.getPageNumber());
        return paginationresponse;
    }
}