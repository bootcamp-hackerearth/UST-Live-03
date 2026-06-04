package com.ust.pos.warehouse.service.impl;

import com.ust.pos.dto.WarehouseDto;
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
        return modelMapper.map(
                warehouseRepository.findByIdentifier(identifier),
                WarehouseDto.class
        );
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {

        Warehouse existing = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (existing != null) {
            warehouseDto.setSuccess(false);
            warehouseDto.setMessage("Warehouse already exists : " + warehouseDto.getIdentifier());
            return warehouseDto;
        }

        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        warehouseRepository.save(warehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {

        Warehouse existing = warehouseRepository.findByIdentifier(warehouseDto.getIdentifier());
        if (existing == null) {
            warehouseDto.setSuccess(false);
            warehouseDto.setMessage("Warehouse not found : " + warehouseDto.getIdentifier());
            return warehouseDto;
        }

        modelMapper.map(warehouseDto, existing);
        warehouseRepository.save(existing);
        return warehouseDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        warehouseRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> customerPage = warehouseRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
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
