package com.ust.pos.unit.service.impl;

import com.ust.pos.dto.PageDto;
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
    public UnitDto save(UnitDto unitDto) {
        String identifier =unitDto.getIdentifier();
        Unit existingUnit =unitRepository.findByIdentifier(identifier);
        if (existingUnit  != null) {
            unitDto.setMessage("Unit with identifier - " + identifier + " already exists");
            unitDto.setSuccess(false);
            return unitDto ;
        }
        Unit unit= modelMapper.map(unitDto, Unit.class);
        unitRepository.save(unit);
        return unitDto ;
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit== null) {
            unitDto.setMessage("Unit with identifier - " + identifier + " not found");
            unitDto.setSuccess(false);
            return unitDto;
        }
        modelMapper.map(unitDto,existingUnit);
        unitRepository.save(existingUnit);
        return unitDto;
    }

    @Override
    public boolean delete(String identifier) {
        unitRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public PageDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findAll(pageable);
        PageDto<UnitDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(unitPage.getContent(), listType));
        pageDto.setTotalRecords(unitPage.getTotalElements());
        pageDto.setTotalPages(unitPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifier(identifier), UnitDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit != null) {
            boolean currentStatus = Boolean.TRUE.equals(unit.getStatus());
            unit.setStatus(!currentStatus);

            unitRepository.save(unit);
        }
    }

    @Override
    public List<UnitDto> findActiveUnits() {
        Type listType = new TypeToken<List<UnitDto>>() {}.getType();
        return modelMapper.map(unitRepository.findByStatusTrue(),listType);
    }
}
