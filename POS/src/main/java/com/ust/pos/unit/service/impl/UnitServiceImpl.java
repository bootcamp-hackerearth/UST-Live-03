package com.ust.pos.unit.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.PaginationResponseDto;
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
    public PaginationResponseDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        Page<Unit> unitPage = unitRepository.findByIsDeletedFalse(pageable);
        List<UnitDto> unitDtoList = modelMapper.map(unitPage.getContent(), listType);

        PaginationResponseDto<UnitDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(unitDtoList);
        paginationResponseDto.setPage(unitPage.getNumber());
        paginationResponseDto.setSizePerPage(unitPage.getSize());
        paginationResponseDto.setTotalPages(unitPage.getTotalPages());
        paginationResponseDto.setTotalRecords(unitPage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public PaginationResponseDto<UnitDto> findAll(Specification<Unit> example, Pageable pageable) {

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> page = unitRepository.findAll(example, pageable);

        PaginationResponseDto<UnitDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationResponseDto.setTotalRecords(page.getTotalElements());
        paginationResponseDto.setTotalPages(page.getTotalPages());
        paginationResponseDto.setSizePerPage(pageable.getPageSize());
        paginationResponseDto.setPage(pageable.getPageNumber());

        return paginationResponseDto;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifier(identifier), UnitDto.class);
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            unit = modelMapper.map(unitDto, Unit.class);
            setCreatedDetails(unit);
            unitRepository.save(unit);
            unitDto.setMessage("Successfully added the unit");
            unitDto.setSuccess(true);
        } else if (isSoftDeleted(unit)) {
            unitDto.setMessage(
                    getDeletedMessage("Unit", identifier)
            );
            unitDto.setSuccess(false);
        } else {
            unitDto.setMessage("Unit " + identifier + " already exists");
            unitDto.setSuccess(false);
        }
        return unitDto;
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);

        if (existingUnit == null) {
            unitDto.setMessage("Unit not found");
            unitDto.setSuccess(false);
            return unitDto;
        }

        if (isSoftDeleted(existingUnit)) {
            unitDto.setSuccess(false);
            unitDto.setMessage(
                    getDeletedMessage("Unit", identifier)
            );
            return unitDto;
        }

        modelMapper.map(unitDto, existingUnit);
        setModifiedDetails(existingUnit);
        unitRepository.save(existingUnit);

        unitDto.setMessage("Unit updated successfully");
        unitDto.setSuccess(true);

        return unitDto;
    }

    @Override
    @Transactional
    public UnitDto updateStatus(String identifier, boolean status) {
        UnitDto response = new UnitDto();

        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            response.setSuccess(false);
            response.setMessage("Unit not found");
            return response;
        }

        setModifiedDetails(unit);
        unit.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        softDelete(unit);
        setModifiedDetails(unit);
        unitRepository.save(unit);
    }
}
