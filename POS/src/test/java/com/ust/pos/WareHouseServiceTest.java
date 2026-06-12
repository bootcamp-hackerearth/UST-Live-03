package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WareHouseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WareHouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private WareHouseServiceImpl warehouseService;

    private Warehouse warehouse;
    private WarehouseDto warehouseDto;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setIdentifier("W001");
        warehouse.setStatus(true);

        warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("W001");
        warehouseDto.setStatus(true);
    }

    @Test
    void findByIdentifier_shouldReturnWarehouseDto() {
        when(warehouseRepository.findByIdentifier("W001")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);

        WarehouseDto result = warehouseService.findByIdentifier("W001");

        assertNotNull(result);
        assertEquals("W001", result.getIdentifier());
    }

    @Test
    void save_shouldCreateWarehouse_whenNotExists() {
        when(warehouseRepository.findByIdentifier("W001")).thenReturn(null);
        when(modelMapper.map(warehouseDto, Warehouse.class)).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        verify(warehouseRepository).save(warehouse);
        assertEquals("W001", result.getIdentifier());
    }

    @Test
    void save_shouldFail_whenWarehouseExists() {
        when(warehouseRepository.findByIdentifier("W001")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateWarehouse_whenExists() {
        when(warehouseRepository.findByIdentifier("W001")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.update(warehouseDto);

        verify(modelMapper).map(warehouseDto, warehouse);
        verify(warehouseRepository).save(warehouse);
        assertEquals("W001", result.getIdentifier());
    }

    @Test
    void update_shouldFail_whenWarehouseNotFound() {
        when(warehouseRepository.findByIdentifier("W001")).thenReturn(null);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteWarehouse() {
        doNothing().when(warehouseRepository).deleteByIdentifier("W001");

        warehouseService.delete("W001");

        verify(warehouseRepository).deleteByIdentifier("W001");
    }

    @Test
    void findAllTest() {
        Warehouse warehouse1 = new Warehouse();
        warehouse1.setIdentifier("Admin");

        WarehouseDto warehouseDto1 = new WarehouseDto();
        warehouseDto1.setIdentifier("Admin");

        List<Warehouse> warehouses = List.of(warehouse1);
        List<WarehouseDto> warehouseDtos = List.of(warehouseDto1);

        Page<Warehouse> warehousePage = new PageImpl<>(warehouses, PageRequest.of(0, 2), warehouses.size());

        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));

        Mockito.when(warehouseRepository.findAll(pageable)).thenReturn(warehousePage);
        Mockito.when(modelMapper.map(Mockito.eq(warehouses), Mockito.any(java.lang.reflect.Type.class))).thenReturn(warehouseDtos);

        List<WarehouseDto> response = warehouseService.findAll(pageable).getDtoList();

        Assertions.assertEquals(1, response.size());
    }


    @Test
    void toggleStatus_shouldToggleWarehouseStatus_fromTrueToFalse() {
        warehouse.setStatus(true);

        when(warehouseRepository.findByIdentifier("W001")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);

        WarehouseDto result = warehouseService.toggleStatus("W001");

        assertFalse(warehouse.isStatus());
        verify(warehouseRepository).save(warehouse);
        assertNotNull(result);
    }

    @Test
    void toggleStatus_shouldToggleWarehouseStatus_fromFalseToTrue() {
        warehouse.setStatus(false);

        when(warehouseRepository.findByIdentifier("W001")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);

        WarehouseDto result = warehouseService.toggleStatus("W001");

        assertTrue(warehouse.isStatus());
        verify(warehouseRepository).save(warehouse);
        assertNotNull(result);
    }

    @Test
    void findIfTrue_shouldReturnActiveWarehouses() {
        when(warehouseRepository.findByStatusIsTrue()).thenReturn(List.of(warehouse));

        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(warehouseDto));

        List<WarehouseDto> result = warehouseService.findIfTrue();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isStatus());
    }
}