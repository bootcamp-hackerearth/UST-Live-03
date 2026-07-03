package com.ust.pos.unit.service.impl;

import com.ust.pos.commonservice.CommonService;
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
public class UnitServiceImpl extends CommonService implements UnitService {
    
    private final ModelMapper modelMapper;

    private final UnitRepository unitRepository;

    public UnitServiceImpl(ModelMapper modelMapper, UnitRepository unitRepository) {
        this.modelMapper = modelMapper;
        this.unitRepository = unitRepository;
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifier(identifier), UnitDto.class);
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        Unit existing = unitRepository.findByIdentifier(unitDto.getIdentifier());
        if (existing != null) {
            if(existing.isDeleted()) {
                unitDto.setMessage("Unit with identifier - " + unitDto.getIdentifier() + "has been soft deleted.(Rollback by changing status");
                unitDto.setSuccess(false);
                return unitDto;
            }
            unitDto.setSuccess(false);
            unitDto.setMessage("Unit already exists : " + unitDto.getIdentifier());
            return unitDto;
        }

        Unit unit = modelMapper.map(unitDto, Unit.class);
        setAuditFields(unit, true);
        unitRepository.save(unit);
        return unitDto;
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        Unit existing = unitRepository.findByIdentifier(unitDto.getIdentifier());
        if (existing == null) {
            unitDto.setSuccess(false);
            unitDto.setMessage("Unit not found : " + unitDto.getIdentifier());
            return unitDto;
        }
        modelMapper.map(unitDto, existing);
        setAuditFields(existing,false);

        unitRepository.save(existing);
        return unitDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        softDelete(unit);
        setAuditFields(unit,false);
        unitRepository.save(unit);    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> userPage = unitRepository.findByDeletedFalse(pageable);

        WsDto<UnitDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());

        return userWsDto;
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

    @Override
    public UnitDto changeToggleStatus(String identifier, boolean status) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit != null) {
            unit.setStatus(status);
            unitRepository.save(unit);
        }
        return modelMapper.map(unit, UnitDto.class);
    }

    @Override
    public List<UnitDto> findActiveStatus() {
        List<Unit> allUnits = unitRepository.findAll();
        List<Unit> activeUnits = allUnits.stream().filter(Unit::isStatus).toList();

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        return modelMapper.map(activeUnits, listType);
    }
}
