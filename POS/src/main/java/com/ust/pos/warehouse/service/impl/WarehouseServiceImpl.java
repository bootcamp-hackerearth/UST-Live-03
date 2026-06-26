package com.ust.pos.warehouse.service.impl;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
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
@Transactional
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private static final String WAREHOUSE_WITH_IDENTIFIER = "Warehouse with identifier - ";

    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        return modelMapper.map(warehouseRepository.findByIdentifier(identifier), WarehouseDto.class);
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);

        if (existingWarehouse != null) {
            if (Boolean.TRUE.equals(existingWarehouse.getIsDeleted())) {
                warehouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " was deleted. Contact admin for further support or try with a different identifier.");
            } else {
                warehouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " already exists");
            }
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        warehouse.setIsDeleted(false);
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
        warehouseRepository.save(existingWarehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto delete(String identifier) {
        WarehouseDto warehouseDto = new WarehouseDto();
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);

        if (warehouse == null) {
            warehouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        warehouse.setIsDeleted(true);
        warehouse.setStatus(false);
        warehouseRepository.save(warehouse);
        warehouseDto.setSuccess(true);
        warehouseDto.setMessage("Warehouse deleted successfully");
        return warehouseDto;
    }

    @Override
    public PaginatedResponseDto<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findByIsDeleted(false, pageable);
        List<WarehouseDto> items = modelMapper.map(warehousePage.getContent(), listType);
        PaginatedResponseDto<WarehouseDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(warehousePage.getTotalElements());
        response.setTotalPages(warehousePage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public List<WarehouseDto> findAllActive() {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        return modelMapper.map(warehouseRepository.findByStatusAndIsDeleted(true, false), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        warehouse.setStatus(status);
        warehouseRepository.save(warehouse);
    }
}