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

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTestSuccess() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse warehouse = new Warehouse();

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Warehouse.class))
                .thenReturn(warehouse);

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertEquals("WH1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void saveTestFailure() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(new Warehouse());

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestSuccess() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse existing = new Warehouse();

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(existing);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertEquals("WH1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(modelMapper).map(dto, existing);
        verify(warehouseRepository).save(existing);
    }

    @Test
    void updateTestFailure() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(null);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {

        warehouseService.delete("WH1");

        verify(warehouseRepository).deleteByIdentifier("WH1");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Warehouse warehouse = new Warehouse();
        WarehouseDto dto = new WarehouseDto();

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(warehouse);

        when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        WarehouseDto response = warehouseService.findByIdentifier("WH1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(dto, response);
    }

    @Test
    void findByIdentifierFailureTest() {

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(null);

        WarehouseDto response = warehouseService.findByIdentifier("WH1");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Warehouse> warehouses = List.of(new Warehouse());
        Page<Warehouse> page = new PageImpl<>(warehouses);

        List<WarehouseDto> dtos = List.of(new WarehouseDto());

        when(warehouseRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(warehouses),
                any(Type.class)
        )).thenReturn(dtos);

        List<WarehouseDto> result = warehouseService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(warehouseRepository).findAll(pageable);
    }
}
