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
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier().trim());
        if (existingWarehouse != null) {
            warehouseDto.setMessage("Warehouse with identifier - " + warehouseDto.getIdentifier() + " already exists");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        warehouseRepository.save(warehouse);
        return warehouseDto;
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        if (pageable == null) {
            List<WarehouseDto> warehouseDtoList = modelMapper.map(warehouseRepository.findAll(), listType);
            WsDto<WarehouseDto> response = new WsDto<>();
            response.setDtoList(warehouseDtoList);
            response.setTotalRecords(warehouseDtoList.size());
            return response;
        }
        Page<Warehouse> warehousePage = warehouseRepository.findAll(pageable);
        List<WarehouseDto> warehouseDtoList = modelMapper.map(warehousePage.getContent(), listType);
        WsDto<WarehouseDto> wsDto = new WsDto<>();
        wsDto.setDtoList(warehouseDtoList);
        wsDto.setPage(warehousePage.getNumber());
        wsDto.setSizePerPage(warehousePage.getSize());
        wsDto.setTotalPages(warehousePage.getTotalPages());
        wsDto.setTotalRecords(warehousePage.getTotalElements());
        return wsDto;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (existingWarehouse == null) {
            warehouseDto.setMessage("Warehouse with identifier - " + warehouseDto.getIdentifier() + "not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existingWarehouse);
        warehouseRepository.save(existingWarehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        return modelMapper.map(warehouseRepository.findByIdentifier(identifier), WarehouseDto.class);
    }

    @Override
    public void delete(String identifier) {
        warehouseRepository.deleteByIdentifier(identifier);
    }

}