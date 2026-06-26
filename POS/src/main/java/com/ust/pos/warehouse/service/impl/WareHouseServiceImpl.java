package com.ust.pos.warehouse.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.WareHouse;
import com.ust.pos.model.WareHouseRepository;
import com.ust.pos.warehouse.service.WareHouseService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class WareHouseServiceImpl extends BaseService implements WareHouseService {
    private final WareHouseRepository wareHouseRepository;
    private final ModelMapper modelMapper;

    public WareHouseServiceImpl(WareHouseRepository wareHouseRepository, ModelMapper modelMapper) {
        this.wareHouseRepository = wareHouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WareHouseDto findByIdentifier(String identifier) {
        return modelMapper.map(wareHouseRepository.findByIdentifier(identifier), WareHouseDto.class);
    }

    @Override
    public WareHouseDto save(WareHouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        WareHouse existing = wareHouseRepository.findByIdentifier(identifier);
        if (existing != null) {
            if (existing.isDeleted()) {
                warehouseDto.setMessage("WareHouse with identifier" + identifier + "has been soft deleted.(Rollback by changing status)");
                warehouseDto.setSuccess(false);
                return warehouseDto;
            }
            warehouseDto.setMessage("WareHouse with identifier - " + identifier + " already exists");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        WareHouse warehouse = modelMapper.map(warehouseDto, WareHouse.class);
        setCreatedDetails(warehouse);
        wareHouseRepository.save(warehouse);
        return warehouseDto;
    }

    @Override
    public WareHouseDto update(WareHouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        WareHouse existing = wareHouseRepository.findByIdentifier(identifier);
        if (existing == null) {
            warehouseDto.setMessage("WareHouse not found - " + identifier);
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existing);
        setModifiedDetails(existing);
        wareHouseRepository.save(existing);
        return warehouseDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        WareHouse wareHouse = wareHouseRepository.findByIdentifier(identifier);
        softDelete(wareHouse);
        setModifiedDetails(wareHouse);
        wareHouseRepository.save(wareHouse);
    }

    public WsDto<WareHouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();
        Page<WareHouse> warehousePage = wareHouseRepository.findByDeletedFalse(pageable);

        WsDto<WareHouseDto> warehouseWsDto = new WsDto<>();
        warehouseWsDto.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
        warehouseWsDto.setTotalRecords(warehousePage.getTotalElements());
        warehouseWsDto.setTotalPages(warehousePage.getTotalPages());
        warehouseWsDto.setSizePerPage(pageable.getPageSize());
        warehouseWsDto.setPage(pageable.getPageNumber());

        return warehouseWsDto;
    }

    @Override
    @Transactional
    public WareHouseDto toggleStatus(String identifier, boolean status) {
        WareHouse wareHouse = wareHouseRepository.findByIdentifier(identifier);
        if (wareHouse != null) {
            wareHouse.setStatus(status);
            setModifiedDetails(wareHouse);
            wareHouseRepository.save(wareHouse);
        }
        return modelMapper.map(wareHouse, WareHouseDto.class);
    }

    @Override
    public List<WareHouseDto> findActiveWareHouse() {
        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();
        return modelMapper.map(wareHouseRepository.findByStatusTrue(), listType);
    }
}