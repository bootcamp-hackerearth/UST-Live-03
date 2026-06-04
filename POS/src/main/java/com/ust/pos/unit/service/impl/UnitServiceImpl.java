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
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnitServiceImpl implements UnitService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UnitRepository unitRepository;

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingmodel = unitRepository.findByIdentifier(identifier);
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
            if (!identifier.equals(existingmodel.getIdentifier()) && unitRepository.findByIdentifier(identifier) != null) {
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
        return modelMapper.map(unitRepository.findByIdentifier(identifier), UnitDto.class);
    }

    @Override
    public List<UnitDto> findAll() {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        return modelMapper.map(unitRepository.findAll(), listType);
    }

    @Override
    public List<UnitDto> findAll(Pageable pageable) {
        Type listtype = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findAll(pageable);
        return modelMapper.map(unitPage.getContent(), listtype);
    }

    @Override
    public void delete(String identifier) {
        unitRepository.deleteByIdentifier(identifier);
    }

    @Override
    public void toggleStatus(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit != null) {
            // ✅ toggle status
            unit.setStatus(!unit.getStatus());
            unitRepository.save(unit);
        }

    }
}