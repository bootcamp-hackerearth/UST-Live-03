package com.ust.pos.warehouse.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.WareHouse;
import com.ust.pos.model.WareHouseRepository;
import com.ust.pos.warehouse.service.WareHouseService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class WareHouseServiceImpl extends CommonService implements WareHouseService {

    private final WareHouseRepository wareHouseRepository;
    private final ModelMapper modelMapper;

    public WareHouseServiceImpl(WareHouseRepository wareHouseRepository, ModelMapper modelMapper) {
        this.wareHouseRepository = wareHouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WareHouseDto findByIdentifier(String identifier) {
        WareHouse warehouse = wareHouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            throw new ResourceNotFoundException("Warehouse with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(warehouse, WareHouseDto.class);
    }

    @Override
    public WareHouseDto save(WareHouseDto wareHouseDto) {
        String identifier = wareHouseDto.getIdentifier();
        WareHouse existingWareHouse = wareHouseRepository.findByIdentifier(identifier);
        if (existingWareHouse != null) {
            if (existingWareHouse.isDeleted()) {
                wareHouseDto.setMessage("WareHouse with identifier " + identifier + " was previously deleted. " +
                        "Please contact backend team to restore."
                );
                wareHouseDto.setSuccess(false);
                return wareHouseDto;
            }
            wareHouseDto.setMessage("WareHouse with identifier - " + identifier + " already exists");
            wareHouseDto.setSuccess(false);
            return wareHouseDto;
        }
        WareHouse wareHouse = modelMapper.map(wareHouseDto, WareHouse.class);
        setAuditFields(wareHouse, true);
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
        setAuditFields(existingWareHouse, false);
        wareHouseRepository.save(existingWareHouse);
        return wareHouseDto;
    }

    @Override
    public boolean delete(String identifier) {
        WareHouse wareHouse = wareHouseRepository.findByIdentifier(identifier);
        if (wareHouse == null) return false;
        softDelete(wareHouse);
        setAuditFields(wareHouse, false);
        wareHouseRepository.save(wareHouse);
        return true;
    }

    @Override
    public WsDto<WareHouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();
        Page<WareHouse> wareHousePage = wareHouseRepository.findByDeletedFalse(pageable);

        WsDto<WareHouseDto> wareHouseWsDto = new WsDto<>();
        wareHouseWsDto.setDtoList(modelMapper.map(wareHousePage.getContent(), listType));
        wareHouseWsDto.setTotalRecords(wareHousePage.getTotalElements());
        wareHouseWsDto.setTotalPages(wareHousePage.getTotalPages());
        wareHouseWsDto.setSizePerPage(pageable.getPageSize());
        wareHouseWsDto.setPage(pageable.getPageNumber());

        return wareHouseWsDto;
    }

    @Override
    public List<WareHouseDto> findIfTrue() {
        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();
        return modelMapper.map(wareHouseRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }

    @Override
    public WareHouseDto toggleStatus(String identifier) {
        WareHouse wareHouse = wareHouseRepository.findByIdentifier(identifier);
        wareHouse.setStatus(!wareHouse.isStatus());
        setAuditFields(wareHouse, false);
        wareHouseRepository.save(wareHouse);
        return modelMapper.map(wareHouse, WareHouseDto.class);
    }

    @Override
    public WsDto<WareHouseDto> findAll(Specification<WareHouse> example, Pageable pageable) {

        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();
        Page<WareHouse> page = wareHouseRepository.findAll(example, pageable);

        WsDto<WareHouseDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}