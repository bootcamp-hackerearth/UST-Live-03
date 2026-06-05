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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Lays Warehouse");
        Mockito.when(warehouseRepository.findByIdentifier("Lays Warehouse")).thenReturn(null);
        Warehouse warehouse = new Warehouse();
        Mockito.when(modelMapper.map(warehouseDto, Warehouse.class)).thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        WarehouseDto response = warehouseService.save(warehouseDto);
        Assertions.assertEquals("Lays Warehouse", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Lays Warehouse");
        Warehouse warehouse = new Warehouse();
        Mockito.when(warehouseRepository.findByIdentifier("Lays Warehouse")).thenReturn(warehouse);
        WarehouseDto response = warehouseService.save(warehouseDto);
        Assertions.assertEquals("Lays Warehouse", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Lays Warehouse");
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Lays Warehouse");
        Mockito.when(warehouseRepository.findByIdentifier("Lays Warehouse")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);
        WarehouseDto response = warehouseService.findByIdentifier("Lays Warehouse");
        Assertions.assertEquals("Lays Warehouse", response.getIdentifier());
    }

    @Test
    void updateTest() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Lays Warehouse");
        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("Lays Warehouse");
        Mockito.when(warehouseRepository.findByIdentifier("Lays Warehouse")).thenReturn(existingWarehouse);
        Mockito.when(warehouseRepository.save(existingWarehouse)).thenReturn(existingWarehouse);
        WarehouseDto response = warehouseService.update(warehouseDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Lays Warehouse");
        Mockito.when(warehouseRepository.findByIdentifier("Lays Warehouse")).thenReturn(null);
        WarehouseDto response = warehouseService.update(warehouseDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(warehouseRepository).deleteByIdentifier("Lays Warehouse");
        warehouseService.delete("Lays Warehouse");
        Mockito.verify(warehouseRepository).deleteByIdentifier("Lays Warehouse");
    }

    @Test
    void findAllWithPageableTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Lays Warehouse");
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("Lays Warehouse");
        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Warehouse> warehousePage = new PageImpl<>(warehouses);
        Mockito.when(warehouseRepository.findAll(pageable)).thenReturn(warehousePage);
        Mockito.when(modelMapper.map(Mockito.eq(warehouses),Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<WarehouseDto> response = warehouseService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Lays Warehouse", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Lays Warehouse");
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("Lays Warehouse");
        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> dtos = List.of(dto);
        Mockito.when(warehouseRepository.findAll()).thenReturn(warehouses);
        Mockito.when(modelMapper.map(Mockito.eq(warehouses),Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<WarehouseDto> response = warehouseService.findAll(null);
        Assertions.assertEquals(1, response.size());
    }
}