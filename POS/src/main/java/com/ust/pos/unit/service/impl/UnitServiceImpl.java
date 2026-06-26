package com.ust.pos.unit.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnitServiceImpl extends CommonService implements UnitService {
    private final ModelMapper modelMapper;
    private final UnitRepository unitRepository;

    UnitServiceImpl(ModelMapper modelMapper, UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingmodel = unitRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (existingmodel != null) {
            unitDto.setMessage("Model - " + identifier + " already exists");
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
        Optional<Unit> optionalUnit = unitRepository.findById(unitDto.getId());
        if (optionalUnit.isEmpty()) {
            unitDto.setSuccess(false);
            return unitDto;
        } else {
            Unit existingUnit = optionalUnit.get();
            if (!identifier.equals(existingUnit.getIdentifier()) &&
                    unitRepository.findByIdentifierAndIsDeleteFalse(identifier) != null) {
                unitDto.setSuccess(false);
                unitDto.setMessage("Model Already Exists");
                return unitDto;
            } else {
                modelMapper.map(unitDto, existingUnit);
                setAuditFields(existingUnit, false);
                unitRepository.save(existingUnit);
                unitDto.setSuccess(true);
            }
            return unitDto;

        }
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.
                findByIdentifierAndIsDeleteFalse(identifier), UnitDto.class);
    }

    @Override
    public List<UnitDto> findAll() {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        return modelMapper.map(unitRepository.findByIsDeleteFalse(), listType);
    }

    @Override
    public void delete(String identifier) {
        Unit unit = unitRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (unit != null) {
            unit.setDelete(true);
            setAuditFields(unit, false);
            unitRepository.save(unit);
        }
    }

    @Override
    public void updateStatusOnly(String identifier, boolean status) {
        Unit unit = unitRepository.findByIdentifierAndIsDeleteFalse(identifier);
        unit.setStatus(status);
        setAuditFields(unit, false);
        unitRepository.save(unit);
    }

    @Override
    public List<UnitDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findByIsDeleteFalse(pageable);
        return modelMapper.map(unitPage.getContent(), listOfType);
    }

    @Override
    public Page<UnitDto> findAll(Pageable pageable, String search) {
        Page<Unit> unitPage;
        if (search != null && !search.trim().isEmpty()) {
            unitPage = unitRepository.findByIdentifierContainingIgnoreCaseAndIsDeleteFalse
                    (search, pageable);
        } else {
            unitPage = unitRepository.findByIsDeleteFalse(pageable);
        }
        return unitPage.map(unit -> modelMapper.map(unit, UnitDto.class));
    }
}