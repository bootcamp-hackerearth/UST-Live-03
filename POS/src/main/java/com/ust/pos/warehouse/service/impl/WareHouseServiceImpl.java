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
public class WareHouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        return modelMapper.map(warehouseRepository.findByIdentifier(identifier), WarehouseDto.class);
    }

    @Override
    public WarehouseDto save(WarehouseDto dto) {

        Warehouse existing = warehouseRepository.findByIdentifier(dto.getIdentifier());
        if (existing != null) {
            dto.setSuccess(false);
            dto.setMessage("Warehouse already exists : " + dto.getIdentifier());
            return dto;
        }

        Warehouse warehouse = modelMapper.map(dto, Warehouse.class);
        warehouseRepository.save(warehouse);
        return dto;
    }

    @Override
    public WarehouseDto update(WarehouseDto dto) {

        Warehouse existing = warehouseRepository.findByIdentifier(dto.getIdentifier());
        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage("Warehouse not found : " + dto.getIdentifier());
            return dto;
        }

        modelMapper.map(dto, existing);
        warehouseRepository.save(existing);
        return dto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        warehouseRepository.deleteByIdentifier(identifier);
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
    public WarehouseDto toggleStatus(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        warehouse.setStatus(!warehouse.isStatus());
        warehouseRepository.save(warehouse);
        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public List<WarehouseDto> findIfTrue() {
        Type listType = new TypeToken<List<WarehouseDto>>() {

        }.getType();
        return modelMapper.map(warehouseRepository.findByStatusIsTrue(), listType);
    }


}
