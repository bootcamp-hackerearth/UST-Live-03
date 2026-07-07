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

    public static final String WAREHOUSE_NOT_FOUND = "Warehouse not found";

    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;

    public WarehouseServiceImpl(
            WarehouseRepository warehouseRepository,
            ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {

        String identifier = warehouseDto.getIdentifier();

        if (identifier == null || identifier.trim().isEmpty()) {
            warehouseDto.setSuccess(false);
            warehouseDto.setMessage("Identifier required");
            return warehouseDto;
        }

        identifier = identifier.trim();

        Warehouse existing = warehouseRepository.findByIdentifier(identifier);

        if (existing != null) {

            if (Boolean.TRUE.equals(existing.getDeleted())) {
                warehouseDto.setSuccess(false);
                warehouseDto.setMessage(
                        "Warehouse with identifier " + identifier +
                                " has been soft deleted. (Rollback by changing status)"
                );
                return warehouseDto;
            }

            warehouseDto.setSuccess(false);
            warehouseDto.setMessage("Warehouse already exists");
            return warehouseDto;
        }

        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);

        warehouse.setIdentifier(identifier);
        warehouse.setStatus(Boolean.TRUE.equals(warehouseDto.getStatus()));

        setCreatedDetails(warehouse);

        warehouseRepository.save(warehouse);

        warehouseDto.setSuccess(true);
        warehouseDto.setMessage("Warehouse added successfully");
        warehouseDto.setIdentifier(identifier);

        return warehouseDto;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {

        String identifier = warehouseDto.getIdentifier();

        if (identifier == null || identifier.trim().isEmpty()) {
            throw new ResourceNotFoundException("Invalid identifier");
        }

        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);

        if (warehouse == null || Boolean.TRUE.equals(warehouse.getDeleted())) {
            throw new ResourceNotFoundException(
                    "Warehouse with identifier '" + identifier + "' not found");
        }

        warehouse.setWarehouseName(warehouseDto.getWarehouseName());
        warehouse.setCountry(warehouseDto.getCountry());
        warehouse.setState(warehouseDto.getState());
        warehouse.setCityName(warehouseDto.getCityName());
        warehouse.setLocation(warehouseDto.getLocation());
        warehouse.setStatus(Boolean.TRUE.equals(warehouseDto.getStatus()));

        setModifiedDetails(warehouse);
        Warehouse saved = warehouseRepository.save(warehouse);

        WarehouseDto result = modelMapper.map(saved, WarehouseDto.class);
        result.setSuccess(true);
        result.setMessage("Warehouse updated successfully");

        return result;
    }

    @Override
    public void delete(String identifier) {

        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);

        if (warehouse == null || Boolean.TRUE.equals(warehouse.getDeleted())) {
            throw new ResourceNotFoundException(
                    "Warehouse with identifier '" + identifier + "' not found");
        }

        softDelete(warehouse);
        setModifiedDetails(warehouse);
        warehouseRepository.save(warehouse);
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<WarehouseDto>>() {}.getType();

        Page<Warehouse> page = warehouseRepository.findByDeletedFalse(pageable);

        WsDto<WarehouseDto> ws = new WsDto<>();
        ws.setDtoList(modelMapper.map(page.getContent(), listType));
        ws.setTotalRecords(page.getTotalElements());
        ws.setTotalPages(page.getTotalPages());
        ws.setSizePerPage(pageable.getPageSize());
        ws.setPage(pageable.getPageNumber());

        return ws;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {

        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);

        if (warehouse == null || Boolean.TRUE.equals(warehouse.getDeleted())) {
            throw new ResourceNotFoundException(
                    "Warehouse with identifier '" + identifier + "' not found");
        }

        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public WarehouseDto toggleStatus(String identifier) {

        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);

        if (warehouse == null || Boolean.TRUE.equals(warehouse.getDeleted())) {
            throw new ResourceNotFoundException(
                    "Warehouse with identifier '" + identifier + "' not found");
        }

        warehouse.setStatus(!Boolean.TRUE.equals(warehouse.getStatus()));
        setModifiedDetails(warehouse);
        warehouseRepository.save(warehouse);

        WarehouseDto response = modelMapper.map(warehouse, WarehouseDto.class);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Override
    public List<WarehouseDto> findActiveWarehouses() {

        return warehouseRepository.findByStatusTrueAndDeletedFalse()
                .stream()
                .map(w -> modelMapper.map(w, WarehouseDto.class))
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
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}