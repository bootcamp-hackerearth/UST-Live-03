package com.ust.pos.unit.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
@Service
public class UnitServiceImpl extends CommonService implements UnitService {

    public static final String UNIT_WITH_IDENTIFIER = "Unit with identifier - ";
    private final UnitRepository unitRepository;

    private final ModelMapper modelMapper;

    public UnitServiceImpl(UnitRepository unitRepository, ModelMapper modelMapper) {
        this.unitRepository = unitRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier =unitDto.getIdentifier();
        Unit existingUnit =unitRepository.findByIdentifier(identifier);
        if (existingUnit  != null) {
            if (Boolean.TRUE.equals(existingUnit.getDeleted())) {
                unitDto.setMessage(UNIT_WITH_IDENTIFIER + identifier + " has been soft deleted. Restore it by changing status.");
                unitDto.setSuccess(false);
                return unitDto;
            }
            unitDto.setMessage(UNIT_WITH_IDENTIFIER + identifier + " already exists");
            unitDto.setSuccess(false);
            return unitDto ;
        }
        Unit unit= modelMapper.map(unitDto, Unit.class);
        unit.setDeleted(false);
        unit.setStatus(true);
        setAuditFields(unit,true);
        unitRepository.save(unit);
        return unitDto ;
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit== null) {
            unitDto.setMessage(UNIT_WITH_IDENTIFIER + identifier + " not found");
            unitDto.setSuccess(false);
            return unitDto;
        }
        modelMapper.map(unitDto,existingUnit);
        setAuditFields(existingUnit,false);
        unitRepository.save(existingUnit);
        return unitDto;
    }

    @Override
    public boolean delete(String identifier) {

        Unit unit = unitRepository.findByIdentifier(identifier);

        if (unit == null) {
            return false;
        }
        softDelete(unit);
        setAuditFields(unit,false);
        unitRepository.save(unit);
        return true;
    }

    @Override
    public PageDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findByDeletedFalse(pageable);
        PageDto<UnitDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(unitPage.getContent(), listType));
        pageDto.setTotalRecords(unitPage.getTotalElements());
        pageDto.setTotalPages(unitPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public PageDto<UnitDto> findAll(Specification<Unit> spec, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findAll(spec, pageable);
        PageDto<UnitDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(unitPage.getContent(), listType));
        pageDto.setTotalRecords(unitPage.getTotalElements());
        pageDto.setTotalPages(unitPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        pageDto.setKeyword(keyword);
        return pageDto;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            throw new ResourceNotFoundException("Unit with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(unit, UnitDto.class);
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
