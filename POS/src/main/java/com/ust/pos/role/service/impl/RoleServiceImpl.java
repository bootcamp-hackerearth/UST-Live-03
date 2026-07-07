package com.ust.pos.role.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.models.Role;
import com.ust.pos.models.RoleRepository;
import com.ust.pos.role.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends BaseService implements RoleService {

    public static final String ROLE_WITH_IDENTIFIER = "Role with identifier - ";

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Override
    public RoleDto findByIdentifier(String identifier) {
        Role role = roleRepository.findByIdentifierAndDeletedFalse(identifier);

        if (role == null) {
            throw new ResourceNotFoundException("Role with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(role, RoleDto.class);
    }

    @Override
    public RoleDto save(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifier(identifier);

        if (existingRole != null) {

            if (Boolean.TRUE.equals(existingRole.getDeleted())) {
                roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " was deleted and cannot be created again.");
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
        Role existingRole = roleRepository.findByIdentifierAndDeletedFalse(identifier);

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
    @Transactional
    public void delete(String identifier) {
        Role role = roleRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(role);
        setModifiedDetails(role);
        roleRepository.save(role);

    }

    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Page<Role> rolePage = roleRepository.findAllByDeletedFalse(pageable);
        WsDto<RoleDto> roleWsDto = new WsDto<>();
        roleWsDto.setDtoList(modelMapper.map(rolePage.getContent(), listType));
        roleWsDto.setTotalRecords(rolePage.getTotalElements());
        roleWsDto.setTotalPages(rolePage.getTotalPages());
        roleWsDto.setSizePerPage(pageable.getPageSize());
        roleWsDto.setPage(pageable.getPageNumber());
        return roleWsDto;
    }

    @Override
    public WsDto<RoleDto> findAll(Specification<Role> example, Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Page<Role> page = roleRepository.findAll(example, pageable);
        WsDto<RoleDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }
}
