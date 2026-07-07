package com.ust.pos.unit.service.impl;

import com.ust.pos.CommonService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
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
public class UnitServiceImpl extends CommonService implements UnitService {

    private static final String UNIT_WITH_IDENTIFIER = "Unit with identifier - ";

    private final UnitRepository unitRepository;
    private final ModelMapper modelMapper;

    public UnitServiceImpl(UnitRepository unitRepository, ModelMapper modelMapper) {
        this.unitRepository = unitRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        Unit unit =unitRepository.findByIdentifier(identifier);
        if(unit==null){
            throw new ResourceNotFoundException("Data Cannot Found");
        }
        return modelMapper.map(unit, UnitDto.class);
    }

    @Override
    public UnitDto save(UnitDto dto) {

        if (dto == null || dto.getIdentifier() == null) {
            throw new IllegalArgumentException("Identifier is required");
        }

        String identifier = dto.getIdentifier();
        Unit existing = unitRepository.findByIdentifier(identifier);

        if (existing != null) {
            if (!existing.getDeleted()) {
                dto.setSuccess(false);
                dto.setMessage(UNIT_WITH_IDENTIFIER + identifier + " already exists");
                return dto;
            }

            dto.setSuccess(false);
            dto.setMessage(UNIT_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        Unit unit = modelMapper.map(dto, Unit.class);
        setAuditFields(unit, true);

        unitRepository.save(unit);

        dto.setSuccess(true);
        dto.setMessage("Unit created successfully");

        return dto;
    }

    @Override
    public UnitDto update(UnitDto dto) {

        String identifier = dto.getIdentifier();
        Unit existing = unitRepository.findByIdentifier(identifier);

        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage(UNIT_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        if (existing.getDeleted()) {
            dto.setSuccess(false);
            dto.setMessage(UNIT_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        modelMapper.map(dto, existing);
        setAuditFields(existing, false);

        unitRepository.save(existing);

        dto.setSuccess(true);
        dto.setMessage("Unit updated successfully");

        return dto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        Unit unit = unitRepository.findByIdentifier(identifier);

        if (unit != null) {
            softDelete(unit);
            setAuditFields(unit, false);
            unitRepository.save(unit);
        }
    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        Page<Unit> page = unitRepository.findByDeletedFalse(pageable);

        WsDto<UnitDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public WsDto<UnitDto> findAll(Specification<Unit> example, Pageable pageable) {

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> page = unitRepository.findAll(example, pageable);

        WsDto<UnitDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public UnitDto toggleStatus(String identifier) {

        Unit unit = unitRepository.findByIdentifier(identifier);

        if (unit == null) {
            UnitDto dto = new UnitDto();
            dto.setSuccess(false);
            dto.setMessage(UNIT_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        unit.setStatus(!unit.getStatus());
        setAuditFields(unit, false);

        unitRepository.save(unit);

        return modelMapper.map(unit, UnitDto.class);
    }

    @Override
    public List<UnitDto> findIfTrue() {

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        return modelMapper.map(
                unitRepository.findByStatusIsTrueAndDeletedFalse(),
                listType
        );
    }
}
