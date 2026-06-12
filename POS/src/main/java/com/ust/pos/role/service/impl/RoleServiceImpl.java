package com.ust.pos.role.service.impl;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.RoleService;
import jakarta.transaction.Transactional;
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
    public RoleDto save(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier().trim();
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

    @Transactional
    @Override
    public void delete(String identifier) {
        roleRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        if (pageable == null) {
            List<RoleDto> roleDtoList = modelMapper.map(roleRepository.findAll(), listType);
            WsDto<RoleDto> response = new WsDto<>();
            response.setDtoList(roleDtoList);
            response.setTotalRecords(roleDtoList.size());
            return response;
        }
        Page<Role> rolePage = roleRepository.findAll(pageable);
        List<RoleDto> roleDtoList = modelMapper.map(rolePage.getContent(), listType);
        WsDto<RoleDto> wsDto = new WsDto<>();
        wsDto.setDtoList(roleDtoList);
        wsDto.setPage(rolePage.getNumber());
        wsDto.setSizePerPage(rolePage.getSize());
        wsDto.setTotalPages(rolePage.getTotalPages());
        wsDto.setTotalRecords(rolePage.getTotalElements());
        return wsDto;
    }

}