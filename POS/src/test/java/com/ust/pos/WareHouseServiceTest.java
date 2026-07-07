package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WareHouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WareHouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private WareHouseServiceImpl warehouseService;

    private Warehouse warehouseEntity;
    private WarehouseDto warehouseDto;

    @BeforeEach
    void setUp() {
        warehouseEntity = new Warehouse();
        warehouseEntity.setId(1L);
        warehouseEntity.setIdentifier("WH-001");
        warehouseEntity.setStatus(true);
        warehouseEntity.setDeleted(false);

        warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH-001");
    }

    @Test
    void testFindByIdentifier() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouseEntity);

        WarehouseDto result = warehouseService.findByIdentifier("WH-001");

        assertNotNull(result);
        assertEquals("WH-001", result.getIdentifier());
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> warehouseService.save(null));
    }

    @Test
    void testSave_WhenIdentifierIsNull() {
        warehouseDto.setIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> warehouseService.save(warehouseDto));
    }

    @Test
    void testSave_WhenWarehouseExistsAndNotDeleted() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouseEntity);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenWarehouseWasPreviouslyDeleted() {
        warehouseEntity.setDeleted(true);
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouseEntity);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouseEntity);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Warehouse created successfully", result.getMessage());
    }

    @Test
    void testUpdate_WhenWarehouseNotFound() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenWarehousegetDeleted() {
        warehouseEntity.setDeleted(true);
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouseEntity);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouseEntity);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouseEntity);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Warehouse updated successfully", result.getMessage());
    }

    @Test
    void testDelete_WhenWarehouseNotFound() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        warehouseService.delete("WH-001");

        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void testDelete_Success() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouseEntity);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouseEntity);

        warehouseService.delete("WH-001");

        verify(warehouseRepository, times(1)).save(warehouseEntity);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Warehouse> entityList = Collections.singletonList(warehouseEntity);
        Page<Warehouse> page = new PageImpl<>(entityList, pageable, 1);

        when(warehouseRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getPage());
    }

    @Test
    void testToggleStatus_WhenWarehouseNotFound() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        WarehouseDto result = warehouseService.toggleStatus("WH-001");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus_Success() {
        warehouseEntity.setStatus(true);
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouseEntity);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouseEntity);

        WarehouseDto result = warehouseService.toggleStatus("WH-001");

        assertNotNull(result);
        assertFalse(result.getStatus());
    }

    @Test
    void testFindIfTrue() {
        List<Warehouse> activeWarehouses = Collections.singletonList(warehouseEntity);
        when(warehouseRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(activeWarehouses);

        List<WarehouseDto> result = warehouseService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}