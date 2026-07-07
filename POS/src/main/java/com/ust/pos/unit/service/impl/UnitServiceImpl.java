package com.ust.pos.unit.service.impl;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnitServiceImpl implements UnitService {

    private final ModelMapper modelMapper;

    private final UnitRepository unitRepository;

    public UnitServiceImpl(ModelMapper modelMapper, UnitRepository unitRepository) {
        this.modelMapper = modelMapper;
        this.unitRepository = unitRepository;
    }

    @Override
    public Page<UnitDto> findAll(Example<Unit> example, Pageable pageable)
    {
        Page<Unit> unitPage = unitRepository.findAll(example, pageable);
        return unitPage.map(unit -> modelMapper.map(unit, UnitDto.class));
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingmodel = unitRepository.findByIdentifierAndDeletedFalse(identifier);
        if(existingmodel != null)
        {
            unitDto.setMessage("Model - "+identifier+" already exists");
            unitDto.setSuccess(false);
            return unitDto;
        }
        Unit unit = modelMapper.map(unitDto, Unit.class);
        unit.setDeleted(false);
        unitRepository.save(unit);
        return unitDto;
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Optional<Unit> optionalUnit = unitRepository.findById(unitDto.getId());
        if(optionalUnit.isEmpty()) {
            unitDto.setSuccess(false);
            return unitDto;
        }

        else{
            Unit existingmodel = optionalUnit.get();
            if (!identifier.equals(existingmodel.getIdentifier()) && unitRepository.findByIdentifierAndDeletedFalse(identifier) != null) {
                unitDto.setSuccess(false);
                unitDto.setMessage("Model Already Exists");
                return unitDto;
            }
            else{
                modelMapper.map(unitDto , existingmodel);
                unitRepository.save(existingmodel);
                unitDto.setSuccess(true);
            }
            return unitDto;

        }
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifierAndDeletedFalse(identifier), UnitDto.class);
    }

    @Override
    public List<UnitDto> findAll() {
        Type listType = new TypeToken<List<UnitDto>>(){}.getType();
        return modelMapper.map(unitRepository.findByDeletedFalse(), listType);
    }
    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {
        Type listtype = new TypeToken<List<UnitDto>>(){}.getType();
        Page<Unit> unitPage = unitRepository.findByDeletedFalse(pageable);
        WsDto<UnitDto> unitDtoWsDto = new WsDto<>();
        unitDtoWsDto.setDtoList(modelMapper.map(unitPage.getContent(), listtype));
        unitDtoWsDto.setTotalRecords(unitPage.getTotalElements());
        unitDtoWsDto.setTotalPage(unitPage.getTotalPages());
        unitDtoWsDto.setSizePerPage(pageable.getPageSize());
        unitDtoWsDto.setPage(pageable.getPageNumber());
        return unitDtoWsDto;
    }
    @Override
    public void delete(String identifier) {

        Unit unit = unitRepository.findByIdentifierAndDeletedFalse(identifier);
        if(unit != null)
        {
            unit.setDeleted(true);
            unitRepository.save(unit);
        }
    }

    @Override
    public void toggleStatus(String identifier) {
        Unit unit = unitRepository.findByIdentifierAndDeletedFalse(identifier);
        if (unit != null) {
            unit.setStatus(!unit.getStatus());
            unitRepository.save(unit);
        }

    }
}