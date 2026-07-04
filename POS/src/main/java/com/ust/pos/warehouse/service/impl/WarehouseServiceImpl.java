package com.ust.pos.warehouse.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.WarehouseService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class WarehouseServiceImpl extends BaseService implements WarehouseService {

    private static final String VALIDATION_MESSAGE = "Warehouse with identifier - ";
    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;


    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {

        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);

        if (warehouse == null) {
            throw new ResourseNotFoundException("Data Cannot found " + identifier);
        }

        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public List<WarehouseDto> findActiveWarehouse() {

        List<Warehouse> warehouses = warehouseRepository.findByStatus(true);
        return warehouses.stream().map(warehouse -> modelMapper.map(warehouse, WarehouseDto.class)).toList();
    }

    @Override
    public void toggleStatus(String identifier) {

        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);

        if (warehouse != null) {
            warehouse.setStatus(!warehouse.isStatus());
            warehouseRepository.save(warehouse);
        }
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {

        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);

        if (existingWarehouse != null) {
            warehouseDto.setMessage(
                    existingWarehouse.isDeleted()
                            ? VALIDATION_MESSAGE + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : VALIDATION_MESSAGE + identifier
                            + " already exists."
            );
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        setCreatedDetails(warehouse);
        warehouseRepository.save(warehouse);

        return warehouseDto;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {

        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);

        if (existingWarehouse == null) {
            warehouseDto.setMessage(VALIDATION_MESSAGE + identifier + " not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        modelMapper.map(warehouseDto, existingWarehouse);
        setModifiedDetails(existingWarehouse);
        warehouseRepository.save(existingWarehouse);

        return warehouseDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        setModifiedDetails(warehouse);
        softDelete(warehouse);
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {

        Page<Warehouse> warehousePage = warehouseRepository.findByIsDeletedFalse(pageable);

        WsDto<WarehouseDto> warehouseDto = new WsDto<>();

        List<WarehouseDto> warehouseDtos = warehousePage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, WarehouseDto.class))
                .toList();

        warehouseDto.setContent(warehouseDtos);
        warehouseDto.setPage(warehousePage.getNumber());
        warehouseDto.setSizePerPage(warehousePage.getSize());
        warehouseDto.setTotalPages(warehousePage.getTotalPages());
        warehouseDto.setTotalRecords(warehousePage.getTotalElements());

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

