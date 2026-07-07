package com.ust.pos.warehouse.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.WarehouseService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class WarehouseServiceImpl extends BaseService implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository,
                                ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);
        if (existingWarehouse != null) {
            if (Boolean.TRUE.equals(existingWarehouse.getDeleted())) {
                warehouseDto.setMessage("Warehouse - " + identifier + " was deleted and cannot be recreated");
            } else {
                warehouseDto.setMessage("warehouse with identifier - " + identifier + " already exists");
            }
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
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
        modelMapper.map(warehouseDto, existingWarehouse);
        setModifiedDetails(existingWarehouse);
        warehouseRepository.save(existingWarehouse);
        warehouseDto.setSuccess(true);
        return warehouseDto;
    }

    @Override
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
        Page<Warehouse> warehousePage = warehouseRepository.findByDeletedFalse(pageable);
        WsDto<WarehouseDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
        wsDto.setTotalRecords(warehousePage.getTotalElements());
        wsDto.setTotalPages(warehousePage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifierAndDeletedFalse(identifier);
        if (warehouse == null) {
            throw new ResourceNotFoundException("Warehouse with identifier " + identifier + " not found");
        }
        return modelMapper.map(warehouse,WarehouseDto.class);
    }

    @Override
    public void updateStatus(String identifier, boolean status) {
        Warehouse warehouse = warehouseRepository.findByIdentifierAndDeletedFalse(identifier);
        if (warehouse != null) {
            warehouse.setStatus(status);
            setModifiedDetails(warehouse);
            warehouseRepository.save(warehouse);
        }
    }

    @Override
    public List<WarehouseDto> findAllActive() {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        return modelMapper.map(
                warehouseRepository.findByStatusAndDeletedFalse(true),
                listType
        );
    }

    @Override
    public WsDto<WarehouseDto> findAll(Specification<Warehouse> example, Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> page = warehouseRepository.findAll(example, pageable);
        WsDto<WarehouseDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }
}