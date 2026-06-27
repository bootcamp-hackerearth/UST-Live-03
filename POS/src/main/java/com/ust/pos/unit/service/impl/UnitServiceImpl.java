package com.ust.pos.unit.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Unit;
import com.ust.pos.modell.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl extends BaseService implements UnitService {

    private final ModelMapper modelMapper;
    private final UnitRepository unitRepository;

    @Override
    public UnitDto findByIdentifier(String identifier) {
        Unit unit = unitRepository.findByIdentifierAndDeletedFalse(identifier);

        if (unit == null) {
            throw new ResourceNotFoundException("Unit with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(unit, UnitDto.class);
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);

        if (existingUnit != null) {
            if (Boolean.TRUE.equals(existingUnit.getDeleted())) {
                unitDto.setMessage(" Unit with identifier - " + identifier + " was deleted and cannot be created again.");
                unitDto.setSuccess(false);
                return unitDto;
            }
            unitDto.setMessage(" Unit with identifier - " + identifier + " already exists");
            unitDto.setSuccess(false);
            return unitDto;
        }
        Unit unit = modelMapper.map(unitDto, Unit.class);
        setCreatedDetails(unit);
        unitRepository.save(unit);
        return unitDto;
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingUnit == null) {
            unitDto.setMessage(
                    "Unit with identifier - " + identifier + " not found");
            unitDto.setSuccess(false);
            return unitDto;
        }
        modelMapper.map(unitDto, existingUnit);
        setModifiedDetails(existingUnit);
        unitRepository.save(existingUnit);
        return unitDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Unit unit = unitRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(unit);
        setModifiedDetails(unit);
        unitRepository.save(unit);
    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findAllByDeletedFalse(pageable);
        WsDto<UnitDto> unitWsDto = new WsDto<>();
        unitWsDto.setDtoList(modelMapper.map(unitPage.getContent(), listType));
        unitWsDto.setTotalRecords(unitPage.getTotalElements());
        unitWsDto.setTotalPage(unitPage.getTotalPages());
        unitWsDto.setSizePerPage(pageable.getPageSize());
        unitWsDto.setPage(pageable.getPageNumber());
        return unitWsDto;
    }

    @Override
    @Transactional
    public UnitDto toggleStatus(String identifier) {
        Unit unit = unitRepository.findByIdentifierAndDeletedFalse(identifier);

        if (unit == null) {
            throw new NullPointerException("Unit not found with identifier: " + identifier);
        }

        Boolean currentStatus = unit.getStatus();
        unit.setStatus(currentStatus == null ? Boolean.TRUE : !currentStatus);
        setModifiedDetails(unit);
        Unit saved = unitRepository.save(unit);
        return modelMapper.map(saved, UnitDto.class);
    }

    @Override
    public List<UnitDto> findAllActive() {
        return unitRepository.findByStatusTrueAndDeletedFalse()
                .stream()
                .map(unit -> modelMapper.map(unit, UnitDto.class))
                .toList();
    }

}