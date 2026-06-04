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
    public WarehouseDto save(WarehouseDto dto) {

        String identifier = dto.getIdentifier().trim();

        if (warehouseRepository.findByIdentifier(identifier) != null) {
            dto.setSuccess(false);
            dto.setMessage("Warehouse with identifier " + identifier + " already exists");
            return dto;
        }

        Warehouse warehouse = modelMapper.map(dto, Warehouse.class);
        warehouse.setStatus(true);
        warehouseRepository.save(warehouse);

        WarehouseDto response = modelMapper.map(warehouse, WarehouseDto.class);
        response.setSuccess(true);
        return response;
    }

    @Override
    public WarehouseDto update(WarehouseDto dto) {

        Warehouse warehouse = warehouseRepository.findByIdentifier(dto.getIdentifier());
        if (warehouse == null) {
            dto.setSuccess(false);
            dto.setMessage("Warehouse not found");
            return dto;
        }

        boolean currentStatus = warehouse.isStatus();

        modelMapper.map(dto, warehouse);
        warehouse.setStatus(currentStatus);
        warehouseRepository.save(warehouse);

        WarehouseDto response = modelMapper.map(warehouse, WarehouseDto.class);
        response.setSuccess(true);
        return response;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        return warehouse == null ? null : modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public List<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findAll(pageable);
        return modelMapper.map(warehousePage.getContent(), listType);
    }


    @Override
    @Transactional
    public boolean delete(String identifier) {
        warehouseRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    @Transactional
    public void toggleStatus(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse != null) {
            warehouse.setStatus(!warehouse.isStatus());
            warehouseRepository.save(warehouse);
        }
    }

    @Override
    public List<WarehouseDto> findIfTrue() {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        return modelMapper.map(warehouseRepository.findByStatusIsTrue(), listType);
    }
}