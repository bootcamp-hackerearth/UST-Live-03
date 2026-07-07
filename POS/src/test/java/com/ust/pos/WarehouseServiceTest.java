package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH001");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH001");

        when(warehouseRepository.findByIdentifierAndIsDeletedFalse("WH001"))
                .thenReturn(warehouse);

        when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        WarehouseDto result = warehouseService.findByIdentifier("WH001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("WH001", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(warehouseRepository.findByIdentifierAndIsDeletedFalse("WH001"))
                .thenReturn(null);

        ResourceNotFoundException exception =
                Assertions.assertThrows(
                        ResourceNotFoundException.class,
                        () -> warehouseService.findByIdentifier("WH001"));

        Assertions.assertEquals(
                "Warehouse with identifier 'WH001' not found",
                exception.getMessage());
    }

    @Test
    void saveSuccessTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH001");

        Warehouse warehouse = new Warehouse();

        when(warehouseRepository.findByIdentifier("WH001"))
                .thenReturn(null);

        when(modelMapper.map(dto, Warehouse.class))
                .thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertEquals("WH001", result.getIdentifier());

        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void saveAlreadyExistsTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH001");

        Warehouse warehouse = new Warehouse();
        warehouse.setDeleted(false);

        when(warehouseRepository.findByIdentifier("WH001"))
                .thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Warehouse with identifier - WH001 already exists",
                result.getMessage()
        );

        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void saveDeletedWarehouseTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH001");

        Warehouse warehouse = new Warehouse();
        warehouse.setDeleted(true);

        when(warehouseRepository.findByIdentifier("WH001"))
                .thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Warehouse with identifier - WH001 was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH001");

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH001");

        when(warehouseRepository.findByIdentifier("WH001"))
                .thenReturn(warehouse);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertEquals("WH001", result.getIdentifier());

        verify(modelMapper).map(dto, warehouse);
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void updateFailureTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH001");

        when(warehouseRepository.findByIdentifier("WH001"))
                .thenReturn(null);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Warehouse with identifier - WH001 not found",
                result.getMessage()
        );

        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Warehouse warehouse = new Warehouse();

        when(warehouseRepository.findByIdentifier("WH001"))
                .thenReturn(warehouse);

        warehouseService.delete("WH001");

        verify(warehouseRepository).findByIdentifier("WH001");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Warehouse warehouse1 = new Warehouse();
        Warehouse warehouse2 = new Warehouse();

        List<Warehouse> warehouses = List.of(
                warehouse1,
                warehouse2
        );

        Page<Warehouse> page = new PageImpl<>(
                warehouses,
                pageable,
                2
        );

        List<WarehouseDto> dtoList = List.of(
                new WarehouseDto(),
                new WarehouseDto()
        );

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();

        when(warehouseRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(warehouses, listType))
                .thenReturn(dtoList);

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }
}