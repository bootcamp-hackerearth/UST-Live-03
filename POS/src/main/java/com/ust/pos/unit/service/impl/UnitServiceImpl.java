package com.ust.pos.unit.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
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

    public UnitServiceImpl(UnitRepository unitRepository, ModelMapper modelMapper) {
        this.unitRepository = unitRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier().trim();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit != null) {
            if (existingUnit.isDeleted()) {
                unitDto.setMessage("Unit with identifier " + identifier + " has been soft deleted. (Rollback by changing status)");
                unitDto.setSuccess(false);
                return unitDto;
            }
            unitDto.setMessage("Unit with identifier - " + identifier + " already exists");
            unitDto.setSuccess(false);
            return unitDto;
        }
        Unit unit = modelMapper.map(unitDto, Unit.class);
        setCreatedDetails(unit);
        unitRepository.save(unit);
        unitDto.setSuccess(true);
        unitDto.setMessage("Unit created successfully");
        return unitDto;
    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        WsDto<UnitDto> wsDto = new WsDto<>();
        if (pageable == null) {
            List<UnitDto> unitDtoList = modelMapper.map(unitRepository.findByDeletedFalse(pageable), listType);
            wsDto.setDtoList(unitDtoList);
            wsDto.setTotalRecords(unitDtoList.size());
            return wsDto;
        }
        Page<Unit> unitPage = unitRepository.findByDeletedFalse(pageable);
        wsDto.setDtoList(modelMapper.map(unitPage.getContent(), listType));
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setTotalPages(unitPage.getTotalPages());
        wsDto.setTotalRecords(unitPage.getTotalElements());
        return wsDto;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifier(identifier), UnitDto.class);
    }

    @Override
    public void delete(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        softDelete(unit);
        setModifiedDetails(unit);
        unitRepository.save(unit);
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        Unit existingUnit = unitRepository.findByIdentifier(unitDto.getIdentifier());
        if (existingUnit == null) {
            unitDto.setMessage("Unit with identifier - " + unitDto.getIdentifier() + " not found");
            unitDto.setSuccess(false);
            return unitDto;
        }
        modelMapper.map(unitDto, existingUnit);
        setModifiedDetails(existingUnit);
        unitRepository.save(existingUnit);
        unitDto.setSuccess(true);
        unitDto.setMessage("Unit updated successfully");
        return unitDto;
    }

    @Override
    @Transactional
    public UnitDto toggleStatus(String identifier, boolean status) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            UnitDto response = new UnitDto();
            response.setSuccess(false);
            response.setMessage("Unit not found");
            return response;
        }
        unit.setStatus(status);
        setModifiedDetails(unit);
        unitRepository.save(unit);
        UnitDto response = modelMapper.map(unit, UnitDto.class);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

    @Override
    public WsDto<UnitDto> findAll(Specification<Unit> specification,
                                  Pageable pageable) {

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        Page<Unit> page =
                unitRepository.findAll(specification, pageable);

        WsDto<UnitDto> wsDto = new WsDto<>();

        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}