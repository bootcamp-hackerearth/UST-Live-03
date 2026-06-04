package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
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
    }

    @Test
    void testSave_WarehouseAlreadyExists() {

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());

        assertEquals(
                "Warehouse with identifier - WH-001 already exists",
                result.getMessage()
        );

        verify(warehouseRepository, times(1))
                .findByIdentifier("WH-001");

        verify(warehouseRepository, never())
                .save(any());

        verify(modelMapper, never())
                .map(any(), any());
    }

    @Test
    void testSave_NewWarehouse() {

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(null);

        when(modelMapper.map(warehouseDto, Warehouse.class))
                .thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);

        verify(warehouseRepository, times(1))
                .findByIdentifier("WH-001");

        verify(modelMapper, times(1))
                .map(warehouseDto, Warehouse.class);

        verify(warehouseRepository, times(1))
                .save(warehouse);
    }

    @Test
    void testUpdate_WarehouseNotFound() {

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(null);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());

        assertEquals(
                "warehouse with identifier - WH-001 not found",
                result.getMessage()
        );

        verify(warehouseRepository, times(1))
                .findByIdentifier("WH-001");

        verify(warehouseRepository, never())
                .save(any());

        verify(modelMapper, never())
                .map(any(), any());
    }

    @Test
    void testUpdate_WarehouseFound() {

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(warehouse);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertNotNull(result);

        verify(warehouseRepository, times(1))
                .findByIdentifier("WH-001");

        verify(modelMapper, times(1))
                .map(warehouseDto, warehouse);

        verify(warehouseRepository, times(1))
                .save(warehouse);
    }

    @Test
    void testDelete() {

        doNothing().when(warehouseRepository)
                .deleteByIdentifier("WH-001");

        warehouseService.delete("WH-001");

        verify(warehouseRepository, times(1))
                .deleteByIdentifier("WH-001");
    }

    @Test
    void testFindAll_WithData() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Warehouse> warehouseList =
                Collections.singletonList(warehouse);

        List<WarehouseDto> warehouseDtoList =
                Collections.singletonList(warehouseDto);

        Page<Warehouse> warehousePage =
                new PageImpl<>(warehouseList, pageable, warehouseList.size());

        Type listType =
                new TypeToken<List<WarehouseDto>>() {
                }.getType();

        when(warehouseRepository.findAll(pageable))
                .thenReturn(warehousePage);

        when(modelMapper.map(warehousePage.getContent(), listType))
                .thenReturn(warehouseDtoList);

        List<WarehouseDto> result =
                warehouseService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(warehouseRepository, times(1))
                .findAll(pageable);

        verify(modelMapper, times(1))
                .map(warehousePage.getContent(), listType);
    }

    @Test
    void testFindAll_EmptyList() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Warehouse> emptyPage =
                new PageImpl<>(Collections.emptyList());

        Type listType =
                new TypeToken<List<WarehouseDto>>() {
                }.getType();

        when(warehouseRepository.findAll(pageable))
                .thenReturn(emptyPage);

        when(modelMapper.map(emptyPage.getContent(), listType))
                .thenReturn(Collections.emptyList());

        List<WarehouseDto> result =
                warehouseService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(warehouseRepository, times(1))
                .findAll(pageable);

        verify(modelMapper, times(1))
                .map(emptyPage.getContent(), listType);
    }

    @Test
    void testFindByIdentifier_Found() {

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(warehouse);

        when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(warehouseDto);

        WarehouseDto result =
                warehouseService.findByIdentifier("WH-001");

        assertNotNull(result);
        assertEquals("WH-001", result.getIdentifier());

        verify(warehouseRepository, times(1))
                .findByIdentifier("WH-001");

        verify(modelMapper, times(1))
                .map(warehouse, WarehouseDto.class);
    }

    @Test
    void testFindByIdentifier_NotFound() {

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(null);

        when(modelMapper.map(null, WarehouseDto.class))
                .thenReturn(null);

        WarehouseDto result =
                warehouseService.findByIdentifier("WH-001");

        assertNull(result);

        verify(warehouseRepository, times(1))
                .findByIdentifier("WH-001");

        verify(modelMapper, times(1))
                .map(null, WarehouseDto.class);
    }

    @Test
    void testFindActiveWarehouses() {

        when(warehouseRepository.findByStatus(true))
                .thenReturn(Collections.singletonList(warehouse));

        List<Warehouse> result =
                warehouseService.findActiveWarehouses();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(warehouseRepository, times(1))
                .findByStatus(true);
    }

    @Test
    void testToggleStatus_TrueToFalse() {

        warehouse.setStatus(true);

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(warehouse);

        warehouseService.toggleStatus("WH-001");

        assertFalse(warehouse.isStatus());

        verify(warehouseRepository, times(1))
                .save(warehouse);
    }

    @Test
    void testToggleStatus_FalseToTrue() {

        warehouse.setStatus(false);

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(warehouse);

        warehouseService.toggleStatus("WH-001");

        assertTrue(warehouse.isStatus());

        verify(warehouseRepository, times(1))
                .save(warehouse);
    }

    @Test
    void testToggleStatus_WarehouseNotFound() {

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(null);

        warehouseService.toggleStatus("WH-001");

        verify(warehouseRepository, times(1))
                .findByIdentifier("WH-001");

        verify(warehouseRepository, never())
                .save(any());
    }
}