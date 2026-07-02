package com.ust.pos.unit.service.impl;

import com.ust.pos.api.BaseService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnitServiceImpl extends BaseService implements UnitService {
    private final ModelMapper modelMapper;
    private final UnitRepository unitRepository;

    public UnitServiceImpl(UnitRepository unitRepository, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.unitRepository = unitRepository;
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingmodel = unitRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingmodel != null) {
            unitDto.setMessage("Model - " + identifier + " already exists");
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
        Optional<Unit> optionalUnit = unitRepository.findById(unitDto.getId());
        if (optionalUnit.isEmpty()) {
            unitDto.setSuccess(false);
            return unitDto;
        } else {
            Unit existingmodel = optionalUnit.get();
            if (!identifier.equals(existingmodel.getIdentifier()) && unitRepository.findByIdentifierAndDeletedFalse(identifier) != null) {
                unitDto.setSuccess(false);
                unitDto.setMessage("Model Already Exists");
                return unitDto;
            } else {
                modelMapper.map(unitDto, existingmodel);
                unitRepository.save(existingmodel);
                unitDto.setSuccess(true);
            }
            return unitDto;

        }
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifierAndDeletedFalse(identifier), UnitDto.class);
    }

    @Override
    public List<UnitDto> findAll() {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        return modelMapper.map(unitRepository.findAll(), listType);
    }

    @Override
    public void delete(String identifier) {
        Unit unit = unitRepository.findByIdentifierAndDeletedFalse(identifier);
        if (unit != null) {
            unit.setDeleted(true);
            unitRepository.save(unit);
        }
    }

    @Override
    public void updateStatusOnly(String identifier, boolean status) {
        Unit unit = unitRepository.findByIdentifierAndDeletedFalse(identifier);
        unit.setStatus(status);
        unitRepository.save(unit);
    }

    @Override
    public Page<UnitDto> findAll(String search, Pageable pageable) {
        Page<Unit> units;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Unit> specification =
                    buildGlobalSearchSpec(Unit.class, search);
            units = unitRepository.findAll(specification, pageable);
        } else {
            units = unitRepository.findByDeletedFalse(pageable);
        }

        return units.map(unit ->
                modelMapper.map(unit, UnitDto.class));
    }
}