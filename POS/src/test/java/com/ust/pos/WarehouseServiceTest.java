package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH01");

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH01");

        when(warehouseRepository.findByIdentifier("WH01")).thenReturn(null);
        when(modelMapper.map(dto, Warehouse.class)).thenReturn(warehouse);

        WarehouseDto response = warehouseService.save(dto);

        assertEquals("WH01", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void saveFailureTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH01");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH01");

        when(warehouseRepository.findByIdentifier("WH01")).thenReturn(warehouse);

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH01");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH01");

        when(warehouseRepository.findByIdentifier("WH01")).thenReturn(warehouse);

        WarehouseDto response = warehouseService.update(dto);

        assertEquals("WH01", response.getIdentifier());
        verify(modelMapper).map(dto, warehouse);
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void updateFailureTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH01");

        when(warehouseRepository.findByIdentifier("WH01")).thenReturn(null);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        warehouseService.delete("WH01");
        verify(warehouseRepository).deleteByIdentifier("WH01");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH01");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH01");

        when(warehouseRepository.findByIdentifier("WH01")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);

        WarehouseDto response = warehouseService.findByIdentifier("WH01");

        assertNotNull(response);
        assertEquals("WH01", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(warehouseRepository.findByIdentifier("WH01")).thenReturn(null);

        WarehouseDto response = warehouseService.findByIdentifier("WH01");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Warehouse> warehouses = List.of(new Warehouse(), new Warehouse());
        Page<Warehouse> page = new PageImpl<>(warehouses);

        List<WarehouseDto> dtoList = List.of(new WarehouseDto(), new WarehouseDto());

        when(warehouseRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(warehouses),
                any(java.lang.reflect.Type.class)
        )).thenReturn(dtoList);

        List<WarehouseDto> result = warehouseService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(warehouseRepository).findAll(pageable);
    }
}