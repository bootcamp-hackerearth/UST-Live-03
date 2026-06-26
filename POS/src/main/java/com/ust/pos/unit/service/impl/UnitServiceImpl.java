package com.ust.pos.unit.service.impl;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private static final String UNIT_WITH_IDENTIFIER = "Unit with identifier - ";

    private final UnitRepository unitRepository;
    private final ModelMapper modelMapper;

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);

        if (existingUnit != null) {
            if (Boolean.TRUE.equals(existingUnit.getIsDeleted())) {
                unitDto.setMessage(UNIT_WITH_IDENTIFIER + identifier + " was deleted. Contact admin for further support or try with a different identifier.");
            } else {
                unitDto.setMessage(UNIT_WITH_IDENTIFIER + identifier + " already exists");
            }
            unitDto.setSuccess(false);
            return unitDto;
        }

        Unit unit = modelMapper.map(unitDto, Unit.class);
        unit.setIsDeleted(false);
        unitRepository.save(unit);
        unitDto.setSuccess(true);
        return unitDto;
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);

        if (existingUnit == null) {
            unitDto.setMessage(UNIT_WITH_IDENTIFIER + identifier + " not found");
            unitDto.setSuccess(false);
            return unitDto;
        }

        modelMapper.map(unitDto, existingUnit);
        unitRepository.save(existingUnit);
        return unitDto;
    }

    @Override
    public UnitDto delete(String identifier) {
        UnitDto unitDto = new UnitDto();
        Unit unit = unitRepository.findByIdentifier(identifier);

        if (unit == null) {
            unitDto.setMessage(UNIT_WITH_IDENTIFIER + identifier + " not found");
            unitDto.setSuccess(false);
            return unitDto;
        }

        unit.setIsDeleted(true);
        unit.setStatus(false);
        unitRepository.save(unit);
        unitDto.setSuccess(true);
        unitDto.setMessage("Unit deleted successfully");
        return unitDto;
    }

    @Override
    public PaginatedResponseDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findByIsDeleted(false, pageable);
        List<UnitDto> items = modelMapper.map(unitPage.getContent(), listType);
        PaginatedResponseDto<UnitDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(unitPage.getTotalElements());
        response.setTotalPages(unitPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifier(identifier), UnitDto.class);
    }

    @Override
    public List<UnitDto> findAllActive() {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        return modelMapper.map(unitRepository.findByStatusAndIsDeleted(true, false), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        unit.setStatus(status);
        unitRepository.save(unit);
    }
}