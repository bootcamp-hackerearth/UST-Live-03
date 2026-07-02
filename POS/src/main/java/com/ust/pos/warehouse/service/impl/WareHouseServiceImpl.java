package com.ust.pos.warehouse.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.User;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
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

    public static final String WAREHOUSE_WITH_IDENTIFIER = "Warehouse with identifier - ";
    private final WarehouseRepository warehouseRepository;

    private final ModelMapper modelMapper;

    public WareHouseServiceImpl(WarehouseRepository warehouseRepository, ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WareHouseDto save(WareHouseDto wareHouseDto) {

        String identifier = wareHouseDto.getIdentifier();

        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);

        if (existingWarehouse != null) {

            if (Boolean.TRUE.equals(existingWarehouse.getDeleted())) {
                wareHouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " has been soft deleted. Restore it by changing status.");
                wareHouseDto.setSuccess(false);
                return wareHouseDto;
            }

            wareHouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " already exists");
            wareHouseDto.setSuccess(false);
            return wareHouseDto;
        }

        Warehouse warehouse = modelMapper.map(wareHouseDto, Warehouse.class);

        warehouse.setDeleted(false);
        warehouse.setStatus(true);
        setAuditFields(warehouse, true);
        warehouseRepository.save(warehouse);
        return wareHouseDto;
    }

    @Override
    public WareHouseDto update(WareHouseDto wareHouseDto) {
        String identifier = wareHouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);
        if (existingWarehouse == null) {
            wareHouseDto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " not found");
            wareHouseDto.setSuccess(false);
            return wareHouseDto;
        }
        modelMapper.map(wareHouseDto, existingWarehouse);
        setAuditFields(existingWarehouse,false);
        warehouseRepository.save(existingWarehouse);
        return wareHouseDto;
    }

    @Override
    public boolean delete(String identifier) {

        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);

        if (warehouse == null) {
            return false;
        }
        softDelete(warehouse);
        setAuditFields(warehouse, false);
        warehouseRepository.save(warehouse);

        return true;
    }

    @Override
    public PageDto<WareHouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findByDeletedFalse(pageable);
        PageDto<WareHouseDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
        pageDto.setTotalRecords(warehousePage.getTotalElements());
        pageDto.setTotalPages(warehousePage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public PageDto<WareHouseDto> findAll(Specification<Warehouse> spec, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<WareHouseDto>>() {
        }.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findAll(spec, pageable);
        PageDto<WareHouseDto> PageDto = new PageDto<>();
        PageDto.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
        PageDto.setTotalRecords(warehousePage.getTotalElements());
        PageDto.setTotalPages(warehousePage.getTotalPages());
        PageDto.setSizePerPage(pageable.getPageSize());
        PageDto.setPage(pageable.getPageNumber());
        PageDto.setKeyword(keyword);
        return PageDto;
    }

    @Override
    public WareHouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse == null) {
            throw new ResourceNotFoundException("Warehouse with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(warehouse, WareHouseDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);
        if (warehouse != null) {
            boolean currentStatus = Boolean.TRUE.equals(warehouse.getStatus());
            warehouse.setStatus(!currentStatus);

            warehouseRepository.save(warehouse);
        }
    }

    @Override
    public List<WareHouseDto> findActiveWarehouses() {
        Type listType = new TypeToken<List<WareHouseDto>>() {}.getType();
        return modelMapper.map(warehouseRepository.findByStatusTrue(),listType);
    }
}
