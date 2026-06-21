package com.ust.pos.unit.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class UnitServiceImpl extends CommonService implements UnitService {
    public static final String UNIT_WITH_IDENTIFIER = "Unit with identifier - ";
    private final UnitRepository unitRepository;
    private final ModelMapper modelMapper;

    public UnitServiceImpl(UnitRepository unitRepository, ModelMapper modelMapper) {
        this.unitRepository = unitRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifier(identifier), UnitDto.class);
    }

    @Override
    public UnitDto toggleStatus(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        unit.setStatus(!unit.isStatus());
        unitRepository.save(unit);
        return modelMapper.map(unit, UnitDto.class);
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit != null) {
            if (existingUnit.isDeleted()) {
                unitDto.setMessage(UNIT_WITH_IDENTIFIER + identifier + " has been soft deleted.(Rollback by changing status");
                unitDto.setSuccess(false);
                return unitDto;
            }
            unitDto.setMessage(UNIT_WITH_IDENTIFIER + identifier + " already exists");
            unitDto.setSuccess(false);
            return unitDto;
        }
        Unit unit = modelMapper.map(unitDto, Unit.class);
        setAuditFields(unit, true);
        unitRepository.save(unit);
        return unitDto;
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit == null) {
            unitDto.setMessage(UNIT_WITH_IDENTIFIER + identifier + " not found");
            unitDto.setSuccess(false);
            return unitDto;
        }
        modelMapper.map(unitDto, existingUnit);
        setAuditFields(existingUnit, false);
        unitRepository.save(existingUnit);
        return unitDto;
    }

    @Override
    public boolean delete(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            return false;
        }
        softDelete(unit);
        setAuditFields(unit, false);
        unitRepository.save(unit);
        return true;
    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findByDeletedFalse(pageable);
        WsDto<UnitDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(unitPage.getContent(), listType));
        wsDto.setTotalRecords(unitPage.getTotalElements());
        wsDto.setTotalPages(unitPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public List<UnitDto> findIfTrue() {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        return modelMapper.map(unitRepository.findByStatusIsTrue(), listType);
    }
}