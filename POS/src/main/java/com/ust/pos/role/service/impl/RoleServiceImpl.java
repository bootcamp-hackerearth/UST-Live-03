package com.ust.pos.role.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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

@Transactional
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
    public RoleDto save(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifier(identifier);
        if (existingRole != null) {
            roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " already exists");
            roleDto.setSuccess(false);
            return roleDto;
        }
        Role role = modelMapper.map(roleDto, Role.class);
        setAuditFields(role, true);
        roleRepository.save(role);
        roleDto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " successfully Created...!");
        return roleDto;
    }

    @Override
    public RoleDto findByIdentifier(String identifier) {
        Role role = roleRepository.findByIdentifier(identifier);
        if(role==null){
            throw new ResourceNotFoundException("Role with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(role,RoleDto.class);
    }

    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {
        Page<Role> rolePage = roleRepository.findByDeletedFalse(pageable);
        Type type = new TypeToken<List<RoleDto>>() {
        }.getType();
        WsDto<RoleDto> roleWsDto = new WsDto<>();
        roleWsDto.setDtoList(modelMapper.map(rolePage.getContent(), type));
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

    @Override
    public List<RoleDto> findAllActive() {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        return modelMapper.map(roleRepository.findAllByStatusAndDeletedFalse(true), listType);
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
        setAuditFields(existingRole, false);
        roleRepository.save(existingRole);
        return roleDto;
    }

    @Override
    public RoleDto toggleStatus(String identifier) {
        Role role = roleRepository.findByIdentifier(identifier);
        role.setStatus(!role.isStatus());
        setAuditFields(role, false);
        roleRepository.save(role);
        return modelMapper.map(role, RoleDto.class);
    }

    @Override
    public boolean delete(String identifier) {
        Role role = roleRepository.findByIdentifier(identifier);
        if (role == null) return false;
        softDelete(role);
        setAuditFields(role, false);
        roleRepository.save(role);
        return true;
    }
}
