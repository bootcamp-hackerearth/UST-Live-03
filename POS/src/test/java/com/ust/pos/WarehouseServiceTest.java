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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    void findAllTestSuccess()
    {
        Warehouse w1 = new Warehouse();
        Warehouse w2 = new Warehouse();

        List<Warehouse> warehouses = new ArrayList<>();
        warehouses.add(w1);
        warehouses.add(w2);

        List<WarehouseDto> dtoList = new ArrayList<>();
        dtoList.add(new WarehouseDto());
        dtoList.add(new WarehouseDto());

        Mockito.when(warehouseRepository.findAll()).thenReturn(warehouses);
        Mockito.when(modelMapper.map(Mockito.eq(warehouses), Mockito.any(Type.class))).thenReturn(dtoList);

        List<WarehouseDto> response = warehouseService.findAll();
        Assertions.assertEquals(2, response.size());
    }

    @Test
    void saveTestSuccess()
    {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH1");

        Warehouse warehouse = new Warehouse();

        Mockito.when(warehouseRepository.findByIdentifier("WH1")).thenReturn(null);

        Mockito.when(modelMapper.map(warehouseDto, Warehouse.class)).thenReturn(warehouse);
        WarehouseDto response = warehouseService.save(warehouseDto);
        Assertions.assertEquals("WH1", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
    }
    @Test
    void findAll_WithPagination_ShouldReturnWarehouseDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Warehouse> warehouses = List.of(new Warehouse());
        Page<Warehouse> page = new PageImpl<>(warehouses);

        List<WarehouseDto> warehouseDtos = List.of(new WarehouseDto());

        Type listType = new TypeToken<List<WarehouseDto>>() {}.getType();

        Mockito.when(warehouseRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(warehouses, listType))
                .thenReturn(warehouseDtos);

        List<WarehouseDto> response = warehouseService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(warehouseRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(warehouses, listType);
    }

    @Test
    void saveTestFail()
    {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifier("WH1")).thenReturn(new Warehouse());

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Warehouse already exists", response.getMessage());
    }

    @Test
    void upateTestSuccess()
    {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse warehouse = new Warehouse();

        Mockito.when(warehouseRepository.findByIdentifier("WH1")).thenReturn(warehouse);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertNull(response.getMessage());
    }

    @Test
    void updateTestFail()
    {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifier("WH1")).thenReturn(null);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Warehouse with identifier - WH1 not found", response.getMessage());
    }

    @Test
    void findWarehouseByIdentifierTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        WarehouseDto response = warehouseService.findByIdentifier("WH1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("WH1", response.getIdentifier());
    }

    @Test
    void deleteWarehouseTest() {
        String identifier = "WH1";

        Mockito.doNothing()
                .when(warehouseRepository)
                .deleteByIdentifier(identifier);

        warehouseService.delete(identifier);

        Mockito.verify(warehouseRepository)
                .deleteByIdentifier(identifier);
    }

    @Test
    void toggleWarehouseStatusSuccess() {
        Warehouse warehouse = new Warehouse();
        warehouse.setStatus(true);

        Mockito.when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(warehouse);

        warehouseService.toggleStatus("WH1");

        Assertions.assertFalse(warehouse.getStatus());
        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void toggleWarehouseStatusNotFound() {
        Mockito.when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(null);

        warehouseService.toggleStatus("WH1");

        Mockito.verify(warehouseRepository, Mockito.never())
                .save(Mockito.any());
    }
}
