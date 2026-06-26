package com.ust.pos.unit.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
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

    private final UnitRepository unitRepository;
    private final ModelMapper modelMapper;

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit != null) {
            if (Boolean.TRUE.equals(existingUnit.getDeleted())) {
                unitDto.setMessage("Unit identifier - " + identifier + " not available");
                unitDto.setSuccess(false);
                return unitDto;
            }
            unitDto.setMessage("Unit with identifier - " + identifier + " already exists");
            unitDto.setSuccess(false);
            return unitDto;
        }
        Unit unit = modelMapper.map(unitDto, Unit.class);
        setCreatedDetails(unit);
        setModifiedDetails(unit);
        unitRepository.save(unit);
        return unitDto;
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit == null) {
            unitDto.setMessage("unit with identifier - " + identifier + " not found");
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
        setModifiedDetails(unit);
        softDelete(unit);
    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findAllByDeletedFalse(pageable);

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
        return modelMapper.map(unitRepository.findByIdentifierAndDeletedFalse(identifier), UnitDto.class);
    }

    @Override
    public List<UnitDto> findActiveUnits() {
        List<Unit> unit = unitRepository.findByStatusTrueAndDeletedFalse();
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        return modelMapper.map(unit, listType);
    }

    @Override
    public UnitDto toggleStatus(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            return null;
        }
        unit.setStatus(!unit.isStatus());
        setModifiedDetails(unit);
        unitRepository.save(unit);
        return modelMapper.map(unit, UnitDto.class);
    }
}
