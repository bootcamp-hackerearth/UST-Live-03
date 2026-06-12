package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
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
        warehouseDto.setIdentifier("Admin");

        Mockito.when(warehouseRepository.findByIdentifier("Admin")).thenReturn(null);
        Warehouse warehouse = new Warehouse();
        Mockito.when(modelMapper.map(warehouseDto, Warehouse.class)).thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        WarehouseDto response = warehouseService.save(warehouseDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");
        Warehouse warehouse = new Warehouse();

        Mockito.when(warehouseRepository.findByIdentifier("Admin")).thenReturn(warehouse);
        WarehouseDto response = warehouseService.save(warehouseDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());

    }

    @Test
    void findByIdentifierTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Mockito.when(warehouseRepository.findByIdentifier("Admin")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);

        WarehouseDto response = warehouseService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("Admin");

        Mockito.when(warehouseRepository.findByIdentifier("Admin"))
                .thenReturn(existingWarehouse);
        Mockito.when(warehouseRepository.save(existingWarehouse))
                .thenReturn(existingWarehouse);

        WarehouseDto response = warehouseService.update(warehouseDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Mockito.when(warehouseRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        WarehouseDto response = warehouseService.update(warehouseDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(warehouseRepository)
                .deleteByIdentifier("Admin");

        warehouseService.delete("Admin");

        Mockito.verify(warehouseRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> warehouseDtos = List.of(warehouseDto);

        Page<Warehouse> warehousePage = new PageImpl<>(warehouses);

        Mockito.when(warehouseRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(warehousePage);

        Mockito.when(modelMapper.map(
                Mockito.eq(warehouses),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(warehouseDtos);

        PaginatedResponseDto<WarehouseDto> response = warehouseService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
    }

    @Test
    void findAllActiveTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");
        warehouse.setStatus(true);

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> warehouseDtos = List.of(warehouseDto);

        Mockito.when(warehouseRepository.findByStatus(true)).thenReturn(warehouses);
        Mockito.when(modelMapper.map(
                Mockito.eq(warehouses),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(warehouseDtos);

        List<WarehouseDto> response = warehouseService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");
        warehouse.setStatus(false);

        Mockito.when(warehouseRepository.findByIdentifier("Admin"))
                .thenReturn(warehouse);

        Mockito.when(warehouseRepository.save(warehouse))
                .thenReturn(warehouse);

        warehouseService.changeStatus("Admin", true);

        Assertions.assertTrue(warehouse.getStatus());

        Mockito.verify(warehouseRepository).save(warehouse);
    }
}