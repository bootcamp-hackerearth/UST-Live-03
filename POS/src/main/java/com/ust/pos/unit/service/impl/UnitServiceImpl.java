package com.ust.pos.unit.service.impl;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
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
    UnitRepository unitRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public UnitDto save(UnitDto unitDto) {
        Unit existingUnit = unitRepository.findByIdentifier(unitDto.getIdentifier().trim());
        if (existingUnit != null) {
            unitDto.setMessage("Unit with identifier - " + unitDto.getIdentifier() + " already exists");
            unitDto.setSuccess(false);
            return unitDto;
        }
        Unit unit = modelMapper.map(unitDto, Unit.class);
        unitRepository.save(unit);
        return unitDto;
    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        if (pageable == null) {
            List<UnitDto> unitDtoList = modelMapper.map(unitRepository.findAll(), listType);
            WsDto<UnitDto> response = new WsDto<>();
            response.setDtoList(unitDtoList);
            response.setTotalRecords(unitDtoList.size());
            return response;
        }
        Page<Unit> unitPage = unitRepository.findAll(pageable);
        List<UnitDto> unitDtoList = modelMapper.map(unitPage.getContent(), listType);
        WsDto<UnitDto> wsDto = new WsDto<>();
        wsDto.setDtoList(unitDtoList);
        wsDto.setPage(unitPage.getNumber());
        wsDto.setSizePerPage(unitPage.getSize());
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
        unitRepository.deleteByIdentifier(identifier);
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        Unit existingUnit = unitRepository.findByIdentifier(unitDto.getIdentifier());
        if (existingUnit == null) {
            unitDto.setMessage("Unit with identifier - " + unitDto.getIdentifier() + "not found");
            unitDto.setSuccess(false);
            return unitDto;
        }
        modelMapper.map(unitDto, existingUnit);
        unitRepository.save(existingUnit);
        return unitDto;
    }

    @Override
    @Transactional
    public UnitDto toggleStatus(String identifier, boolean status) {
        UnitDto response = new UnitDto();
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            response.setSuccess(false);
            response.setMessage("Unit not found");
            return response;
        }
        unit.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

}