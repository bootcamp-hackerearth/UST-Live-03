package com.ust.pos.warehouse.service.impl;


import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.WarehouseDto;
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
public class WarehouseServiceImpl extends BaseService implements WarehouseService {

    private static final String CONST_WAREHOUSE =
            "Warehouse ";
    private static final String DELETED_MESSAGE =
            " has been deleted. Please contact the administrator.";

    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PaginationResponseDto<WarehouseDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();

        Page<Warehouse> warehousePage =
                warehouseRepository.findByIsDeletedFalse(pageable);

        List<WarehouseDto> warehouseDtoList =
                modelMapper.map(
                        warehousePage.getContent(),
                        listType
                );

        PaginationResponseDto<WarehouseDto> paginationResponseDto =
                new PaginationResponseDto<>();

        paginationResponseDto.setDtoList(warehouseDtoList);
        paginationResponseDto.setPage(warehousePage.getNumber());
        paginationResponseDto.setSizePerPage(warehousePage.getSize());
        paginationResponseDto.setTotalPages(warehousePage.getTotalPages());
        paginationResponseDto.setTotalRecords(
                warehousePage.getTotalElements()
        );

        return paginationResponseDto;
    }

    @Override
    public List<WarehouseDto> findByStatusTrue() {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        return modelMapper.map(warehouseRepository.findByStatusTrue(), listType);
    }


    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {

        String identifier = warehouseDto.getIdentifier();

        Warehouse existingWarehouse =
                warehouseRepository.findByIdentifier(identifier);

        if (existingWarehouse != null) {

            if (existingWarehouse.isDeleted()) {
                warehouseDto.setMessage(
                        CONST_WAREHOUSE + identifier +
                                DELETED_MESSAGE
                );
                warehouseDto.setSuccess(false);
                return warehouseDto;
            }

            warehouseDto.setMessage(
                    CONST_WAREHOUSE + identifier + " already exists"
            );
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        Warehouse warehouse =
                modelMapper.map(warehouseDto, Warehouse.class);

        setCreatedDetails(warehouse);

        warehouseRepository.save(warehouse);

        warehouseDto.setMessage("Successfully added the warehouse");
        warehouseDto.setSuccess(true);

        return warehouseDto;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        return modelMapper.map(warehouseRepository.findByIdentifier(identifier), WarehouseDto.class);
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {

        String identifier = warehouseDto.getIdentifier();

        Warehouse existingWarehouse =
                warehouseRepository.findByIdentifier(identifier);

        if (existingWarehouse == null) {
            warehouseDto.setMessage("Warehouse not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        if (existingWarehouse.isDeleted()) {
            warehouseDto.setMessage(
                    CONST_WAREHOUSE + identifier +
                            DELETED_MESSAGE
            );
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        modelMapper.map(warehouseDto, existingWarehouse);

        setModifiedDetails(existingWarehouse);

        warehouseRepository.save(existingWarehouse);

        warehouseDto.setMessage("Warehouse updated successfully");
        warehouseDto.setSuccess(true);

        return warehouseDto;
    }

    @Override
    @Transactional
    public WarehouseDto updateStatus(String identifier, boolean status) {
        WarehouseDto response = new WarehouseDto();

        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            response.setSuccess(false);
            response.setMessage("Warehouse not found");
            return response;
        }

        setModifiedDetails(warehouse);
        warehouse.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        softDelete(warehouse);
        setModifiedDetails(warehouse);
        warehouseRepository.save(warehouse);
    }
}
