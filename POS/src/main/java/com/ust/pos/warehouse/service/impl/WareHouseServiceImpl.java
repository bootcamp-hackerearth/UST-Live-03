package com.ust.pos.warehouse.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.UserDto;
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

    private static final String WAREHOUSE_WITH_IDENTIFIER = "WareHouse with identifier - ";

    private final WareHouseRepository wareHouseRepository;

    private final ModelMapper modelMapper;

    public WareHouseServiceImpl(WareHouseRepository wareHouseRepository, ModelMapper modelMapper) {
        this.wareHouseRepository = wareHouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WareHouseDto findByIdentifier(String identifier) {
        WareHouse wareHouse=wareHouseRepository.findByIdentifier(identifier);
        if(wareHouse==null){
            throw new ResourceNotFoundException("Warehouse with identifier '" + identifier + "' not found");
        }
       return modelMapper.map(wareHouse,WareHouseDto.class);
    }

    @Override
    public WareHouseDto save(WareHouseDto wareHouseDto) {
        wareHouseDto.setIdentifier(wareHouseDto.getIdentifier().trim());
        String identifier = wareHouseDto.getIdentifier();
        WareHouse existingWareHouse = wareHouseRepository.findByIdentifier(identifier);
        if (existingWareHouse != null) {
            if (!existingWareHouse.isDeleted()) {
                wareHouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " already exists");
                wareHouseDto.setSuccess(false);
                return wareHouseDto;
            }
            wareHouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            wareHouseDto.setSuccess(false);
            return wareHouseDto;
        }
        WareHouse wareHouse = modelMapper.map(wareHouseDto, WareHouse.class);
        setAuditFields(wareHouse,true);
        wareHouseRepository.save(wareHouse);
        wareHouseDto.setSuccess(true);
        wareHouseDto.setMessage("WareHouse created successfully");
        return wareHouseDto;
    }

    @Override
    public WareHouseDto update(WareHouseDto wareHouseDto) {
        String identifier = wareHouseDto.getIdentifier();
        WareHouse existingWareHouse = wareHouseRepository.findByIdentifier(identifier);
        if (existingWareHouse == null) {
            wareHouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " not found");
            wareHouseDto.setSuccess(false);
            return wareHouseDto;
        }
        modelMapper.map(wareHouseDto, existingWareHouse);
        setAuditFields(existingWareHouse,false);
        wareHouseRepository.save(existingWareHouse);
        return wareHouseDto;
    }

    @Override
    public boolean delete(String identifier) {
        WareHouse wareHouse = wareHouseRepository.findByIdentifier(identifier);
        if (wareHouse == null) return false;
        softDelete(wareHouse);
        setAuditFields(wareHouse,false);
        wareHouseRepository.save(wareHouse);
        return true;
    }

    @Override
    public WsDto<WareHouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();
        Page<WareHouse> wareHousePage = wareHouseRepository.findByDeletedFalse(pageable);
        WsDto<WareHouseDto> wareHouseDtoWsDto = new WsDto<>();
        wareHouseDtoWsDto.setDtoList(modelMapper.map(wareHousePage.getContent(), listType));
        wareHouseDtoWsDto.setTotalRecords(wareHousePage.getTotalElements());
        wareHouseDtoWsDto.setTotalPages(wareHousePage.getTotalPages());
        wareHouseDtoWsDto.setSizePerPage(pageable.getPageSize());
        wareHouseDtoWsDto.setPage(pageable.getPageNumber());
        return wareHouseDtoWsDto;    }

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
        setAuditFields(wareHouse,false);
        wareHouseRepository.save(wareHouse);
        return modelMapper.map(wareHouse, WareHouseDto.class);
    }

    @Override
    public WsDto<WareHouseDto> findAll(Specification<WareHouse> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        Page<WareHouse>  wareHousePage= wareHouseRepository.findAll(example, pageable);
        WsDto<WareHouseDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(wareHousePage.getContent(), listType));
        wsDto.setTotalRecords(wareHousePage.getTotalElements());
        wsDto.setTotalPages(wareHousePage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}