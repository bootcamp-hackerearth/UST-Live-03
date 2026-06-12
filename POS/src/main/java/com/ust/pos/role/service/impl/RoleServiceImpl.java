package com.ust.pos.role.service.impl;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.RoleService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RoleDto findByIdentifier(String identifier) {
        return modelMapper.map(roleRepository.findByIdentifier(identifier), RoleDto.class);
    }

    @Override
    public RoleDto save(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifier(identifier);
        if (existingRole != null) {
            roleDto.setMessage("Role with identifier - " + identifier + " already exists");
            roleDto.setSuccess(false);
            return roleDto;
        }
        Role role = modelMapper.map(roleDto, Role.class);
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
        roleRepository.save(existingRole);
        return roleDto;
    }

    @Override
    public boolean delete(String identifier) {
        roleRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Page<Role> rolePage = roleRepository.findAll(pageable);
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
        return modelMapper.map(roleRepository.findByStatusIsTrue(), listType);
    }

    @Override
    public RoleDto toggleStatus(String identifier) {
        Role role = roleRepository.findByIdentifier(identifier);
        role.setStatus(!role.isStatus());
        roleRepository.save(role);
        return modelMapper.map(role, RoleDto.class);
    }
}