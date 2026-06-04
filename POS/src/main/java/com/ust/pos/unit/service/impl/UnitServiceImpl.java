package com.ust.pos.unit.service.impl;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
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
public class UnitServiceImpl implements UnitService {

    public static final String UNIT_NOT_FOUND = "Unit not found";
    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UnitDto save(UnitDto unitDto) {
        String unitName = unitDto.getUnitName();
        if (unitName == null || unitName.trim().isEmpty()) {
            unitDto.setSuccess(false);
            unitDto.setMessage("Unit name is required");
            return unitDto;
        }
        Unit existing = unitRepository.findByIdentifier(unitName);
        if (existing != null) {
            unitDto.setSuccess(false);
            unitDto.setMessage("Unit already exists");
            return unitDto;
        }
        Unit unit = new Unit();
        unit.setIdentifier(unitName);
        unit.setUnitName(unitName);
        unit.setStatus(Boolean.TRUE.equals(unitDto.getStatus()));
        unitRepository.save(unit);
        unitDto.setIdentifier(unitName);
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
        unit.setStatus(Boolean.TRUE.equals(unitDto.getStatus()));
        unitRepository.save(unit);
        unitDto.setSuccess(true);
        unitDto.setMessage("Unit updated successfully");
        return unitDto;
    }

    @Override
    public void delete(String identifier) {
        unitRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findAll(pageable);
        return modelMapper.map(unitPage.getContent(), listType);
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            UnitDto unitDto = new UnitDto();
            unitDto.setSuccess(false);
            unitDto.setMessage(UNIT_NOT_FOUND);
            return unitDto;
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
        unit.setStatus(!Boolean.TRUE.equals(unit.getStatus()));
        unitRepository.save(unit);
        response = modelMapper.map(unit, UnitDto.class);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

    @Override
    public List<UnitDto> findActiveUnits() {
        return unitRepository.findAll().stream().filter(u -> Boolean.TRUE
                .equals(u.getStatus())).map(u -> modelMapper.map(u, UnitDto.class)).toList();
    }
}