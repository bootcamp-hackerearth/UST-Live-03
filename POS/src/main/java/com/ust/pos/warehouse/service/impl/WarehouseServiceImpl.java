package com.ust.pos.warehouse.service.impl;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Warehouse;
import com.ust.pos.modell.WarehouseRepository;
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
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        WarehouseDto response = new WarehouseDto();

        if (warehouse == null) {
            response.setSuccess(false);
            response.setMessage("Warehouse not found");
            return response;
        }

        response = modelMapper.map(warehouse, WarehouseDto.class);
        response.setSuccess(true);
        return response;
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);

        if (existingWarehouse != null) {
            warehouseDto.setMessage("Warehouse with identifier - " + identifier + " already exists");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        warehouseRepository.save(warehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);

        if (existingWarehouse == null) {
            warehouseDto.setMessage("Warehouse with identifier - " + identifier + " not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        modelMapper.map(warehouseDto, existingWarehouse);
        warehouseRepository.save(existingWarehouse);
        return warehouseDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        warehouseRepository.deleteByIdentifier(identifier);
    }

    public WsDto<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findAll(pageable);

        WsDto<WarehouseDto> warehouseWsDto = new WsDto<>();
        warehouseWsDto.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
        warehouseWsDto.setTotalRecords(warehousePage.getTotalElements());
        warehouseWsDto.setTotalPage(warehousePage.getTotalPages());
        warehouseWsDto.setSizePerPage(pageable.getPageSize());
        warehouseWsDto.setPage(pageable.getPageNumber());

        return warehouseWsDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);

        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found: " + identifier);
        }

        Boolean currentStatus = warehouse.getStatus();
        warehouse.setStatus(currentStatus == null || !currentStatus);

        warehouseRepository.save(warehouse);
    }

    @Override
    public List<WarehouseDto> findAllActive() {
        return warehouseRepository.findByStatusTrue()
                .stream()
                .map(warehouse -> modelMapper.map(warehouse, WarehouseDto.class))
                .toList();
    }
}