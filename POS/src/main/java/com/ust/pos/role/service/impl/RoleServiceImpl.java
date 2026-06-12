package com.ust.pos.role.service.impl;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RoleDto;
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
@Transactional
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

        roleDto.setSuccess(true);
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
    @Transactional
    public RoleDto updateStatus(String identifier, boolean status) {
        RoleDto response = new RoleDto();

        Role role = roleRepository.findByIdentifier(identifier);
        if (role == null) {
            response.setSuccess(false);
            response.setMessage("Role not found");
            return response;
        }

        role.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Override
    public void delete(String identifier) {
        roleRepository.deleteByIdentifier(identifier);
    }

    @Override
    public PaginationResponseDto<RoleDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        if (pageable == null) {

            List<RoleDto> roleDtoList =
                    modelMapper.map(roleRepository.findAll(), listType);

            PaginationResponseDto<RoleDto> response =
                    new PaginationResponseDto<>();

            response.setDtoList(roleDtoList);
            response.setTotalRecords(roleDtoList.size());

            return response;
        }
        Page<Role> rolePage = roleRepository.findAll(pageable);
        List<RoleDto> roleDtoList = modelMapper.map(rolePage.getContent(), listType);

        PaginationResponseDto<RoleDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(roleDtoList);
        paginationResponseDto.setPage(rolePage.getNumber());
        paginationResponseDto.setSizePerPage(rolePage.getSize());
        paginationResponseDto.setTotalPages(rolePage.getTotalPages());
        paginationResponseDto.setTotalRecords(rolePage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public List<RoleDto> findByStatusTrue() {
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        return modelMapper.map(roleRepository.findByStatusTrue(), listType);
    }
}
