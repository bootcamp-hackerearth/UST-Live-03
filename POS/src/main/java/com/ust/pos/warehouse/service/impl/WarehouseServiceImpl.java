package com.ust.pos.warehouse.service.impl;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
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
    private WarehouseRepository wareHouseRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        return modelMapper.map(wareHouseRepository.findByIdentifier(identifier), WarehouseDto.class);
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWareHouse = wareHouseRepository.findByIdentifier(identifier);
        if (existingWareHouse != null) {
            warehouseDto.setMessage("Warehouse with identifier - " + identifier + " already exists");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        Warehouse product = modelMapper.map(warehouseDto, Warehouse.class);
        wareHouseRepository.save(product);
        return warehouseDto;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = wareHouseRepository.findByIdentifier(identifier);
        if (existingWarehouse == null) {
            warehouseDto.setMessage("Warehouse with identifier - " + identifier + " not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existingWarehouse);
        wareHouseRepository.save(existingWarehouse);
        return warehouseDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        wareHouseRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = wareHouseRepository.findAll(pageable);
        WsDto<WarehouseDto> warehouseDto = new WsDto<>();
        warehouseDto.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
        warehouseDto.setTotalRecords(warehousePage.getTotalElements());
        warehouseDto.setTotalPage(warehousePage.getTotalPages());
        warehouseDto.setSizePerPage(pageable.getPageSize());
        warehouseDto.setPage(pageable.getPageNumber());
        return warehouseDto;
    }

}
