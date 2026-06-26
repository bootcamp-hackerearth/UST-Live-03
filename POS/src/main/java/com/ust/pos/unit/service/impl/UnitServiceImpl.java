package com.ust.pos.unit.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class UnitServiceImpl extends BaseService implements UnitService {

    public static final String UNIT_NOT_FOUND = "Unit not found";
    public static final String UNIT_WITH_IDENTIFIER = "Unit with identifier ";
    public static final String HAS_BEEN_SOFT_DELETED_ROLLBACK_BY_CHANGING_STATUS = " has been soft deleted. (Rollback by changing status)";

    private final UnitRepository unitRepository;
    private final ModelMapper modelMapper;

    public UnitServiceImpl(UnitRepository unitRepository, ModelMapper modelMapper) {
        this.unitRepository = unitRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String unitName = unitDto.getUnitName();
        if (unitName == null || unitName.trim().isEmpty()) {
            unitDto.setSuccess(false);
            unitDto.setMessage("Unit name is required");
            return unitDto;
        }

        String identifier = unitName.trim();
        Unit existing = unitRepository.findByIdentifier(identifier);

        if (existing != null) {

            if (Boolean.TRUE.equals(existing.getDeleted())) {
                unitDto.setMessage( UNIT_WITH_IDENTIFIER + identifier +
                                HAS_BEEN_SOFT_DELETED_ROLLBACK_BY_CHANGING_STATUS);
                unitDto.setSuccess(false);
                return unitDto;
            }

            unitDto.setSuccess(false);
            unitDto.setMessage("Unit already exists");
            return unitDto;
        }

        Unit unit = new Unit();
        unit.setIdentifier(identifier);
        unit.setUnitName(identifier);
        unit.setStatus(Boolean.TRUE.equals(unitDto.getStatus()));
        setCreatedDetails(unit);
        unitRepository.save(unit);
        unitDto.setIdentifier(identifier);
        unitDto.setSuccess(true);
        unitDto.setMessage("Unit added successfully");
        return unitDto;
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        if (identifier == null || identifier.trim().isEmpty()) {
            unitDto.setSuccess(false);
            unitDto.setMessage("Invalid identifier");
            return unitDto;
        }

        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            unitDto.setSuccess(false);
            unitDto.setMessage(UNIT_NOT_FOUND);
            return unitDto;
        }

        if (Boolean.TRUE.equals(unit.getDeleted())) {
            unitDto.setSuccess(false);
            unitDto.setMessage(UNIT_WITH_IDENTIFIER + identifier +
                            HAS_BEEN_SOFT_DELETED_ROLLBACK_BY_CHANGING_STATUS);
            return unitDto;
        }

        unit.setStatus(Boolean.TRUE.equals(unitDto.getStatus()));
        setModifiedDetails(unit);
        unitRepository.save(unit);
        unitDto.setSuccess(true);
        unitDto.setMessage("Unit updated successfully");
        return unitDto;
    }

    @Override
    public void delete(String identifier) {

        Unit unit = unitRepository.findByIdentifier(identifier);

        if (unit == null) {
            return;
        }

        softDelete(unit);
        setModifiedDetails(unit);
        unitRepository.save(unit);
    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findByDeletedFalse(pageable);
        WsDto<UnitDto> unitWsDto = new WsDto<>();
        unitWsDto.setDtoList(modelMapper.map(unitPage.getContent(), listType));
        unitWsDto.setTotalRecords(unitPage.getTotalElements());
        unitWsDto.setTotalPages(unitPage.getTotalPages());
        unitWsDto.setSizePerPage(pageable.getPageSize());
        unitWsDto.setPage(pageable.getPageNumber());
        return unitWsDto;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null || Boolean.TRUE.equals(unit.getDeleted())) {
            UnitDto dto = new UnitDto();
            dto.setSuccess(false);
            dto.setMessage(UNIT_NOT_FOUND);
            return dto;
        }
        return modelMapper.map(unit, UnitDto.class);
    }

    @Override
    public UnitDto toggleStatus(String identifier) {
        UnitDto response = new UnitDto();
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            response.setSuccess(false);
            response.setMessage(UNIT_NOT_FOUND);
            return response;
        }

        if (Boolean.TRUE.equals(unit.getDeleted())) {
            response.setSuccess(false);
            response.setMessage(UNIT_WITH_IDENTIFIER + identifier +
                            HAS_BEEN_SOFT_DELETED_ROLLBACK_BY_CHANGING_STATUS);
            return response;
        }

        unit.setStatus(!Boolean.TRUE.equals(unit.getStatus()));
        setModifiedDetails(unit);
        unitRepository.save(unit);
        response = modelMapper.map(unit, UnitDto.class);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

    @Override
    public List<UnitDto> findActiveUnits() {
        return unitRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getStatus()) && !Boolean.TRUE.equals(u.getDeleted()))
                .map(u -> modelMapper.map(u, UnitDto.class))
                .toList();
    }
}