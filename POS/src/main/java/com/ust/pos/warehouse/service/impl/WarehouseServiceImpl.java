package com.ust.pos.warehouse.service.impl;
import com.ust.pos.CommonService;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.WarehouseService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class WarehouseServiceImpl extends CommonService implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WsDto<WarehouseDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<WarehouseDto>>() {}.getType();
        Page<Warehouse> warehousePage = warehouseRepository.findByIsDeletedFalse(pageable);
        WsDto<WarehouseDto> warehouseDtoWsDto = new WsDto<>();
        warehouseDtoWsDto.setDtoList(modelMapper.map(warehousePage.getContent(), listType));
        warehouseDtoWsDto.setTotalRecords(warehousePage.getTotalElements());
        warehouseDtoWsDto.setTotalPages(warehousePage.getTotalPages());
        warehouseDtoWsDto.setSizePerPage(pageable.getPageSize());
        warehouseDtoWsDto.setPage(pageable.getPageNumber());
        return warehouseDtoWsDto;
    }

    @Override
    public WarehouseDto save(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingwarehouse = warehouseRepository.findByIdentifier(identifier);
        if (existingwarehouse != null) {
            if(existingwarehouse.isDeleted()){
                warehouseDto.setMessage("Warehouse identifier - " + identifier + " not available");
                warehouseDto.setSuccess(false);
                return warehouseDto;
            }
            warehouseDto.setMessage("Warehouse already exists");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        Warehouse warehouse = modelMapper.map(warehouseDto, Warehouse.class);
        setAuditFields(warehouse,true);
        warehouseRepository.save(warehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        String identifier = warehouseDto.getIdentifier();
        Warehouse existingWarehouse = warehouseRepository.findByIdentifier(identifier);
        if (existingWarehouse == null) {
            warehouseDto.setMessage("Warehouse with identifier - " + identifier + " not found");
            warehouseDto.setSuccess(false);
            return warehouseDto;
        }
        modelMapper.map(warehouseDto, existingWarehouse);
        setAuditFields(existingWarehouse,false);
        warehouseRepository.save(existingWarehouse);
        return warehouseDto;
    }

    @Override
    public WarehouseDto findByIdentifier(String identifier) {
        Warehouse warehouse = warehouseRepository.findByIdentifierAndIsDeletedFalse(identifier);
        if (warehouse == null) {
            throw new ResourceNotFoundException("Warehouse with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(warehouse, WarehouseDto.class);
    }

    @Override
    public void delete(String identifier) {
        Warehouse warehouse=warehouseRepository.findByIdentifier(identifier.trim());
        softDelete(warehouse);
        setAuditFields(warehouse,false);
    }
}
