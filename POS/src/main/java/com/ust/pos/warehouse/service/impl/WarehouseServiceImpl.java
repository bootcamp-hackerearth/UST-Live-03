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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl extends BaseService implements WarehouseService {

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
                warehouseDto.setMessage("Warehouse with Identifier " + identifier + " already exists (Soft-Deleted)");
                warehouseDto.setSuccess(false);
                return warehouseDto;
            }
            warehouseDto.setMessage("Warehouse with identifier - " + identifier + " already exists");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);

        if (warehouse.getStatus() == null) {
            warehouse.setStatus(true);
        }
        setCreatedDetails(warehouse);
        warehouseRepository.save(warehouse);
        warehouseDto.setSuccess(true);
        return warehouseDto;
    }


    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingWarehouse == null) {
            warehouseDto.setMessage("Warehouse with identifier - " + identifier + " not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        String originalCreatedBy = existingWarehouse.getCreatedBy();
        java.time.LocalDateTime originalCreatedOn = existingWarehouse.getCreatedOn();

        modelMapper.map(warehouseDto, existingWarehouse);

        existingWarehouse.setCreatedBy(originalCreatedBy);
        existingWarehouse.setCreatedOn(originalCreatedOn);

        setModifiedDetails(existingWarehouse);
        warehouseRepository.save(existingWarehouse);

        warehouseDto.setSuccess(true);
        return warehouseDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifierAndDeletedFalse(identifier);
        if (warehouse != null) {
            softDelete(warehouse);
            setModifiedDetails(warehouse);
            warehouseRepository.save(warehouse);
        }
    }

    @Override
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
    public void toggleStatus(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifierAndDeletedFalse(identifier);

        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found: " + identifier);
        }

        Boolean currentStatus = warehouse.getStatus();
        warehouse.setStatus(currentStatus == null || !currentStatus);
        setModifiedDetails(warehouse);
        warehouseRepository.save(warehouse);
    }

    @Override
    public List<WarehouseDto> findAllActive() {
        return warehouseRepository.findByStatusTrueAndDeletedFalse()
                .stream()
                .map(warehouse -> modelMapper.map(warehouse, WarehouseDto.class))
                .toList();
    }

    @Override
    public WsDto<WarehouseDto> findAll(Specification<Warehouse> example, Pageable pageable) {

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> page = warehouseRepository.findAll(example, pageable);

        WsDto<WarehouseDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPage(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}