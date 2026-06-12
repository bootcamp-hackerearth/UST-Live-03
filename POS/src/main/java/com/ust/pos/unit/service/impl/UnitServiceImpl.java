package com.ust.pos.unit.service.impl;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
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
    private UnitRepository unitRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifier(identifier), UnitDto.class);
    }

    @Override
    public UnitDto toggleStatus(String identifier) {
        Unit unit=unitRepository.findByIdentifier(identifier);
        unit.setStatus(!unit.isStatus());
        unitRepository.save(unit);
        return modelMapper.map(unit, UnitDto.class);
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit != null) {
            unitDto.setMessage("Unit with identifier - " + identifier + " already exists");
            unitDto.setSuccess(false);
            return unitDto;
        }
        Unit unit = modelMapper.map(unitDto, Unit.class);
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
        unitRepository.save(existingUnit);
        return unitDto;
    }

    @Override
    public boolean delete(String identifier) {
        unitRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public List<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage=unitRepository.findAll(pageable);
        return modelMapper.map(unitPage.getContent(), listType);
    }

    @Override
    public List<UnitDto> findIfTrue() {
        Type listType = new TypeToken<List<UnitDto>>(){
        }.getType();
        return modelMapper.map(unitRepository.findByStatusIsTrue(), listType);
    }
}