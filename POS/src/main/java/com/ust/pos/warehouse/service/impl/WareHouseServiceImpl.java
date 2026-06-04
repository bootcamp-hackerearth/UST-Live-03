package com.ust.pos.warehouse.service.impl;

import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.model.WareHouse;
import com.ust.pos.model.WareHouseRepository;
import com.ust.pos.warehouse.service.WareHouseService;
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
public class WareHouseServiceImpl implements WareHouseService {

    @Autowired
    private WareHouseRepository warehouseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public WareHouseDto findByIdentifier(String identifier) {
        return modelMapper.map(warehouseRepository.findByIdentifier(identifier), WareHouseDto.class);
    }

    @Override
    public WareHouseDto save(WareHouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        WareHouse existing = warehouseRepository.findByIdentifier(identifier);
        if (existing != null) {
            warehouseDto.setMessage("WareHouse with identifier - " + identifier + " already exists");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        WareHouse warehouse = modelMapper.map(warehouseDto, WareHouse.class);
        warehouseRepository.save(warehouse);
        return warehouseDto;
    }

    @Override
    public WareHouseDto update(WareHouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        WareHouse existing = warehouseRepository.findByIdentifier(identifier);
        if (existing == null) {
            warehouseDto.setMessage("WareHouse not found - " + identifier);
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existing);
        warehouseRepository.save(existing);
        return warehouseDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        warehouseRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<WareHouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();
        Page<WareHouse> wareHousePage = warehouseRepository.findAll(pageable);
        return modelMapper.map(wareHousePage.getContent(), listType);
    }

    @Override
    @Transactional
    public WareHouseDto toggleStatus(String identifier, boolean status) {
        WareHouse wareHouse = warehouseRepository.findByIdentifier(identifier);
        if (wareHouse != null) {
            wareHouse.setStatus(!wareHouse.isStatus());
            warehouseRepository.save(wareHouse);
        }
        return modelMapper.map(wareHouse, WareHouseDto.class);
    }

    @Override
    public List<WareHouseDto> findActiveWareHouse() {
        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();
        return modelMapper.map(warehouseRepository.findByStatusTrue(), listType);
    }
}