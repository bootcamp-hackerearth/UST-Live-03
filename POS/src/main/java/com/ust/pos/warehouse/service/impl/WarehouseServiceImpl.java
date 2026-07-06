package com.ust.pos.warehouse.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
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
public class WarehouseServiceImpl extends BaseService implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    private final ModelMapper modelMapper;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();

        Page<Warehouse> warehousePage = warehouseRepository.findByIsDeletedFalse(pageable);

        WsDto<WarehouseDto> dto = new WsDto<>();

        dto.setContent(modelMapper.map(warehousePage.getContent(), listType));
        dto.setTotalRecords(warehousePage.getTotalElements());
        dto.setTotalPages(warehousePage.getTotalPages());
        dto.setSizePerPage(pageable.getPageSize());
        dto.setPage(pageable.getPageNumber());

        return dto;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        return modelMapper.map(warehouseRepository.findByIdentifier(identifier), WarehouseDto.class);
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);

        if (existingWarehouse != null) {
            warehouseDto.setMessage(
                    existingWarehouse.isDeleted()
                            ? "Warehouse - " + identifier + " already exists but was deleted, Please contact Administrator"
                            : "Warehouse - " + identifier + " already exists"
            );

            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        setCreatedDetails(warehouse);
        warehouseRepository.save(warehouse);
        return warehouseDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        setModifiedDetails(warehouse);
        softDelete(warehouse);
    }

    @Override
    public void toggleStatus(String identifier) {
        Warehouse warehouse = warehouseRepository
                .findByIdentifier(identifier);

        if (warehouse != null) {
            warehouse.setStatus(!warehouse.isStatus());
            warehouseRepository.save(warehouse);
        }
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);
        if (existingWarehouse == null) {
            warehouseDto.setMessage("Warehouse with warehouse - " + identifier + " not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existingWarehouse);
        setModifiedDetails(existingWarehouse);
        warehouseRepository.save(existingWarehouse);
        return warehouseDto;
    }

    @Override
    public WsDto<WarehouseDto> findAll(Specification<Warehouse> example, Pageable pageable) {

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> page = warehouseRepository.findAll(example, pageable);

        WsDto<WarehouseDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}
