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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class RoleServiceImpl extends BaseService implements RoleService {

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
        String identifier = roleDto.getIdentifier().trim();
        Role existingRole = roleRepository.findByIdentifier(identifier);
        if (existingRole != null) {
            if (existingRole.isDeleted()) {
                roleDto.setMessage("Role with identifier " + identifier + " has been soft deleted. (Rollback by changing status)");
                roleDto.setSuccess(false);
                return roleDto;
            }
            roleDto.setMessage("Role with identifier - " + identifier + " already exists");
            roleDto.setSuccess(false);
            return roleDto;
        }
        Role role = modelMapper.map(roleDto, Role.class);
        setCreatedDetails(role);
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
            roleDto.setMessage("Role with identifier - " + identifier + " not found");
            roleDto.setSuccess(false);
            return roleDto;
        }
        modelMapper.map(roleDto, existingRole);
        setModifiedDetails(existingRole);
        roleRepository.save(existingRole);
        roleDto.setSuccess(true);
        roleDto.setMessage("Role updated successfully");
        return roleDto;
    }

    @Override
    public void delete(String identifier) {
        Role role = roleRepository.findByIdentifier(identifier);
        softDelete(role);
        setModifiedDetails(role);
        roleRepository.save(role);
    }

    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        WsDto<RoleDto> wsDto = new WsDto<>();
        if (pageable == null) {
            List<RoleDto> roleDtoList = modelMapper.map(roleRepository.findByDeletedFalse(pageable), listType);
            wsDto.setDtoList(roleDtoList);
            wsDto.setTotalRecords(roleDtoList.size());
            return wsDto;
        }
        Page<Role> rolePage = roleRepository.findByDeletedFalse(pageable);
        wsDto.setDtoList(modelMapper.map(rolePage.getContent(), listType));
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setTotalPages(rolePage.getTotalPages());
        wsDto.setTotalRecords(rolePage.getTotalElements());
        return wsDto;
    }

    @Override
    public WsDto<RoleDto> findAll(Specification<Role> specification,
                                  Pageable pageable) {

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();

        Page<Role> page =
                roleRepository.findAll(specification, pageable);

        WsDto<RoleDto> wsDto = new WsDto<>();

        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}