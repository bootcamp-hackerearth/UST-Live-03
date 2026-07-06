package com.ust.pos.warehouse.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.WarehouseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class WarehouseServiceImpl extends BaseService implements WarehouseService {

    public static final String WAREHOUSE_WITH_IDENTIFIER = "Warehouse with identifier - ";
    private final WarehouseRepository wareHouseRepository;
    private final ModelMapper modelMapper;

    @Override
    public WarehouseDto findByIdentifier(String identifier) {

        Warehouse warehouse = wareHouseRepository.findByIdentifierAndDeletedFalse(identifier);
        if (warehouse == null) {
            throw new ResourceNotFoundException("Warehouse with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWareHouse = wareHouseRepository.findByIdentifier(identifier);
        if (existingWareHouse != null) {
            if (Boolean.TRUE.equals(existingWareHouse.getDeleted())) {
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
        wareHouseRepository.save(warehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = wareHouseRepository.findByIdentifier(identifier);
        if (existingWarehouse == null) {
            warehouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existingWarehouse);
        setModifiedDetails(existingWarehouse);
        wareHouseRepository.save(existingWarehouse);
        return warehouseDto;
    }

    @Override
    public void delete(String identifier) {
        Warehouse warehouse = wareHouseRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(warehouse);
        setModifiedDetails(warehouse);
        wareHouseRepository.save(warehouse);
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = wareHouseRepository.findAllByDeletedFalse(pageable);
        WsDto<WarehouseDto> warehouseDto = new WsDto<>();
        warehouseDto.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
        warehouseDto.setTotalRecords(warehousePage.getTotalElements());
        warehouseDto.setTotalPage(warehousePage.getTotalPages());
        warehouseDto.setSizePerPage(pageable.getPageSize());
        warehouseDto.setPage(pageable.getPageNumber());
        return warehouseDto;
    }

    @Override
    public WsDto<WarehouseDto> findAll(Specification<Warehouse> example, Pageable pageable) {

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> page = wareHouseRepository.findAll(example, pageable);
        WsDto<WarehouseDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPage(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}
