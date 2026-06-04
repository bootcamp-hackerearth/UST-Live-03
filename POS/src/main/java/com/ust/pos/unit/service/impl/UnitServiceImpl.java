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
public class UnitServiceImpl implements UnitService {

    @Autowired
    UnitRepository unitRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public List<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findAll(pageable);
        return modelMapper.map(unitPage.getContent(), listType);
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifier(identifier), UnitDto.class);
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit != null) {
            unitDto.setMessage("Unit - " + identifier + " already exists");
            unitDto.setSuccess(false);
            return unitDto;
        }
        Unit unit = modelMapper.map(unitDto, Unit.class);
        unitRepository.save(unit);
        return unitDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        unitRepository.deleteByIdentifier(identifier);
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit == null) {
            unitDto.setMessage("Unit with unit - " + identifier + " not found");
            unitDto.setSuccess(false);
            return unitDto;
        }
        modelMapper.map(unitDto, existingUnit);
        unitRepository.save(existingUnit);
        return unitDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit != null) {
            unit.setStatus(!unit.isStatus());
            unitRepository.save(unit);
        }
    }

    @Override
    public List<Unit> findActiveUnit() {
        return unitRepository.findByStatus(true);
    }
}
