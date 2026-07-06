package com.ust.pos.unit.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
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

    private final UnitRepository unitRepository;

    private final ModelMapper modelMapper;

    public UnitServiceImpl(UnitRepository unitRepository, ModelMapper modelMapper) {
        this.unitRepository = unitRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit != null) {
            if (existingUnit.isDeleted()) {
                unitDto.setMessage("Unit with identifier - " + identifier + "has been soft deleted.(Rollback by changing status");
                unitDto.setSuccess(false);
                return unitDto;
            }
            unitDto.setSuccess(false);
            unitDto.setMessage("Unit with this identifier" + unitDto.getIdentifier() + "already exist");
            return unitDto;
        }
        Unit unit = modelMapper.map(unitDto, Unit.class);
        setAuditFields(unit, true);
        Unit savedUnit = unitRepository.save(unit);
        return modelMapper.map(savedUnit, UnitDto.class);

    }

    @Override
    public UnitDto findById(Long id) {

        Unit unit = unitRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unit not found with id " + id));
        return modelMapper.map(unit, UnitDto.class);

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
    public WsDto<UnitDto> findAll(Specification<Unit> spec, Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {}.getType();
        Page<Unit> page = unitRepository.findAll(spec, pageable);
        WsDto<UnitDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public void delete(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        softDelete(unit);
        setAuditFields(unit, false);
        unitRepository.save(unit);
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        Unit unit = unitRepository.findByIdentifier(unitDto.getIdentifier());
        if (unit == null) {
            unitDto.setMessage("Unit with identifier" + unitDto.getIdentifier() + "not found");
            unitDto.setSuccess(false);
            return unitDto;
        }
        if (!unitDto.getIdentifier().equalsIgnoreCase(unit.getIdentifier()) && unitRepository.existsByIdentifier(unitDto.getIdentifier())) {
            unitDto.setMessage("This unit already exist!");
            unitDto.setSuccess(false);
            return unitDto;
        }
        modelMapper.map(unitDto, unit);
        setAuditFields(unit, false);
        unitRepository.save(unit);
        return unitDto;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            throw new ResourceNotFoundException("Unit with identifier '" + identifier + "'not found");
        }
        return modelMapper.map(unit, UnitDto.class);
    }
}