package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private Warehouse warehouse;
    private WarehouseDto dto;

    @BeforeEach
    void setup() {
        warehouse = new Warehouse();
        warehouse.setIdentifier("WH3");
        warehouse.setDeleted(false);

        dto = new WarehouseDto();
        dto.setIdentifier("WH3");
    }

    @Test
    void findByIdentifierSuccessTest() {

        when(warehouseRepository.findByIdentifierAndIsDeletedFalse("WH3")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);

        WarehouseDto result = warehouseService.findByIdentifier("WH3");

        assertNotNull(result);
        assertEquals("WH3", result.getIdentifier());

        verify(warehouseRepository).findByIdentifierAndIsDeletedFalse("WH3");
        verify(modelMapper).map(warehouse, WarehouseDto.class);
    }

    @Test
    void findByIdentifierNotFoundTest() {
        when(warehouseRepository.findByIdentifierAndIsDeletedFalse("WH3")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> warehouseService.findByIdentifier("WH3"));

        verify(warehouseRepository).findByIdentifierAndIsDeletedFalse("WH3");
        verify(modelMapper, never()).map(any(), eq(WarehouseDto.class));
    }

    @Test
    void saveNewWarehouseTest() {
        when(warehouseRepository.findByIdentifier("WH3")).thenReturn(null);
        when(modelMapper.map(dto, Warehouse.class)).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        assertNotNull(result);

        verify(warehouseRepository).findByIdentifier("WH3");
        verify(modelMapper).map(dto, Warehouse.class);
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void saveExistingWarehouseTest() {
        when(warehouseRepository.findByIdentifier("WH3")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Warehouse already exists", result.getMessage());

        verify(warehouseRepository).findByIdentifier("WH3");
        verify(warehouseRepository, never()).save(any());
        verify(modelMapper, never()).map(dto, Warehouse.class);
    }

    @Test
    void saveDeletedWarehouseTest() {
        warehouse.setDeleted(true);

        when(warehouseRepository.findByIdentifier("WH3")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Warehouse identifier - WH3 not available", result.getMessage());

        verify(warehouseRepository).findByIdentifier("WH3");
        verify(warehouseRepository, never()).save(any());
        verify(modelMapper, never()).map(dto, Warehouse.class);
    }

    @Test
    void updateSuccessTest() {
        when(warehouseRepository.findByIdentifier("WH3")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.update(dto);

        assertNotNull(result);

        verify(warehouseRepository).findByIdentifier("WH3");
        verify(modelMapper).map(dto, warehouse);
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void updateNotFoundTest() {
        when(warehouseRepository.findByIdentifier("WH3")).thenReturn(null);

        WarehouseDto result = warehouseService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals("Warehouse with identifier - WH3 not found", result.getMessage());

        verify(warehouseRepository).findByIdentifier("WH3");
        verify(modelMapper, never()).map(any(), any());
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        when(warehouseRepository.findByIdentifier("WH3")).thenReturn(warehouse);

        warehouseService.delete("WH3");

        assertTrue(warehouse.isDeleted());

        verify(warehouseRepository).findByIdentifier("WH3");
    }

    @Test
    void findAllTest() {
        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> dtoList = List.of(dto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Warehouse> page = new PageImpl<>(warehouses, pageable, warehouses.size());

        when(warehouseRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(warehouses), any(Type.class))).thenReturn(dtoList);

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(warehouseRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllWithSpecificationTest() {
        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> dtoList = List.of(dto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Warehouse> page = new PageImpl<>(warehouses, pageable, warehouses.size());

        @SuppressWarnings("unchecked") Specification<Warehouse> spec = mock(Specification.class);

        when(warehouseRepository.findAll(spec, pageable)).thenReturn(page);
        when(modelMapper.map(eq(warehouses), any(Type.class))).thenReturn(dtoList);

        WsDto<WarehouseDto> result = warehouseService.findAll(spec, pageable, "keyword");

        assertEquals(1, result.getDtoList().size());
        assertEquals("keyword", result.getKeyword());

        verify(warehouseRepository).findAll(spec, pageable);
    }

    @Test
    void constructorTest() {
        WarehouseServiceImpl service = new WarehouseServiceImpl(warehouseRepository, modelMapper);
        assertNotNull(service);
    }
}