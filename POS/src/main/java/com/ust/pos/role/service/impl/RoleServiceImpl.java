package com.ust.pos.role.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.RoleService;
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
public class RoleServiceImpl extends CommonService implements RoleService {

    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

    RoleServiceImpl(ModelMapper modelMapper, RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RoleDto findByIdentifier(String identifier) {
        return modelMapper.map(roleRepository.
                findByIdentifierAndIsDeleteFalse(identifier), RoleDto.class);
    }

    @Override
    public RoleDto save(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.
                findByIdentifierAndIsDeleteFalse(identifier);
        if (existingRole != null) {
            roleDto.setMessage("Role with identifier - " + identifier + " already exists");
            roleDto.setSuccess(false);
            return roleDto;
        }
        Role role = modelMapper.map(roleDto, Role.class);
        setAuditFields(role, true);
        roleRepository.save(role);
        return roleDto;
    }

    @Override
    public RoleDto update(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.
                findByIdentifierAndIsDeleteFalse(identifier);
        if (existingRole == null) {
            roleDto.setMessage("Role with identifier - " + identifier + " not found");
            roleDto.setSuccess(false);
            return roleDto;
        }
        roleDto.setSuccess(true);
        modelMapper.map(roleDto, existingRole);
        setAuditFields(existingRole, false);
        roleRepository.save(existingRole);
        return roleDto;
    }

    @Override
    public void delete(String identifier) {
        Role role = roleRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (role != null) {
            role.setDelete(true);
            setAuditFields(role, false);
            roleRepository.save(role);
        }
    }

    @Override
    public List<RoleDto> findAll() {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        return modelMapper.map(roleRepository.findByIsDeleteFalse(), listType);
    }

    @Override
    public Page<RoleDto> findAll(Pageable pageable, String search) {
        Page<Role> roles;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Role> specification = buildGlobalSearchSpec(Role.class, search);
            roles = roleRepository.findAll(specification, pageable);
        } else {
            roles = roleRepository.findByIsDeleteFalse(pageable);
        }

        return roles.map(role -> modelMapper.map(role, RoleDto.class));
    }
}
