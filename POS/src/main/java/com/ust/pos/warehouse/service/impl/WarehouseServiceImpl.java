package com.ust.pos.warehouse.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.WarehouseService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class WarehouseServiceImpl extends BaseService implements WarehouseService {

    public static final String WAREHOUSE_WITH_IDENTIFIER = "Warehouse with identifier - ";
    private final WarehouseRepository warehouseRepository;

    private final ModelMapper modelMapper;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier.trim());
        if (warehouse == null) {
            return null;
        }
        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);
        if (existingWarehouse != null) {
            warehouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " already exists");
            if (existingWarehouse.isDeleted()) {
                warehouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " was deleted , Please Contact the Administrator to add.");
            }

            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        setCreatedDetails(warehouse);
        warehouseRepository.save(warehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);
        if (existingWarehouse == null) {
            warehouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existingWarehouse);
        setModifiedDetails(existingWarehouse);
        warehouseRepository.save(existingWarehouse);
        return warehouseDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        softDelete(warehouse);
        setModifiedDetails(warehouse);
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findByIsDeletedFalse(pageable);
        WsDto<WarehouseDto> warehouseWsDto = new WsDto<>();
        warehouseWsDto.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
        warehouseWsDto.setTotalRecords(warehousePage.getTotalElements());
        warehouseWsDto.setTotalPages(warehousePage.getTotalPages());
        warehouseWsDto.setSizePerPage(pageable.getPageSize());
        warehouseWsDto.setPage(pageable.getPageNumber());

        return warehouseWsDto;
    }
}
