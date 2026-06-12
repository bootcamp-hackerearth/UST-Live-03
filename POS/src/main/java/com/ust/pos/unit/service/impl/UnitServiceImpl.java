package com.ust.pos.unit.service.impl;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.UnitDto;
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
public class UnitServiceImpl implements UnitService {

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PaginationResponseDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        if (pageable == null) {

            List<UnitDto> unitDtoList =
                    modelMapper.map(unitRepository.findAll(), listType);

            PaginationResponseDto<UnitDto> response =
                    new PaginationResponseDto<>();

            response.setDtoList(unitDtoList);
            response.setTotalRecords(unitDtoList.size());

            return response;
        }
        Page<Unit> unitPage = unitRepository.findAll(pageable);
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
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifier(identifier), UnitDto.class);
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit unit = unitRepository.findByIdentifier(identifier);
        if (unit == null) {
            unitDto.setSuccess(true);
            unitRepository.save(modelMapper.map(unitDto, Unit.class));
            unitDto.setMessage("Successfully added the unit");
            unitDto.setSuccess(true);
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
        } else {
            modelMapper.map(unitDto, existingUnit);
            unitRepository.save(existingUnit);
            unitDto.setMessage("Unit updated successfully");
            unitDto.setSuccess(true);
        }
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

        unit.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        unitRepository.deleteByIdentifier(identifier);
    }
}
