package com.ust.pos.warehouse.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class WarehouseServiceImpl extends BaseService implements WarehouseService {

    private final ModelMapper modelMapper;
    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(
            ModelMapper modelMapper,
            WarehouseRepository warehouseRepository) {

        this.modelMapper = modelMapper;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (existingWarehouse != null) {
            if (existingWarehouse.isDeleted()) {
                warehouseDto.setMessage("Warehouse with identifier - " + warehouseDto.getIdentifier()
                                + " has been soft deleted. (Rollback by changing status)");
                warehouseDto.setSuccess(false);
                return warehouseDto;
            }
            warehouseDto.setMessage("Warehouse with identifier - " + warehouseDto.getIdentifier()+"already exists");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        setCreatedDetails(warehouse);
        warehouseRepository.save(warehouse);
        return warehouseDto;
    }

    @Override
    public PaginationResponseDto<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {}.getType();
        PaginationResponseDto<WarehouseDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Warehouse> warehouses = warehouseRepository.findAll();
            response.setDtoList(modelMapper.map(warehouses, listType));
            response.setTotalRecords(warehouses.size());
            response.setTotalPages(1);
            response.setSizePerPage(warehouses.size());
            response.setPage(0);
        } else {
            Page<Warehouse> warehousePage = warehouseRepository.findByDeletedFalse(pageable);
            response.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
            response.setTotalRecords(warehousePage.getTotalElements());
            response.setTotalPages(warehousePage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (existingWarehouse == null) {
            warehouseDto.setMessage("Warehouse with identifier -" + warehouseDto.getIdentifier()+ "not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existingWarehouse);
        setModifiedDetails(existingWarehouse);
        warehouseRepository.save(existingWarehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            throw new ResourceNotFoundException("Warehouse with identifier " + identifier + "not found");
        }
        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            throw new EntityNotFoundException("Warehouse not found");
        }
        softDelete(warehouse);
        setModifiedDetails(warehouse);
        warehouseRepository.save(warehouse);
    }
}