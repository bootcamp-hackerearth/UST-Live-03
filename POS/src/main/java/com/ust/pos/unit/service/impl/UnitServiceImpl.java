package com.ust.pos.unit.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
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
public class UnitServiceImpl extends BaseService implements UnitService {

    private final UnitRepository unitRepository;

    private final ModelMapper modelMapper;

    public UnitServiceImpl(UnitRepository unitRepository, ModelMapper modelMapper) {
        this.unitRepository = unitRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        Page<Unit> unitPage = unitRepository.findByIsDeletedFalse(pageable);

        WsDto<UnitDto> dto = new WsDto<>();

        dto.setContent(modelMapper.map(unitPage.getContent(), listType));
        dto.setTotalRecords(unitPage.getTotalElements());
        dto.setTotalPages(unitPage.getTotalPages());
        dto.setSizePerPage(pageable.getPageSize());
        dto.setPage(pageable.getPageNumber());

        return dto;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);

        if (unit == null) {
            throw new ResourceNotFoundException("Data cannot found");
        }

        return modelMapper.map(unit, UnitDto.class);
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);

        if (existingUnit != null) {
            unitDto.setMessage(
                    existingUnit.isDeleted()
                            ? "Unit - " + identifier + " already exists but was deleted, Please contact Administrator"
                            : "Unit - " + identifier + " already exists"
            );

            unitDto.setSuccess(false);
            return unitDto;
        }
        Unit unit = modelMapper.map(unitDto, Unit.class);
        setCreatedDetails(unit);
        unitRepository.save(unit);
        return unitDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        setModifiedDetails(unit);
        softDelete(unit);
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
        setModifiedDetails(existingUnit);
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

    @Override
    public WsDto<UnitDto> findAll(Specification<Unit> example, Pageable pageable) {

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> page = unitRepository.findAll(example, pageable);

        WsDto<UnitDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}
