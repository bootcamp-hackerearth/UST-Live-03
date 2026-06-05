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
    private ModelMapper modelMapper;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (existingWarehouse != null) {
            warehouseDto.setMessage("Warehouse with identifier - " + warehouseDto.getIdentifier());
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        warehouseRepository.save(warehouse);
        return warehouseDto;
    }

    @Override
    public List<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        if(pageable == null){
            return modelMapper.map(warehouseRepository.findAll(),listType);
        }
        Page<Warehouse> warehousePage = warehouseRepository.findAll(pageable);
        return modelMapper.map(warehousePage.getContent(), listType);
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (existingWarehouse == null) {
            warehouseDto.setMessage("Warehouse with identifier -" + warehouseDto.getIdentifier());
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existingWarehouse);
        warehouseRepository.save(existingWarehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        return modelMapper.map(warehouseRepository.findByIdentifier(identifier), WarehouseDto.class);
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        warehouseRepository.deleteByIdentifier(identifier);
    }
}