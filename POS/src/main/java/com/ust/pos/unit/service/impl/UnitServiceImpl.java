package com.ust.pos.unit.service.impl;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UnitServiceImpl implements UnitService {

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ModelMapper modelMapper;

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
            unitDto.setMessage("Unit with identifier - " + identifier + " already exists");
            unitDto.setSuccess(false);
            return unitDto;
        }

        Unit unit = modelMapper.map(unitDto, Unit.class);
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
        unitRepository.save(existingUnit);

        return unitDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        unitRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {

        Page<Unit> unitPage = unitRepository.findAll(pageable);
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
    public List<Unit> findActiveUnit() {

        return unitRepository.findByStatus(true);
    }

}



