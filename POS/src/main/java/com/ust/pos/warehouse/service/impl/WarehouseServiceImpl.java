package com.ust.pos.warehouse.service.impl;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.WarehouseService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;

    private final ModelMapper modelMapper;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<WarehouseDto> findAll() {
        Type listType = new TypeToken<List<WarehouseDto>>()
        {}.getType();
        return modelMapper.map(warehouseRepository.findByDeletedFalse(), listType);
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {
        Type listtype = new TypeToken<List<WarehouseDto>>(){}.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findByDeletedFalse(pageable);

        WsDto<WarehouseDto> warehouseDtoWsDto = new WsDto<>();
        warehouseDtoWsDto.setDtoList(modelMapper.map(warehousePage.getContent(), listtype));
        warehouseDtoWsDto.setTotalRecords(warehousePage.getTotalElements());
        warehouseDtoWsDto.setTotalPage(warehousePage.getTotalPages());
        warehouseDtoWsDto.setSizePerPage(pageable.getPageSize());
        warehouseDtoWsDto.setPage(pageable.getPageNumber());

        return warehouseDtoWsDto;
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingwarehouse = warehouseRepository.findByIdentifierAndDeletedFalse(identifier);
        if(existingwarehouse != null)
        {
            warehouseDto.setMessage("Warehouse already exists");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        warehouse.setDeleted(false);
        warehouseRepository.save(warehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifierAndDeletedFalse(identifier);
        if(existingWarehouse == null)
        {
            warehouseDto.setMessage("Warehouse with identifier - "+identifier+" not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existingWarehouse);
        warehouseRepository.save(existingWarehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        return modelMapper.map(warehouseRepository.findByIdentifierAndDeletedFalse(identifier), WarehouseDto.class);
    }

    @Override
    public void delete(String identifier) {

        Warehouse warehouse =
                warehouseRepository.findByIdentifierAndDeletedFalse(identifier);

        if (warehouse != null) {
            warehouse.setDeleted(true);
            warehouseRepository.save(warehouse);
        }
    }
    @Override
    public void toggleStatus(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifierAndDeletedFalse(identifier);
        if (warehouse != null) {
            warehouse.setStatus(!warehouse.getStatus());
            warehouseRepository.save(warehouse);
        }

    }

    @Override
    public Page<WarehouseDto> findAll(Example<Warehouse> example, Pageable pageable) {
        Page<Warehouse> warehousePage = warehouseRepository.findAll(example, pageable);
        return warehousePage.map(warehouse -> modelMapper.map(warehouse, WarehouseDto.class));
    }
}
