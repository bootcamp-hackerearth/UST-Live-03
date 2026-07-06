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

    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier().trim();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);
        if (existingWarehouse != null) {
            if (existingWarehouse.isDeleted()) {
                warehouseDto.setMessage("Warehouse with identifier " + identifier + " has been soft deleted. (Rollback by changing status)");
                warehouseDto.setSuccess(false);
                return warehouseDto;
            }
            warehouseDto.setMessage("Warehouse with identifier - " + identifier + " already exists");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        setCreatedDetails(warehouse);
        warehouseRepository.save(warehouse);
        warehouseDto.setSuccess(true);
        warehouseDto.setMessage("Warehouse created successfully");
        return warehouseDto;
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        WsDto<WarehouseDto> wsDto = new WsDto<>();
        if (pageable == null) {
            List<WarehouseDto> warehouseDtoList = modelMapper.map(warehouseRepository.findByDeletedFalse(pageable), listType);
            wsDto.setDtoList(warehouseDtoList);
            wsDto.setTotalRecords(warehouseDtoList.size());
            return wsDto;
        }
        Page<Warehouse> warehousePage = warehouseRepository.findByDeletedFalse(pageable);
        wsDto.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setTotalPages(warehousePage.getTotalPages());
        wsDto.setTotalRecords(warehousePage.getTotalElements());
        return wsDto;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (existingWarehouse == null) {
            warehouseDto.setMessage("Warehouse with identifier - " + warehouseDto.getIdentifier() + " not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existingWarehouse);
        setModifiedDetails(existingWarehouse);
        warehouseRepository.save(existingWarehouse);
        warehouseDto.setSuccess(true);
        warehouseDto.setMessage("Warehouse updated successfully");
        return warehouseDto;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            throw new ResourceNotFoundException(
                    "Warehouse with identifier '" + identifier + "' not found"
            );
        }
        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public void delete(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        softDelete(warehouse);
        setModifiedDetails(warehouse);
        warehouseRepository.save(warehouse);
    }

    @Override
    public WsDto<WarehouseDto> findAll(Specification<Warehouse> specification,
                                       Pageable pageable) {

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();

        Page<Warehouse> page =
                warehouseRepository.findAll(specification, pageable);

        WsDto<WarehouseDto> wsDto = new WsDto<>();

        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}