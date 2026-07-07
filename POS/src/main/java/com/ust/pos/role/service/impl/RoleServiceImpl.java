package com.ust.pos.role.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
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

    private static final String VALIDATION_MESSAGE = "Role with identifier - ";
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
            throw new ResourseNotFoundException("Data cannot found");
        }

        return modelMapper.map(role, RoleDto.class);
    }

    @Override
    public List<RoleDto> findActiveRoles() {

        List<Role> roles = roleRepository.findByStatus(true);
        return roles.stream().map(role -> modelMapper.map(role, RoleDto.class)).toList();
    }

    @Override
    public RoleDto save(RoleDto roleDto) {

        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifier(identifier);

        if (existingRole != null) {
            roleDto.setMessage(
                    existingRole.isDeleted()
                            ? VALIDATION_MESSAGE + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : VALIDATION_MESSAGE + identifier
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
            roleDto.setMessage(VALIDATION_MESSAGE + identifier + " not found");
            roleDto.setSuccess(false);
            return roleDto;
        }

        modelMapper.map(roleDto, existingRole);
        setModifiedDetails(existingRole);
        roleRepository.save(existingRole);

        return roleDto;
    }

    @Override
    public void toggleStatus(String identifier) {

        Role role = roleRepository.findByIdentifier(identifier);

        if (role != null) {
            role.setStatus(!role.isStatus());
            roleRepository.save(role);
        }
    }

    @Override
    @Transactional
    public boolean delete(String identifier) {

        if (identifier == null) {
            return false;
        }
        identifier = identifier.replace("\"", "");
        Role role = roleRepository.findByIdentifier(identifier);
        setModifiedDetails(role);
        softDelete(role);

        return true;
    }


    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {

        Page<Role> rolePage = roleRepository.findByIsDeletedFalse(pageable);

        WsDto<RoleDto> rolesDto = new WsDto<>();

        List<RoleDto> rolesDtos = rolePage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, RoleDto.class))
                .toList();

        rolesDto.setContent(rolesDtos);
        rolesDto.setPage(rolePage.getNumber());
        rolesDto.setSizePerPage(rolePage.getSize());
        rolesDto.setTotalPages(rolePage.getTotalPages());
        rolesDto.setTotalRecords(rolePage.getTotalElements());

        return rolesDto;
    }

    @Override
    public WsDto<RoleDto> findAll(Specification<Role> example, Pageable pageable) {

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Page<Role> page = roleRepository.findAll(example, pageable);

        WsDto<RoleDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}