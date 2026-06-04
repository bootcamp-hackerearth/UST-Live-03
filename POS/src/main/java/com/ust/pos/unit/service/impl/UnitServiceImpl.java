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
    ModelMapper modelMapper;

    @Autowired
    UnitRepository unitRepository;

    @Override
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifier(identifier), UnitDto.class);
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        Unit existing = unitRepository.findByIdentifier(unitDto.getIdentifier());
        if (existing != null) {
            unitDto.setSuccess(false);
            unitDto.setMessage("Unit already exists : " + unitDto.getIdentifier());
            return unitDto;
        }

        Unit unit = modelMapper.map(unitDto, Unit.class);
        unitRepository.save(unit);
        return unitDto;
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        Unit existing = unitRepository.findByIdentifier(unitDto.getIdentifier());
        if (existing == null) {
            unitDto.setSuccess(false);
            unitDto.setMessage("Unit not found : " + unitDto.getIdentifier());
            return unitDto;
        }

        modelMapper.map(unitDto, existing);
        unitRepository.save(existing);
        return unitDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        unitRepository.deleteByIdentifier(identifier);

    }

    @Override
    public List<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage=unitRepository.findAll(pageable);
        return modelMapper.map(unitPage.getContent(), listType);
    }

    @Override
    public UnitDto changeToggleStatus(String identifier, boolean status) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit != null) {
            unit.setStatus(status);
            unitRepository.save(unit);
        }
        return modelMapper.map(unit, UnitDto.class);
    }

    @Override
    public List<UnitDto> findActiveStatus() {
        List<Unit> allUnits = unitRepository.findAll();
        List<Unit> activeUnits = allUnits.stream().filter(Unit::isStatus).toList();
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        return modelMapper.map(activeUnits, listType);
    }
}
