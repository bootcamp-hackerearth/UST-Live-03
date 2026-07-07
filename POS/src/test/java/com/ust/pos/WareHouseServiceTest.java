package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

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

    private Warehouse warehouse;
    private WarehouseDto warehouseDto;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setIdentifier("WH-001");
        warehouse.setStatus(true);
        warehouse.setDeleted(false);

        warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH-001");
    }

    @Test
    void testFindByIdentifier_Success() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.findByIdentifier("WH-001");

        assertNotNull(result);
        assertEquals("WH-001", result.getIdentifier());
        verify(warehouseRepository, times(1)).findByIdentifier("WH-001");
    }

    @Test
    void testFindByIdentifier_ThrowsResourceNotFoundException() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> warehouseService.findByIdentifier("WH-001"));
        verify(warehouseRepository, times(1)).findByIdentifier("WH-001");
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
    void testSave_WhenWarehouseAlreadyExistsAndNotDeleted() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void testSave_WhenWarehouseAlreadyExistsButDeleted() {
        warehouse.setDeleted(true);
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void testSave_Success() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Warehouse created successfully", result.getMessage());
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    void testUpdate_WhenWarehouseNotFound() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void testUpdate_WhenWarehouseDeleted() {
        warehouse.setDeleted(true);
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void testUpdate_Success() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Warehouse updated successfully", result.getMessage());
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    void testDelete_WhenWarehouseNotFound() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        warehouseService.delete("WH-001");

        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void testDelete_Success() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        warehouseService.delete("WH-001");

        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Warehouse> page = new PageImpl<>(Collections.singletonList(warehouse), pageable, 1);
        when(warehouseRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());
        verify(warehouseRepository, times(1)).findByDeletedFalse(pageable);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Warehouse> page = new PageImpl<>(Collections.singletonList(warehouse), pageable, 1);
        Specification<Warehouse> spec = mock(Specification.class);
        when(warehouseRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        WsDto<WarehouseDto> result = warehouseService.findAll(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());
        verify(warehouseRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void testToggleStatus_WhenWarehouseNotFound() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        WarehouseDto result = warehouseService.toggleStatus("WH-001");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void testToggleStatus_Success() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseDto result = warehouseService.toggleStatus("WH-001");

        assertNotNull(result);
        assertFalse(result.isStatus());
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    void testFindIfTrue() {
        when(warehouseRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(Collections.singletonList(warehouse));

        List<WarehouseDto> result = warehouseService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WH-001", result.get(0).getIdentifier());
        verify(warehouseRepository, times(1)).findByStatusIsTrueAndDeletedFalse();
    }
}