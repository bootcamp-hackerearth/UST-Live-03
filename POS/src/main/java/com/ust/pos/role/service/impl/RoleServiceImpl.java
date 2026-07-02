package com.ust.pos.role.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Racks;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.RoleService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RoleServiceImpl extends CommonService implements RoleService {

    private static final String ROLE_WITH_IDENTIFIER = "Role with identifier - ";

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
        roleDto.setIdentifier(roleDto.getIdentifier().trim());
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifier(identifier);
        if (existingRole != null) {
            if (!existingRole.isDeleted()) {
                roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " already exists");
                roleDto.setSuccess(false);
                return roleDto;
            }
            roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            roleDto.setSuccess(false);
            return roleDto;
        }
        Role role = modelMapper.map(roleDto, Role.class);
        setAuditFields(role,true);
        roleRepository.save(role);
        roleDto.setSuccess(true);
        roleDto.setMessage("Role created successfully");
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
        if (role == null) return false;
        softDelete(role);
        setAuditFields(role,false);
        roleRepository.save(role);
        return true;
    }

    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Page<Role> rolePage = roleRepository.findByDeletedFalse(pageable);
        WsDto<RoleDto> roleDtoWsDto = new WsDto<>();
        roleDtoWsDto.setDtoList(modelMapper.map(rolePage.getContent(), listType));
        roleDtoWsDto.setTotalRecords(rolePage.getTotalElements());
        roleDtoWsDto.setTotalPages(rolePage.getTotalPages());
        roleDtoWsDto.setSizePerPage(pageable.getPageSize());
        roleDtoWsDto.setPage(pageable.getPageNumber());
        return roleDtoWsDto;
    }

    @Override
    public List<RoleDto> findIfTrue() {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        return modelMapper.map(roleRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }

    @Override
    public RoleDto toggleStatus(String identifier) {
        Role role = roleRepository.findByIdentifier(identifier);
        role.setStatus(!role.isStatus());
        setAuditFields(role,false);
        roleRepository.save(role);
        return modelMapper.map(role, RoleDto.class);
    }

    @Override
    public WsDto<RoleDto> findAll(Specification<Role> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Page<Role> rolePage = roleRepository.findAll(example, pageable);
        WsDto<RoleDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(rolePage.getContent(), listType));
        wsDto.setTotalRecords(rolePage.getTotalElements());
        wsDto.setTotalPages(rolePage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}