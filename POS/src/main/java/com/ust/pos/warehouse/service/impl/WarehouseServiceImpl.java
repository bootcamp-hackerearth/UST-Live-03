package com.ust.pos.warehouse.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Warehouse;
import com.ust.pos.modell.WarehouseRepository;
import com.ust.pos.warehouse.service.WarehouseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl extends BaseService implements WarehouseService {

    public static final String WAREHOUSE_WITH_IDENTIFIER = "Warehouse with identifier - ";

    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifierAndDeletedFalse(identifier);
        
        if (warehouse == null) {
            throw new ResourceNotFoundException("Warehouse with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);

        if (existingWarehouse != null) {
            if (Boolean.TRUE.equals(existingWarehouse.getDeleted())) {
                warehouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " was deleted and cannot be created again.");
                warehouseDto.setSuccess(false);
                return warehouseDto;
            }

            warehouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " already exists");
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
        Warehouse warehouse = warehouseRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(warehouse);
        setModifiedDetails(warehouse);
        warehouseRepository.save(warehouse);
    }

    public WsDto<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findAllByDeletedFalse(pageable);
        WsDto<WarehouseDto> warehouseWsDto = new WsDto<>();
        warehouseWsDto.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
        warehouseWsDto.setTotalRecords(warehousePage.getTotalElements());
        warehouseWsDto.setTotalPage(warehousePage.getTotalPages());
        warehouseWsDto.setSizePerPage(pageable.getPageSize());
        warehouseWsDto.setPage(pageable.getPageNumber());
        return warehouseWsDto;
    }

    @Override
    @Transactional
    public WarehouseDto toggleStatus(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifierAndDeletedFalse(identifier);

        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found with identifier: " + identifier);
        }

        Boolean currentStatus = warehouse.getStatus();
        warehouse.setStatus(currentStatus == null ? Boolean.TRUE : !currentStatus);
        setModifiedDetails(warehouse);
        Warehouse saved = warehouseRepository.save(warehouse);
        return modelMapper.map(saved, WarehouseDto.class);
    }

    @Override
    public List<WarehouseDto> findAllActive() {
        return warehouseRepository.findByStatusTrueAndDeletedFalse()
                .stream()
                .map(warehouse -> modelMapper.map(warehouse, WarehouseDto.class))
                .toList();
    }
}