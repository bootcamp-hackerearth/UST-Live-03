package com.ust.pos.warehouse.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.WarehouseDto;
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
@Transactional
public class WarehouseServiceImpl extends CommonService implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;

    WarehouseServiceImpl(WarehouseRepository warehouseRepository, ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<WarehouseDto> findAll() {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        return modelMapper.map(warehouseRepository.findByIsDeleteFalse(), listType);
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingwarehouse = warehouseRepository.
                findByIdentifierAndIsDeleteFalse(identifier);
        if (existingwarehouse != null) {
            warehouseDto.setMessage("Warehouse already exists");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        setAuditFields(warehouse, true);
        warehouseRepository.save(warehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        Warehouse existingWarehouse = warehouseRepository.
                findByIdentifierAndIsDeleteFalse(warehouseDto.getIdentifier());
        if (existingWarehouse == null) {
            warehouseDto.setMessage("Warehouse with identifier - " + warehouseDto.getIdentifier() + " not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existingWarehouse);
        setAuditFields(existingWarehouse, false);
        warehouseRepository.save(existingWarehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        return modelMapper.map(warehouseRepository.
                findByIdentifierAndIsDeleteFalse(identifier), WarehouseDto.class);
    }

    @Override
    public void delete(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (warehouse != null) {
            warehouse.setDelete(true);
            setAuditFields(warehouse, false);
            warehouseRepository.save(warehouse);
        }
    }

    @Override
    public List<WarehouseDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findByIsDeleteFalse(pageable);
        return modelMapper.map(warehousePage.getContent(), listOfType);
    }

    @Override
    public Page<WarehouseDto> findAll(Pageable pageable, String search) {
        Page<Warehouse> warehousePage;
        if (search != null && !search.trim().isEmpty()) {
            warehousePage = warehouseRepository.findByIdentifierContainingIgnoreCaseAndIsDeleteFalse
                    (search, pageable);
        } else {
            warehousePage = warehouseRepository.findByIsDeleteFalse(pageable);
        }
        return warehousePage.map(warehouse -> modelMapper.map(warehouse, WarehouseDto.class));
    }
}
