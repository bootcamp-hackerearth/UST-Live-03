package com.ust.pos.unit.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifier(identifier), UnitDto.class);
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingUnit = unitRepository.findByIdentifier(identifier);
        if (existingUnit != null) {
            unitDto.setMessage(
                    existingUnit.isDeleted()
                            ? " Unit with identifier - " + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : " Unit with identifier - " + identifier
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
            unitDto.setMessage("Unit with identifier - " + identifier + " not found");
            unitDto.setSuccess(false);
            return unitDto;
        }
        modelMapper.map(unitDto, existingUnit);
        setModifiedDetails(existingUnit);
        unitRepository.save(existingUnit);
        return unitDto;
    }

    @Transactional
    public void delete(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        setModifiedDetails(unit);
        softDelete(unit);
    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        Page<Unit> unitPage = unitRepository.findByIsDeletedFalse(pageable);

        List<UnitDto> unitDtos = modelMapper.map(
                unitPage.getContent(),
                listType
        );

        WsDto<UnitDto> wsDto =
                new WsDto<>();

        wsDto.setContent(unitDtos);
        wsDto.setPage(unitPage.getNumber());
        wsDto.setSizePerPage(unitPage.getSize());
        wsDto.setTotalPages(unitPage.getTotalPages());
        wsDto.setTotalRecords(unitPage.getTotalElements());

        return wsDto;
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
    public WsDto<UnitDto> findAll(Specification<Unit> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> unitPage = unitRepository.findAll(example,pageable);
        WsDto<UnitDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(unitPage.getContent(), listType));
        wsDto.setTotalRecords(unitPage.getTotalElements());
        wsDto.setTotalPages(unitPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}