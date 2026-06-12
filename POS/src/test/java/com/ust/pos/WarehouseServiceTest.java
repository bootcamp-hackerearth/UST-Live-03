package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Test
    @DisplayName("Save Warehouse - Success Case")
    void saveTest_Success() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH-MAIN");
        Warehouse warehouse = new Warehouse();

        Mockito.when(warehouseRepository.findByIdentifier("WH-MAIN")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Warehouse.class)).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertNotNull(result);
        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    @DisplayName("Save Warehouse - Failure: Already Exists")
    void saveTest_AlreadyExists() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH-MAIN");
        Mockito.when(warehouseRepository.findByIdentifier("WH-MAIN")).thenReturn(new Warehouse());

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        Mockito.verify(warehouseRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Update Warehouse - Success Case")
    void updateTest_Success() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH-MAIN");
        Warehouse existingWarehouse = new Warehouse();

        Mockito.when(warehouseRepository.findByIdentifier("WH-MAIN")).thenReturn(existingWarehouse);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertNotNull(result);
        Mockito.verify(modelMapper).map(dto, existingWarehouse);
        Mockito.verify(warehouseRepository).save(existingWarehouse);
    }

    @Test
    @DisplayName("Update Warehouse - Failure: Not Found")
    void updateTest_NotFound() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH-MAIN");
        Mockito.when(warehouseRepository.findByIdentifier("WH-MAIN")).thenReturn(null);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        Mockito.verify(warehouseRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Find All - Paginated Success")
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Warehouse> warehouses = List.of(new Warehouse());
        Page<Warehouse> warehousePage = new PageImpl<>(warehouses);
        List<WarehouseDto> dtos = List.of(new WarehouseDto());

        Mockito.when(warehouseRepository.findAll(pageable)).thenReturn(warehousePage);
        Mockito.when(modelMapper.map(eq(warehouses), any(Type.class))).thenReturn(dtos);

        List<WarehouseDto> result = warehouseService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
        Mockito.verify(warehouseRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Find All Active - Success Case")
    void findAllActiveTest() {
        List<Warehouse> warehouses = List.of(new Warehouse());
        List<WarehouseDto> dtos = List.of(new WarehouseDto());

        Mockito.when(warehouseRepository.findAllByStatus(true)).thenReturn(warehouses);
        Mockito.when(modelMapper.map(eq(warehouses), any(Type.class))).thenReturn(dtos);

        List<WarehouseDto> result = warehouseService.findAllActive();
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success Case")
    void findByIdentifierTest() {
        Warehouse warehouse = new Warehouse();
        WarehouseDto dto = new WarehouseDto();
        Mockito.when(warehouseRepository.findByIdentifier("WH-MAIN")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);

        WarehouseDto result = warehouseService.findByIdentifier("WH-MAIN");
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Toggle Status - Logic Flip")
    void toggleStatusTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setStatus(false);
        WarehouseDto dto = new WarehouseDto();

        Mockito.when(warehouseRepository.findByIdentifier("WH-MAIN")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);

        WarehouseDto result = warehouseService.toggleStatus("WH-MAIN");

        Assertions.assertTrue(warehouse.isStatus());
        Mockito.verify(warehouseRepository).save(warehouse);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Delete Warehouse - Success Case")
    void deleteTest() {
        boolean result = warehouseService.delete("WH-MAIN");
        Assertions.assertTrue(result);
        Mockito.verify(warehouseRepository).deleteByIdentifier("WH-MAIN");
    }
}