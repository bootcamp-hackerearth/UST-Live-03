package com.ust.pos.role.service.impl;

import com.ust.pos.CommonService;
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
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RoleServiceImpl extends CommonService implements RoleService {

    private static final String ROLE_WITH_IDENTIFIER = "Role with identifier - ";

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public RoleServiceImpl(RoleRepository roleRepository,
                           ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RoleDto findByIdentifier(String identifier) {
        return modelMapper.map(roleRepository.findByIdentifier(identifier), RoleDto.class);
    }

    @Override
    public RoleDto save(RoleDto dto) {

        if (dto == null || dto.getIdentifier() == null) {
            throw new IllegalArgumentException("Identifier is required");
        }

        String identifier = dto.getIdentifier();
        Role existing = roleRepository.findByIdentifier(identifier);

        if (existing != null) {
            if (!existing.isDeleted()) {
                dto.setSuccess(false);
                dto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " already exists");
                return dto;
            }

            dto.setSuccess(false);
            dto.setMessage(ROLE_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        Role role = modelMapper.map(dto, Role.class);
        setAuditFields(role, true);

        roleRepository.save(role);

        dto.setSuccess(true);
        dto.setMessage("Role created successfully");

        return dto;
    }

    @Override
    public RoleDto update(RoleDto dto) {

        String identifier = dto.getIdentifier();
        Role existing = roleRepository.findByIdentifier(identifier);

        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        if (existing.isDeleted()) {
            dto.setSuccess(false);
            dto.setMessage(ROLE_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        modelMapper.map(dto, existing);
        setAuditFields(existing, false);

        roleRepository.save(existing);

        dto.setSuccess(true);
        dto.setMessage("Role updated successfully");

        return dto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        Role role = roleRepository.findByIdentifier(identifier);

        if (role != null) {
            softDelete(role);
            setAuditFields(role, false);
            roleRepository.save(role);
        }
    }

    @Override
    public WsDto<RoleDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Page<Role> page = roleRepository.findByDeletedFalse(pageable);

        WsDto<RoleDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public RoleDto toggleStatus(String identifier) {

        Role role = roleRepository.findByIdentifier(identifier);

        if (role == null) {
            RoleDto dto = new RoleDto();
            dto.setSuccess(false);
            dto.setMessage(ROLE_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        role.setStatus(!role.isStatus());
        setAuditFields(role, false);

        roleRepository.save(role);

        return modelMapper.map(role, RoleDto.class);
    }

    @Override
    public List<RoleDto> findIfTrue() {

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();

        return modelMapper.map(
                roleRepository.findByStatusIsTrueAndDeletedFalse(),
                listType
        );
    }
}