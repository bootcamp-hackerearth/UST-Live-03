package com.ust.pos.unit.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class UnitServiceImpl extends BaseService implements UnitService {

    public static final String UNIT_WITH_IDENTIFIER = "Unit with identifier - ";
    private final UnitRepository unitRepository;
    private final ModelMapper modelMapper;

    public UnitServiceImpl(
            UnitRepository unitRepository,
            ModelMapper modelMapper) {
        this.unitRepository = unitRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        Unit existingUnit = unitRepository.findByIdentifier(unitDto.getIdentifier());
        if (existingUnit != null) {
            if (existingUnit.isDeleted()) {
                unitDto.setMessage(UNIT_WITH_IDENTIFIER + unitDto.getIdentifier() + " has been soft deleted. (Rollback by changing status)");
                unitDto.setSuccess(false);
                return unitDto;
            }
            unitDto.setMessage(UNIT_WITH_IDENTIFIER + unitDto.getIdentifier() + " already exists");
            unitDto.setSuccess(false);
            return unitDto;
        }
        Unit unit = modelMapper.map(unitDto, Unit.class);
        setCreatedDetails(unit);
        unitRepository.save(unit);
        unitDto.setSuccess(true);
        return unitDto;
    }


    @Override
    public PaginationResponseDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {}.getType();
        PaginationResponseDto<UnitDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Unit> units = unitRepository.findAll();
            response.setDtoList(modelMapper.map(units, listType));
            response.setTotalRecords(units.size());
            response.setTotalPages(1);
            response.setSizePerPage(units.size());
            response.setPage(0);
        } else {
            Page<Unit> unitPage = unitRepository.findByDeletedFalse(pageable);
            response.setDtoList(modelMapper.map(unitPage.getContent(), listType));
            response.setTotalRecords(unitPage.getTotalElements());
            response.setTotalPages(unitPage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        return unit == null ? null : modelMapper.map(unit, UnitDto.class);
    }

    @Override
    public void delete(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            throw new EntityNotFoundException("Unit not found");
        }
        softDelete(unit);
        setModifiedDetails(unit);
        unitRepository.save(unit);
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        Unit existingUnit =
                unitRepository.findByIdentifier(unitDto.getIdentifier());
        if (existingUnit == null) {
            unitDto.setMessage(UNIT_WITH_IDENTIFIER + unitDto.getIdentifier() + " not found");
            unitDto.setSuccess(false);
            return unitDto;
        }
        modelMapper.map(unitDto, existingUnit);
        setModifiedDetails(existingUnit);
        unitRepository.save(existingUnit);
        unitDto.setSuccess(true);
        return unitDto;
    }

    @Transactional
    @Override
    public UnitDto toggleStatus(String identifier, boolean status) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit != null) {
            unit.setStatus(!unit.isStatus());
            setModifiedDetails(unit);
            unitRepository.save(unit);
        }
        return modelMapper.map(unit,UnitDto.class);
    }
    
    @Override
    public PaginationResponseDto<UnitDto> findAll(Specification<Unit> example, Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> page = unitRepository.findAll(example, pageable);
        PaginationResponseDto<UnitDto> paginationresponse = new PaginationResponseDto<>();
        paginationresponse.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationresponse.setTotalRecords(page.getTotalElements());
        paginationresponse.setTotalPages(page.getTotalPages());
        paginationresponse.setSizePerPage(pageable.getPageSize());
        paginationresponse.setPage(pageable.getPageNumber());
        return paginationresponse;
    }
}