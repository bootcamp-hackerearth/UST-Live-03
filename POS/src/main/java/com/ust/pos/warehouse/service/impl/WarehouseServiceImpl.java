package com.ust.pos.warehouse.service.impl;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.WarehouseService;
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
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        if (warehouseDto.getIdentifier() == null || warehouseDto.getIdentifier().isEmpty()) {
            WarehouseDto dto = new WarehouseDto();
            dto.setSuccess(false);
            dto.setMessage("Identifier required");
            return dto;
        }
        Warehouse existing = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (existing != null) {
            WarehouseDto dto = new WarehouseDto();
            dto.setSuccess(false);
            dto.setMessage("Warehouse already exists");
            return dto;
        }
        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        Warehouse saved = warehouseRepository.save(warehouse);
        WarehouseDto response = modelMapper.map(saved, WarehouseDto.class);
        response.setSuccess(true);
        response.setMessage("Warehouse saved successfully");
        return response;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        Warehouse existing = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (existing == null) {
            WarehouseDto dto = new WarehouseDto();
            dto.setSuccess(false);
            dto.setMessage("Warehouse not found");
            return dto;
        }
        modelMapper.map(warehouseDto, existing);
        Warehouse saved = warehouseRepository.save(existing);
        WarehouseDto response = modelMapper.map(saved, WarehouseDto.class);
        response.setSuccess(true);
        response.setMessage("Warehouse updated successfully");
        return response;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            WarehouseDto warehouseDto = new WarehouseDto();
            warehouseDto.setSuccess(false);
            warehouseDto.setMessage("Warehouse not found");
            return warehouseDto;
        }
        WarehouseDto warehouseDto = modelMapper.map(warehouse, WarehouseDto.class);
        warehouseDto.setSuccess(true);
        return warehouseDto;
    }

    @Override
    public List<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<Warehouse>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findAll(pageable);
        return modelMapper.map(warehousePage.getContent(), listType);
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        warehouseRepository.deleteByIdentifier(identifier);
    }
}