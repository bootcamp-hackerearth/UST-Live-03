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
    void save_success() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifier("WH1")).thenReturn(null);

        Mockito.when(modelMapper.map(dto, Warehouse.class)).thenReturn(new Warehouse());

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertTrue(result.isSuccess());
        Mockito.verify(warehouseRepository).save(Mockito.any(Warehouse.class));
    }

    @Test
    void save_failure_duplicate() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifier("WH1")).thenReturn(new Warehouse());

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void update_success() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifier("WH1")).thenReturn(existingWarehouse);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertTrue(result.isSuccess());
        Mockito.verify(warehouseRepository).save(existingWarehouse);
    }

    @Test
    void update_failure_notFound() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifier("WH1")).thenReturn(null);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void delete_success() {

        warehouseService.delete("WH1");

        Mockito.verify(warehouseRepository).deleteByIdentifier("WH1");
    }

    @Test
    void findAll_success() {

        Warehouse w1 = new Warehouse();
        Warehouse w2 = new Warehouse();

        List<Warehouse> warehouses = List.of(w1, w2);

        WarehouseDto d1 = new WarehouseDto();
        WarehouseDto d2 = new WarehouseDto();

        List<WarehouseDto> warehouseDtos = List.of(d1, d2);

        Pageable pageable = PageRequest.of(0, 20);
        Page<Warehouse> page = new PageImpl<>(warehouses);

        Mockito.when(warehouseRepository.findAll(pageable)).thenReturn(page);

        Mockito.when(modelMapper.map(Mockito.eq(warehouses), Mockito.any(Type.class))).thenReturn(warehouseDtos);

        List<WarehouseDto> result = warehouseService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void findByIdentifier_success() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifier("WH1")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);

        WarehouseDto result = warehouseService.findByIdentifier("WH1");

        Assertions.assertEquals("WH1", result.getIdentifier());
    }
}