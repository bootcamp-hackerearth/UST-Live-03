package com.ust.pos.warehouse.service.impl;

import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.WareHouseService;
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
public class WareHouseServiceImpl implements WareHouseService {
    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public WareHouseDto save(WareHouseDto wareHouseDto) {
        String identifier = wareHouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);
        if (existingWarehouse != null) {
            wareHouseDto.setMessage("Warehouse with identifier - " + identifier + " already exists");
            wareHouseDto.setSuccess(false);
            return wareHouseDto;
        }
        Warehouse warehouse = modelMapper.map(wareHouseDto, Warehouse.class);
        warehouseRepository.save(warehouse);
        return wareHouseDto;
    }

    @Override
    public WareHouseDto update(WareHouseDto wareHouseDto) {
        String identifier = wareHouseDto.getIdentifier();
        Warehouse existingRole = warehouseRepository.findByIdentifier(identifier);
        if (existingRole == null) {
            wareHouseDto.setMessage("Role with identifier - " + identifier + " not found");
            wareHouseDto.setSuccess(false);
            return wareHouseDto;
        }
        modelMapper.map(wareHouseDto, existingRole);
        warehouseRepository.save(existingRole);
        return wareHouseDto;
    }

    @Override
    @Transactional
    public boolean delete(String identifier) {
        warehouseRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public List<WareHouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findAll(pageable);
        return modelMapper.map(warehousePage.getContent(), listType);
    }

    @Override
    public WareHouseDto findByIdentifier(String identifier) {
        return modelMapper.map(warehouseRepository.findByIdentifier(identifier), WareHouseDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse != null) {
            boolean currentStatus = Boolean.TRUE.equals(warehouse.getStatus());
            warehouse.setStatus(!currentStatus);

            warehouseRepository.save(warehouse);
        }
    }
}
