package com.ust.pos.role.service.impl;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.RoleService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final ModelMapper modelMapper;

    public RoleServiceImpl(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RoleDto findByIdentifier(String identifier) {
        return modelMapper.map(roleRepository.findByIdentifierAndDeletedFalse(identifier), RoleDto.class);
    }

    @Override
    public RoleDto save(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingRole != null) {
            roleDto.setMessage("Role with identifier - " + identifier + " already exists");
            roleDto.setSuccess(false);
            return roleDto;
        }
        Role role = modelMapper.map(roleDto, Role.class);
        role.setDeleted(false);
        roleRepository.save(role);
        return roleDto;
    }

    @Override
    public RoleDto update(RoleDto roleDto) {
        String identifier = roleDto.getIdentifier();
        Role existingRole = roleRepository.findByIdentifierAndDeletedFalse(identifier);
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
    public void delete(String identifier) {
        Role role = roleRepository.findByIdentifierAndDeletedFalse(identifier);
        if(role != null)
        {
            role.setDeleted(true);
            roleRepository.save(role);
        }
    }

    @Override
    public Page<RoleDto> findAll(Example<Role> example, Pageable pageable) {
        Page<Role> rolePage = roleRepository.findAll(example, pageable);
        return rolePage.map(role -> modelMapper.map(role, RoleDto.class));
    }

    @Override
    public List<RoleDto> findAll() {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        return modelMapper.map(roleRepository.findByDeletedFalse(), listType);
    }

    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Page<Role> rolePage = roleRepository.findByDeletedFalse(pageable);

        WsDto<RoleDto> roleDtoWsDto = new WsDto<>();
        roleDtoWsDto.setDtoList(modelMapper.map(rolePage.getContent(), listType));
        roleDtoWsDto.setTotalRecords(rolePage.getTotalElements());
        roleDtoWsDto.setTotalPage(rolePage.getTotalPages());
        roleDtoWsDto.setSizePerPage(pageable.getPageSize());
        roleDtoWsDto.setPage(pageable.getPageNumber());

        return roleDtoWsDto;
    }
}
