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
    public List<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        if (pageable == null) {
            return modelMapper.map(warehouseRepository.findAll(), listType);
        }
        Page<Warehouse> warehousePage = warehouseRepository.findAll(pageable);
        return modelMapper.map(warehousePage.getContent(), listType);
    }

    @Override
    public List<WarehouseDto> findByStatusTrue() {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        return modelMapper.map(warehouseRepository.findByStatusTrue(), listType);
    }


    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            warehouseRepository.save(modelMapper.map(warehouseDto, Warehouse.class));
            warehouseDto.setMessage("Successfully added the warehouse");
            warehouseDto.setSuccess(true);
        } else {
            warehouseDto.setMessage("Warehouse " + identifier + " already exists");
            warehouseDto.setSuccess(false);
        }
        return warehouseDto;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        return modelMapper.map(warehouseRepository.findByIdentifier(identifier), WarehouseDto.class);
    }

    public WarehouseDto update(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse exisitngWarehouse = warehouseRepository.findByIdentifier(identifier);
        if (exisitngWarehouse == null) {
            warehouseDto.setMessage("Warehouse not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        modelMapper.map(warehouseDto, exisitngWarehouse);
        warehouseRepository.save(exisitngWarehouse);
        warehouseDto.setMessage("Warehouse updated successfully");
        warehouseDto.setSuccess(true);

        return warehouseDto;
    }

    @Override
    @Transactional
    public WarehouseDto updateStatus(String identifier, boolean status) {
        WarehouseDto response = new WarehouseDto();

        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            response.setSuccess(false);
            response.setMessage("Warehouse not found");
            return response;
        }

        warehouse.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        warehouseRepository.deleteByIdentifier(identifier);
    }
}
