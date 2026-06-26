package com.ust.pos.role.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.RoleDto;
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

    public static final String ROLE_WITH_IDENTIFIER = "Role with identifier - ";
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
    public void toggleStatus(String identifier) {
        Role role = roleRepository.findByIdentifier(identifier);
        if (role != null) {
            boolean currentStatus = Boolean.TRUE.equals(role.getStatus());
            role.setStatus(!currentStatus);

            roleRepository.save(role);
        }
    }

    @Override
    public List<RoleDto> findActiveRoles() {
        Type listType = new TypeToken<List<RoleDto>>() {}.getType();
        return modelMapper.map(roleRepository.findByStatusTrue(),listType);
    }

    @Override
    public RoleDto save(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifier(identifier);
        if (existingRole != null) {
            if (Boolean.TRUE.equals(existingRole.getDeleted())) {
                roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " has been soft deleted. Restore it by changing status.");
                roleDto.setSuccess(false);
                return roleDto;
            }
            roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " already exists");
            roleDto.setSuccess(false);
            return roleDto;
        }
        Role role = modelMapper.map(roleDto, Role.class);
        role.setDeleted(false);
        role.setStatus(true);
        setAuditFields(role, true);
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
        setAuditFields(existingRole,false);
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
    public PageDto<RoleDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Page<Role> rolePage = roleRepository.findByDeletedFalse(pageable);
        PageDto<RoleDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(rolePage.getContent(), listType));
        pageDto.setTotalRecords(rolePage.getTotalElements());
        pageDto.setTotalPages(rolePage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }
}
