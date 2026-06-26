package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceImplTest {

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
    }

    @Test
    void findByIdentifierNotFoundTest() {
        when(warehouseRepository.findByIdentifierAndIsDeletedFalse("WH3")).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> warehouseService.findByIdentifier("WH3"));
    }

    @Test
    void saveNewWarehouseTest() {
        when(warehouseRepository.findByIdentifier("WH3")).thenReturn(null);
        when(modelMapper.map(dto, Warehouse.class)).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        assertNotNull(result);
        verify(warehouseRepository, times(1)).save(warehouse);
    }

    @Test
    void saveExistingWarehouseTest() {
        when(warehouseRepository.findByIdentifier("WH3")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Warehouse already exists", result.getMessage());
    }

    @Test
    void saveDeletedWarehouseTest() {
        warehouse.setDeleted(true);

        when(warehouseRepository.findByIdentifier("WH3")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Warehouse identifier - WH3 not available", result.getMessage());
    }

    @Test
    void updateSuccessTest() {
        when(warehouseRepository.findByIdentifier("WH3")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.update(dto);

        assertNotNull(result);
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void updateNotFoundTest() {
        when(warehouseRepository.findByIdentifier("WH3")).thenReturn(null);

        WarehouseDto result = warehouseService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals("Warehouse with identifier - WH3 not found", result.getMessage());
    }

    @Test
    void deleteTest() {
        when(warehouseRepository.findByIdentifier("WH3")).thenReturn(warehouse);
        warehouseService.delete("WH3");
        verify(warehouseRepository).findByIdentifier("WH3");
    }
}