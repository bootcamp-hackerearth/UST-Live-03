package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
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
        warehouseDto.setIdentifier("S1");
        Mockito.when(warehouseRepository.findByIdentifier("S1")).thenReturn(null);
        Warehouse warehouse = new Warehouse();
        Mockito.when(modelMapper.map(warehouseDto, Warehouse.class)).thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        WarehouseDto response = warehouseService.save(warehouseDto);
        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("S1");
        Warehouse existingWarehouse = new Warehouse();
        Mockito.when(warehouseRepository.findByIdentifier("S1")).thenReturn(existingWarehouse);
        WarehouseDto response = warehouseService.save(warehouseDto);
        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("S1");
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("S1");
        Mockito.when(warehouseRepository.findByIdentifier("S1")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);
        WarehouseDto response = warehouseService.findByIdentifier("S1");
        Assertions.assertEquals("S1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("S1");
        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("S1");
        Mockito.when(warehouseRepository.findByIdentifier("S1"))
                .thenReturn(existingWarehouse);
        Mockito.when(warehouseRepository.save(existingWarehouse))
                .thenReturn(existingWarehouse);
        WarehouseDto response = warehouseService.update(warehouseDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("S1");
        Mockito.when(warehouseRepository.findByIdentifier("S1"))
                .thenReturn(null);
        WarehouseDto response = warehouseService.update(warehouseDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(warehouseRepository)
                .deleteByIdentifier("S1");
        warehouseService.delete("S1");
        Mockito.verify(warehouseRepository).deleteByIdentifier("S1");
    }

    @Test
    void findAllTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("W1");
        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> warehouseDtos = List.of(warehouseDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Warehouse> warehousePage = new PageImpl<>(warehouses);
        Mockito.when(warehouseRepository.findAll(pageable))
                .thenReturn(warehousePage);
        Mockito.when(modelMapper.map(
                Mockito.eq(warehouses),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(warehouseDtos);
        WsDto<WarehouseDto> response = warehouseService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("W1", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void updateStatusTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("S1");
        Mockito.when(warehouseRepository.findByIdentifier("S1"))
                .thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse))
                .thenReturn(warehouse);
        warehouseService.updateStatus("S1", true);
        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void findAllActiveTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("S1");
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("S1");
        List<Warehouse> shelves = List.of(warehouse);
        List<WarehouseDto> warehouseDtos = List.of(warehouseDto);
        Mockito.when(warehouseRepository.findByStatus(true)).thenReturn(shelves);
        Mockito.when(modelMapper.map(
                Mockito.eq(shelves),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(warehouseDtos);
        List<WarehouseDto> response = warehouseService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }
}