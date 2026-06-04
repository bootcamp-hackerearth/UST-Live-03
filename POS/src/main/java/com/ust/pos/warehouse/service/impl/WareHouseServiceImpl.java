package com.ust.pos.warehouse.service.impl;

import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.model.WareHouse;
import com.ust.pos.model.WareHouseRepository;
import com.ust.pos.warehouse.service.WareHouseService;
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
    private WareHouseRepository wareHouseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public WareHouseDto findByIdentifier(String identifier) {
        return modelMapper.map(wareHouseRepository.findByIdentifier(identifier), WareHouseDto.class);
    }

    @Override
    public WareHouseDto save(WareHouseDto wareHouseDto) {
        String identifier = wareHouseDto.getIdentifier();
        WareHouse existingWareHouse = wareHouseRepository.findByIdentifier(identifier);
        if (existingWareHouse != null) {
            wareHouseDto.setMessage("WareHouse with identifier - " + identifier + " already exists");
            wareHouseDto.setSuccess(false);
            return wareHouseDto;
        }
        WareHouse wareHouse = modelMapper.map(wareHouseDto, WareHouse.class);
        wareHouseRepository.save(wareHouse);
        return wareHouseDto;
    }

    @Override
    public WareHouseDto update(WareHouseDto wareHouseDto) {
        String identifier = wareHouseDto.getIdentifier();
        WareHouse existingWareHouse = wareHouseRepository.findByIdentifier(identifier);
        if (existingWareHouse == null) {
            wareHouseDto.setMessage("WareHouse with identifier - " + identifier + " not found");
            wareHouseDto.setSuccess(false);
            return wareHouseDto;
        }
        modelMapper.map(wareHouseDto, existingWareHouse);
        wareHouseRepository.save(existingWareHouse);
        return wareHouseDto;
    }

    @Override
    public boolean delete(String identifier) {
        wareHouseRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public List<WareHouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();
        Page<WareHouse> wareHousePage = wareHouseRepository.findAll(pageable);
        return modelMapper.map(wareHousePage.getContent(), listType);
    }

    @Override
    public List<WareHouseDto> findIfTrue() {
        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();
        return modelMapper.map(wareHouseRepository.findByStatusIsTrue(), listType);
    }

    @Override
    public WareHouseDto toggleStatus(String identifier) {
        WareHouse wareHouse = wareHouseRepository.findByIdentifier(identifier);
        wareHouse.setStatus(!wareHouse.isStatus());
        wareHouseRepository.save(wareHouse);
        return modelMapper.map(wareHouse, WareHouseDto.class);
    }
}