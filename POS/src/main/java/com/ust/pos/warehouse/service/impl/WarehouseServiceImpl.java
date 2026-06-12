package com.ust.pos.warehouse.service.impl;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.WarehouseService;
import jakarta.transaction.Transactional;
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
        if (warehouseDto.getIdentifier() == null || warehouseDto.getIdentifier().isBlank()) {
            WarehouseDto dto = new WarehouseDto();
            dto.setSuccess(false);
            dto.setMessage("Identifier required");
            return dto;
        }
        Warehouse existing = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier().trim());
        if (existing != null) {
            WarehouseDto dto = new WarehouseDto();
            dto.setSuccess(false);
            dto.setMessage("Warehouse already exists");
            return dto;
        }
        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        Warehouse saved = warehouseRepository.save(warehouse);
        WarehouseDto response = modelMapper.map(saved, WarehouseDto.class);
        response.setSuccess(true);
        response.setMessage("Warehouse saved successfully");
        return response;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        Warehouse existing = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (existing == null) {
            WarehouseDto dto = new WarehouseDto();
            dto.setSuccess(false);
            dto.setMessage("Warehouse not found");
            return dto;
        }
        modelMapper.map(warehouseDto, existing);
        Warehouse saved = warehouseRepository.save(existing);
        WarehouseDto response = modelMapper.map(saved, WarehouseDto.class);
        response.setSuccess(true);
        response.setMessage("Warehouse updated successfully");
        return response;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            WarehouseDto dto = new WarehouseDto();
            dto.setSuccess(false);
            dto.setMessage("Warehouse not found");
            return dto;
        }
        WarehouseDto dto = modelMapper.map(warehouse, WarehouseDto.class);
        dto.setSuccess(true);
        return dto;
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
    @Transactional
    public void delete(String identifier) {
        warehouseRepository.deleteByIdentifier(identifier);
    }
}