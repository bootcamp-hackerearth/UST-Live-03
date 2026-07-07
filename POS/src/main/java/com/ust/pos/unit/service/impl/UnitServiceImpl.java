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
@Transactional
public class UnitServiceImpl extends BaseService implements UnitService {
    private final UnitRepository unitRepository;
    private final ModelMapper modelMapper;

    public UnitServiceImpl(UnitRepository unitRepository,
                           ModelMapper modelMapper) {
        this.unitRepository = unitRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit != null) {
            if (Boolean.TRUE.equals(existingUnit.getDeleted())) {
                unitDto.setMessage("Unit - " + identifier + " was deleted and cannot be recreated");
            } else {
                unitDto.setMessage("Unit with identifier - " + identifier + " already exists");
            }
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
    public UnitDto update(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingUnit == null) {
            unitDto.setMessage("Unit with identifier - " + identifier + " not found");
            unitDto.setSuccess(false);
            return unitDto;
        }
        modelMapper.map(unitDto, existingUnit);
        setModifiedDetails(existingUnit);
        unitRepository.save(existingUnit);
        unitDto.setSuccess(true);
        return unitDto;
    }

    @Override
    public void delete(String identifier) {
        Unit unit = unitRepository.findByIdentifierAndDeletedFalse(identifier);
        if (unit != null) {
            softDelete(unit);
            setModifiedDetails(unit);
            unitRepository.save(unit);
        }
    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findByDeletedFalse(pageable);
        WsDto<UnitDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(unitPage.getContent(), listType));
        wsDto.setTotalRecords(unitPage.getTotalElements());
        wsDto.setTotalPages(unitPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        Unit unit = unitRepository.findByIdentifierAndDeletedFalse(identifier);
        if (unit == null) {
            throw new ResourceNotFoundException("Unit with identifier " + identifier + " not found");
        }
        return modelMapper.map(unit, UnitDto.class);
    }

    @Override
    public void updateStatus(String identifier, boolean status) {
        Unit unit = unitRepository.findByIdentifierAndDeletedFalse(identifier);
        if (unit != null) {
            unit.setStatus(status);
            setModifiedDetails(unit);
            unitRepository.save(unit);
        }
    }

    @Override
    public List<UnitDto> findAllActive() {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        return modelMapper.map(
                unitRepository.findByStatusAndDeletedFalse(true),
                listType
        );
    }

    @Override
    public WsDto<UnitDto> findAll(Specification<Unit> example, Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> page = unitRepository.findAll(example, pageable);
        WsDto<UnitDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }
}