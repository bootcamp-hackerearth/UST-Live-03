package com.ust.pos.unit.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UnitServiceImpl extends BaseService implements UnitService {

    private static final String VALIDATION_MESSAGE = "Unit with identifier - ";
    private final UnitRepository unitRepository;
    private final ModelMapper modelMapper;


    public UnitServiceImpl(UnitRepository unitRepository, ModelMapper modelMapper) {
        this.unitRepository = unitRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {

        Unit unit = unitRepository.findByIdentifier(identifier);

        if (unit == null) {
            return null;
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
                            ? VALIDATION_MESSAGE + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : VALIDATION_MESSAGE + identifier
                            + " already exists."
            );
            unitDto.setSuccess(false);
            return unitDto;
        }

        Unit unit = modelMapper.map(unitDto, Unit.class);
        setCreatedDetails(unit);
        unitRepository.save(unit);

        return unitDto;
    }

    @Override
    public UnitDto update(UnitDto unitDto) {

        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);

        if (existingUnit == null) {
            unitDto.setMessage(VALIDATION_MESSAGE + identifier + " not found");
            unitDto.setSuccess(false);
            return unitDto;
        }

        modelMapper.map(unitDto, existingUnit);
        setModifiedDetails(existingUnit);
        unitRepository.save(existingUnit);

        return unitDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        Unit unit = unitRepository.findByIdentifier(identifier);
        setModifiedDetails(unit);
        softDelete(unit);
    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {

        Page<Unit> unitPage = unitRepository.findByIsDeletedFalse(pageable);
        WsDto<UnitDto> unitDto = new WsDto<>();

        List<UnitDto> unitDtos = unitPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, UnitDto.class))
                .toList();

        unitDto.setContent(unitDtos);
        unitDto.setPage(unitPage.getNumber());
        unitDto.setSizePerPage(unitPage.getSize());
        unitDto.setTotalPages(unitPage.getTotalPages());
        unitDto.setTotalRecords(unitPage.getTotalElements());

        return unitDto;
    }

    @Override
    public void toggleStatus(String identifier) {

        Unit shelf = unitRepository.findByIdentifier(identifier);

        if (shelf != null) {
            shelf.setStatus(!shelf.isStatus());
            unitRepository.save(shelf);
        }
    }

    @Override
    public List<UnitDto> findActiveUnit() {

        List<Unit> units = unitRepository.findByStatus(true);
        return units.stream().map(unit -> modelMapper.map(unit, UnitDto.class)).toList();
    }

}



