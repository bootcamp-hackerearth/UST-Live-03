package com.ust.pos.unit.service.impl;

import com.ust.pos.dto.PaginationResponseDto;
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

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UnitDto save(UnitDto unitDto) {
        Unit existingUnit = unitRepository.findByIdentifier(unitDto.getIdentifier());
        if (existingUnit != null) {
            unitDto.setMessage(
                    "Unit with identifier - " + unitDto.getIdentifier() + " already exists"
            );
            unitDto.setSuccess(false);
            return unitDto;
        }
        Unit unit = modelMapper.map(unitDto, Unit.class);
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
            response.setTotalRecords((long) units.size());
            response.setTotalPages(1);
            response.setSizePerPage(units.size());
            response.setPage(0);
        } else {
            Page<Unit> unitPage = unitRepository.findAll(pageable);
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
        unitRepository.deleteByIdentifier(identifier);
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        Unit existingUnit =
                unitRepository.findByIdentifier(unitDto.getIdentifier());
        if (existingUnit == null) {
            unitDto.setMessage(
                    "Unit with identifier - " + unitDto.getIdentifier() + " not found"
            );
            unitDto.setSuccess(false);
            return unitDto;
        }
        modelMapper.map(unitDto, existingUnit);
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
            unitRepository.save(unit);
        }
        return modelMapper.map(unit,UnitDto.class);
    }
}