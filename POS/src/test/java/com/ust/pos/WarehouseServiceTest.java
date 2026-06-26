package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private WarehouseDto warehouseDto;

    @BeforeEach
    void setUp() {
        warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH-001");
        warehouseDto.setSuccess(true);

        warehouse = new Warehouse();
        warehouse.setIdentifier("WH-001");
        warehouse.setStatus(true);
        warehouse.setDeleted(false);
    }

    @Test
    void testSave_WarehouseAlreadyExists_Active() {
        warehouse.setDeleted(false);
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Warehouse with identifier - WH-001 already exists", result.getMessage());
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void testSave_WarehouseAlreadyExists_SoftDeleted() {
        warehouse.setDeleted(true);
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Brand identifier - WH-001 not available", result.getMessage());
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void testSave_NewWarehouse() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);
        when(modelMapper.map(warehouseDto, Warehouse.class)).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);
        verify(warehouseRepository, times(1)).save(warehouse);
    }

    @Test
    void testUpdate_WarehouseNotFound() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("warehouse with identifier - WH-001 not found", result.getMessage());
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void testUpdate_WarehouseFound() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertNotNull(result);
        verify(warehouseRepository, times(1)).save(warehouse);
    }

    @Test
    void testDelete_Success() {
        String identifier = "WH-001";
        when(warehouseRepository.findByIdentifierAndDeletedFalse(identifier)).thenReturn(warehouse);

        assertDoesNotThrow(() -> warehouseService.delete(identifier));

        verify(warehouseRepository, times(1)).findByIdentifierAndDeletedFalse(identifier);
        assertTrue(warehouse.getDeleted());
    }

    @Test
    void testFindAll_WithData() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Warehouse> warehouseList = Collections.singletonList(warehouse);
        List<WarehouseDto> warehouseDtoList = Collections.singletonList(warehouseDto);
        Page<Warehouse> warehousePage = new PageImpl<>(warehouseList, pageable, warehouseList.size());
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();

        when(warehouseRepository.findAllByDeletedFalse(pageable)).thenReturn(warehousePage);
        when(modelMapper.map(warehousePage.getContent(), listType)).thenReturn(warehouseDtoList);

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        verify(warehouseRepository, times(1)).findAllByDeletedFalse(pageable);
    }

    @Test
    void testFindAll_EmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Warehouse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();

        when(warehouseRepository.findAllByDeletedFalse(pageable)).thenReturn(emptyPage);
        when(modelMapper.map(emptyPage.getContent(), listType)).thenReturn(Collections.emptyList());

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getDtoList().isEmpty());
        verify(warehouseRepository, times(1)).findAllByDeletedFalse(pageable);
    }

    @Test
    void testFindByIdentifier_Found() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);

        WarehouseDto result = warehouseService.findByIdentifier("WH-001");

        assertNotNull(result);
        assertEquals("WH-001", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);
        when(modelMapper.map(null, WarehouseDto.class)).thenReturn(null);

        WarehouseDto result = warehouseService.findByIdentifier("WH-001");

        assertNull(result);
    }

    @Test
    void testFindActiveWarehouses() {
        List<Warehouse> warehouseList = Collections.singletonList(warehouse);
        List<WarehouseDto> warehouseDtoList = Collections.singletonList(warehouseDto);
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();

        when(warehouseRepository.findByStatusTrueAndDeletedFalse()).thenReturn(warehouseList);
        when(modelMapper.map(warehouseList, listType)).thenReturn(warehouseDtoList);

        List<WarehouseDto> result = warehouseService.findActiveWarehouses();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(warehouseRepository, times(1)).findByStatusTrueAndDeletedFalse();
    }

    @Test
    void testToggleStatus_TrueToFalse() {
        warehouse.setStatus(true);
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);

        warehouseService.toggleStatus("WH-001");

        assertFalse(warehouse.isStatus());
        verify(warehouseRepository, times(1)).save(warehouse);
    }

    @Test
    void testToggleStatus_FalseToTrue() {
        warehouse.setStatus(false);
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);

        warehouseService.toggleStatus("WH-001");

        assertTrue(warehouse.isStatus());
        verify(warehouseRepository, times(1)).save(warehouse);
    }

    @Test
    void testToggleStatus_WarehouseNotFound() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        warehouseService.toggleStatus("WH-001");

        verify(warehouseRepository, times(1)).findByIdentifier("WH-001");
        verify(warehouseRepository, never()).save(any());
    }
}