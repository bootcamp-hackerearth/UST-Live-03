package com.ust.pos.warehouse.service.impl;

import com.ust.pos.CommonService;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.WarehouseService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class WareHouseServiceImpl extends CommonService implements WarehouseService {

    private static final String WAREHOUSE_WITH_IDENTIFIER = "Warehouse with identifier - ";

    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;

    public WareHouseServiceImpl(WarehouseRepository warehouseRepository,
                                ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse =warehouseRepository.findByIdentifier(identifier);
        if(warehouse==null){
            throw new ResourceNotFoundException("Data Cannot Found");
        }
        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public WarehouseDto save(WarehouseDto dto) {

        if (dto == null || dto.getIdentifier() == null) {
            throw new IllegalArgumentException("Identifier is required");
        }

        String identifier = dto.getIdentifier();
        Warehouse existing = warehouseRepository.findByIdentifier(identifier);

        if (existing != null) {
            if (BooleanUtils.isNotTrue(existing.getDeleted())) {
                dto.setSuccess(false);
                dto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " already exists");
                return dto;
            }

            dto.setSuccess(false);
            dto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        Warehouse warehouse = modelMapper.map(dto, Warehouse.class);
        setAuditFields(warehouse, true);

        warehouseRepository.save(warehouse);

        dto.setSuccess(true);
        dto.setMessage("Warehouse created successfully");

        return dto;
    }

    @Override
    public WarehouseDto update(WarehouseDto dto) {

        String identifier = dto.getIdentifier();
        Warehouse existing = warehouseRepository.findByIdentifier(identifier);

        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        if (BooleanUtils.isTrue(existing.getDeleted())) {
            dto.setSuccess(false);
            dto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        modelMapper.map(dto, existing);
        setAuditFields(existing, false);

        warehouseRepository.save(existing);

        dto.setSuccess(true);
        dto.setMessage("Warehouse updated successfully");

        return dto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);

        if (warehouse != null) {
            softDelete(warehouse);
            setAuditFields(warehouse, false);
            warehouseRepository.save(warehouse);
        }
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> page = warehouseRepository.findByDeletedFalse(pageable);

        WsDto<WarehouseDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
    @Override
    public WsDto<WarehouseDto> findAll(Specification<Warehouse> example, Pageable pageable) {

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Page<Warehouse> page = warehouseRepository.findAll(example, pageable);

        WsDto<WarehouseDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public WarehouseDto toggleStatus(String identifier) {

        Warehouse warehouse = warehouseRepository.findByIdentifier(identifier);

        if (warehouse == null) {
            WarehouseDto dto = new WarehouseDto();
            dto.setSuccess(false);
            dto.setMessage(WAREHOUSE_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        warehouse.setStatus(!warehouse.getStatus());
        setAuditFields(warehouse, false);

        warehouseRepository.save(warehouse);

        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public List<WarehouseDto> findIfTrue() {

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();

        return modelMapper.map(
                warehouseRepository.findByStatusIsTrueAndDeletedFalse(),
                listType
        );
    }
}