package com.ust.pos.warehouse.service.impl;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.WarehouseService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {

        Warehouse existing = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (existing != null) {
            warehouseDto.setMessage("Warehouse Already Exist!");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }

        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
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
        warehouseRepository.save(warehouse);
        warehouseDto.setSuccess(true);
        return warehouseDto;
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findAll(pageable);
        WsDto<WarehouseDto> warehouseWsDto = new WsDto<>();
        warehouseWsDto.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
        warehouseWsDto.setTotalRecords(warehousePage.getTotalElements());
        warehouseWsDto.setTotalPages(warehousePage.getTotalPages());
        warehouseWsDto.setSizePerPage(pageable.getPageSize());
        warehouseWsDto.setPage(pageable.getPageNumber());

        return warehouseWsDto;
    }

    @Override
    public WarehouseDto findById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public void delete(Long id) {
        warehouseRepository.deleteById(id);
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
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