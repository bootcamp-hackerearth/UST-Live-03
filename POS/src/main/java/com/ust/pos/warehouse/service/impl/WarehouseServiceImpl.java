package com.ust.pos.warehouse.service.impl;

import com.ust.pos.commonservice.CommonService;
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
    public WarehouseDto save(WarehouseDto warehouseDto) {

        Warehouse existing = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (existing != null) {
            if (existing.isDeleted()) {
                warehouseDto.setMessage("Warehouse with identifier - " + existing + "has been soft deleted.(Rollback by changing status");
                warehouseDto.setSuccess(false);
                return warehouseDto;
            }
            warehouseDto.setMessage("Warehouse Already Exist!");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        setAuditFields(warehouse, true);
        warehouseRepository.save(warehouse);
        warehouseDto.setSuccess(true);
        return warehouseDto;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {

        Warehouse warehouse = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (warehouse == null) {
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, warehouse);
        setAuditFields(warehouse, false);
        warehouseRepository.save(warehouse);
        warehouseDto.setSuccess(true);
        return warehouseDto;
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findByDeletedFalse(pageable);
        WsDto<WarehouseDto> warehouseWsDto = new WsDto<>();
        warehouseWsDto.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
        warehouseWsDto.setTotalRecords(warehousePage.getTotalElements());
        warehouseWsDto.setTotalPages(warehousePage.getTotalPages());
        warehouseWsDto.setSizePerPage(pageable.getPageSize());
        warehouseWsDto.setPage(pageable.getPageNumber());

        return warehouseWsDto;
    }

    @Override
    public WsDto<WarehouseDto> findAll(Specification<Warehouse> spec, Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {}.getType();
        Page<Warehouse> page = warehouseRepository.findAll(spec, pageable);
        WsDto<WarehouseDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public WarehouseDto findById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id " + id));
        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public void delete(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        softDelete(warehouse);
        setAuditFields(warehouse, false);
        warehouseRepository.save(warehouse);
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            throw new ResourceNotFoundException("Warehouse with identifier '" + identifier + "'not found");
        }
        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public WarehouseDto changeWarehouseStatus(String identifier, boolean status) {

        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            return null; // test expects null
        }
        warehouse.setStatus(status);
        warehouseRepository.save(warehouse);
        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public List<WarehouseDto> findAllActiveWarehouse() {
        return warehouseRepository.findByStatusTrue()
                .stream()
                .map(w -> modelMapper.map(w, WarehouseDto.class))
                .toList();
    }
}