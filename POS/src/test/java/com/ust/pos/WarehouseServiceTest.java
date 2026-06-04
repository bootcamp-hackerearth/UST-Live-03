package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.modell.Warehouse;
import com.ust.pos.modell.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

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
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        when(warehouseRepository.findByIdentifier("WH1")).thenReturn(null);
        Warehouse entity = new Warehouse();
        when(modelMapper.map(dto, Warehouse.class)).thenReturn(entity);
        warehouseService.save(dto);
        verify(warehouseRepository).save(entity);
        Assertions.assertTrue(dto.isSuccess());
    }

    @Test
    void saveFailureTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        when(warehouseRepository.findByIdentifier("WH1")).thenReturn(new Warehouse());
        WarehouseDto response = warehouseService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Warehouse entity = new Warehouse();
        entity.setIdentifier("WH1");
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        when(warehouseRepository.findByIdentifier("WH1")).thenReturn(entity);
        when(modelMapper.map(entity, WarehouseDto.class)).thenReturn(dto);
        WarehouseDto response = warehouseService.findByIdentifier("WH1");
        Assertions.assertEquals("WH1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(warehouseRepository.findByIdentifier("WH1")).thenReturn(null);
        WarehouseDto response = warehouseService.findByIdentifier("WH1");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Warehouse not found", response.getMessage());
    }

    @Test
    void updateTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        Warehouse entity = new Warehouse();
        entity.setIdentifier("WH1");
        when(warehouseRepository.findByIdentifier("WH1")).thenReturn(entity);
        warehouseService.update(dto);
        verify(warehouseRepository).save(entity);
        Assertions.assertTrue(dto.isSuccess());
    }

    @Test
    void updateFailureTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        when(warehouseRepository.findByIdentifier("WH1")).thenReturn(null);
        WarehouseDto response = warehouseService.update(dto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        warehouseService.delete("WH1");
        verify(warehouseRepository).deleteByIdentifier("WH1");
    }

    @Test
    void findAllActiveTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        List<Warehouse> list = List.of(warehouse);
        when(warehouseRepository.findByStatusTrue()).thenReturn(list);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);
        List<WarehouseDto> result = warehouseService.findAllActive();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("W1", result.get(0).getIdentifier());
        verify(modelMapper, times(1))
                .map(warehouse, WarehouseDto.class);
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Warehouse entity = new Warehouse();
        entity.setIdentifier("WH1");
        entity.setStatus(true);

        when(warehouseRepository.findByIdentifier("WH1")).thenReturn(entity);
        warehouseService.toggleStatus("WH1");
        Assertions.assertFalse(entity.getStatus());
        verify(warehouseRepository).save(entity);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Warehouse entity = new Warehouse();
        entity.setIdentifier("WH1");
        entity.setStatus(false);
        when(warehouseRepository.findByIdentifier("WH1")).thenReturn(entity);
        warehouseService.toggleStatus("WH1");
        Assertions.assertTrue(entity.getStatus());
    }

    @Test
    void toggleStatusNullTest() {
        Warehouse entity = new Warehouse();
        entity.setIdentifier("WH1");
        entity.setStatus(null);
        when(warehouseRepository.findByIdentifier("WH1")).thenReturn(entity);
        warehouseService.toggleStatus("WH1");
        Assertions.assertTrue(entity.getStatus());
    }

    @Test
    void toggleStatusFailureTest() {
        when(warehouseRepository.findByIdentifier("WH1")).thenReturn(null);
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> {
            warehouseService.toggleStatus("WH1");
        });
        Assertions.assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void findAllTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");
        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> warehouseDtos = List.of(warehouseDto);
        Page<Warehouse> warehousePage = new PageImpl<>(warehouses, PageRequest.of(0, 2), warehouses.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(warehouseRepository.findAll(pageable)).thenReturn(warehousePage);
        Mockito.when(modelMapper.map(Mockito.eq(warehouses), Mockito.any(java.lang.reflect.Type.class))).thenReturn(warehouseDtos);
        List<WarehouseDto> response = warehouseService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

}