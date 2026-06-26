package com.ust.pos.warehouse.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.WarehouseService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class WarehouseServiceImpl extends CommonService implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WarehouseDto save(WarehouseDto dto) {

        String identifier = dto.getIdentifier().trim();

        Warehouse existing = warehouseRepository.findByIdentifier(identifier);
        if (existing != null) {
            if (existing.isDeleted()) {
                dto.setSuccess(false);
                dto.setMessage("Warehouse with identifier " + identifier + " has been soft deleted.(Rollback by changing status");
                return dto;
            }
            dto.setSuccess(false);
            dto.setMessage("Warehouse with identifier " + identifier + " already exists");
            return dto;
        }

        Warehouse warehouse = modelMapper.map(dto, Warehouse.class);
        warehouse.setStatus(true);
        setAuditFields(warehouse, true);
        warehouseRepository.save(warehouse);

        WarehouseDto response = modelMapper.map(warehouse, WarehouseDto.class);
        response.setSuccess(true);
        return response;
    }

    @Override
    public WarehouseDto update(WarehouseDto dto) {

        Warehouse warehouse = warehouseRepository.findByIdentifier(dto.getIdentifier());
        if (warehouse == null) {
            throw new ResourceNotFoundException("Warehouse with identifier '" + dto.getIdentifier() + "' not found");
        }

        boolean currentStatus = warehouse.isStatus();

        modelMapper.map(dto, warehouse);
        warehouse.setStatus(currentStatus);
        setAuditFields(warehouse, false);
        warehouseRepository.save(warehouse);

        WarehouseDto response = modelMapper.map(warehouse, WarehouseDto.class);
        response.setSuccess(true);
        return response;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            throw new ResourceNotFoundException("Warehouse with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public List<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findByDeletedFalse(pageable);
        return modelMapper.map(warehousePage.getContent(), listType);
    }


    @Override
    @Transactional
    public boolean delete(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            throw new ResourceNotFoundException("Warehouse with identifier '" + identifier + "' not found");
        }
        softDelete(warehouse);
        setAuditFields(warehouse, false);
        warehouseRepository.save(warehouse);
        return true;
    }

    @Override
    @Transactional
    public void toggleStatus(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            throw new ResourceNotFoundException("Warehouse with identifier '" + identifier + "' not found");
        }
        warehouse.setStatus(!warehouse.isStatus());
        warehouseRepository.save(warehouse);
    }

    @Override
    public List<WarehouseDto> findIfTrue() {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        return modelMapper.map(warehouseRepository.findByStatusIsTrue(), listType);
    }
}