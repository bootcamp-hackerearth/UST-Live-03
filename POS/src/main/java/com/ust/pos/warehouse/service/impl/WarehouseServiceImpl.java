package com.ust.pos.warehouse.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
            throw new ResourceNotFoundException(
                    "Warehouse with identifier '" + identifier + "' not found");
        }

        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);
        if (existingWarehouse != null) {
            warehouseDto.setMessage(
                    existingWarehouse.isDeleted()
                            ? " Warehouse with identifier - " + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : " Warehouse with identifier - " + identifier
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
            warehouseDto.setMessage("Warehouse with identifier - " + identifier + " not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existingWarehouse);
        setModifiedDetails(existingWarehouse);
        warehouseRepository.save(existingWarehouse);
        return warehouseDto;
    }

    @Transactional
    public void delete(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        setModifiedDetails(warehouse);
        softDelete(warehouse);
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();

        Page<Warehouse> warehousePage = warehouseRepository.findByIsDeletedFalse(pageable);

        List<WarehouseDto> warehouseDtos = modelMapper.map(
                warehousePage.getContent(),
                listType
        );

        WsDto<WarehouseDto> wsDto =
                new WsDto<>();

        wsDto.setContent(warehouseDtos);
        wsDto.setPage(warehousePage.getNumber());
        wsDto.setSizePerPage(warehousePage.getSize());
        wsDto.setTotalPages(warehousePage.getTotalPages());
        wsDto.setTotalRecords(warehousePage.getTotalElements());

        return wsDto;
    }

    @Override
    public WsDto<WarehouseDto> findAll(Specification<Warehouse> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findAll(example,pageable);
        WsDto<WarehouseDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(warehousePage.getContent(), listType));
        wsDto.setTotalRecords(warehousePage.getTotalElements());
        wsDto.setTotalPages(warehousePage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}