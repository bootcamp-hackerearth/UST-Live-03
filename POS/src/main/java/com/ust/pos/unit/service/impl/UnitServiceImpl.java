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
    public UnitDto findByIdentifier(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            throw new ResourceNotFoundException("Unit with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(unit, UnitDto.class);
    }

    @Override
    public UnitDto toggleStatus(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        unit.setStatus(!unit.isStatus());
        setAuditFields(unit, false);
        unitRepository.save(unit);
        return modelMapper.map(unit, UnitDto.class);
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit != null) {
            if (existingUnit.isDeleted()) {
                unitDto.setMessage("Unit with identifier " + identifier + " was previously deleted. " +
                        "Please contact backend team to restore."
                );
                unitDto.setSuccess(false);
                return unitDto;
            }
            unitDto.setMessage("Unit with identifier - " + identifier + " already exists");
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
            unitDto.setMessage("Unit with identifier - " + identifier + " not found");
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
        if (unit == null) return false;
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
        WsDto<UnitDto> unitWsDto = new WsDto<>();
        unitWsDto.setDtoList(modelMapper.map(unitPage.getContent(), listType));
        unitWsDto.setTotalRecords(unitPage.getTotalElements());
        unitWsDto.setTotalPages(unitPage.getTotalPages());
        unitWsDto.setSizePerPage(pageable.getPageSize());
        unitWsDto.setPage(pageable.getPageNumber());

        return unitWsDto;
    }

    @Override
    public List<UnitDto> findIfTrue() {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        return modelMapper.map(unitRepository.findByStatusIsTrueAndDeletedFalse(), listType);
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
}