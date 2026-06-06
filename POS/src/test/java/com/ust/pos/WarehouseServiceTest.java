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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private org.modelmapper.ModelMapper modelMapper;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Test
    void findAllSuccessTest() {
        Warehouse w1 = new Warehouse();
        w1.setIdentifier("W1");

        Warehouse w2 = new Warehouse();
        w2.setIdentifier("W2");

        List<Warehouse> warehouses = List.of(w1, w2);

        WarehouseDto d1 = new WarehouseDto();
        d1.setIdentifier("W1");

        WarehouseDto d2 = new WarehouseDto();
        d2.setIdentifier("W2");

        List<WarehouseDto> warehouseDtos = List.of(d1, d2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Warehouse> warehousePage = new PageImpl<>(warehouses, pageable, warehouses.size());

        Mockito.when(warehouseRepository.findAll(pageable)).thenReturn(warehousePage);
        Mockito.when(modelMapper.map(Mockito.eq(warehouses), Mockito.any(Type.class))).thenReturn(warehouseDtos);

        List<WarehouseDto> result = warehouseService.findAll(pageable);

        Assertions.assertEquals(2, result.size());

        Mockito.verify(warehouseRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(Mockito.eq(warehouses), Mockito.any(Type.class));
    }

    @Test
    void saveSuccessTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifier("WH1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Warehouse.class)).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertEquals("WH1", result.getIdentifier());

        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Warehouse existing = new Warehouse();
        existing.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifier("WH1")).thenReturn(existing);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Warehouse already exists", result.getMessage());

        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        Warehouse existing = new Warehouse();
        existing.setIdentifier("WH2");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH2");

        Mockito.when(warehouseRepository.findByIdentifier("WH2")).thenReturn(existing);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertEquals("WH2", result.getIdentifier());

        Mockito.verify(modelMapper).map(dto, existing);
        Mockito.verify(warehouseRepository).save(existing);
    }

    @Test
    void updateFailureNotFoundTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("UNKNOWN");

        Mockito.when(warehouseRepository.findByIdentifier("UNKNOWN")).thenReturn(null);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Warehouse with identifier - UNKNOWN not found", result.getMessage());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH3");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH3");

        Mockito.when(warehouseRepository.findByIdentifier("WH3")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);

        WarehouseDto result = warehouseService.findByIdentifier("WH3");

        Assertions.assertEquals("WH3", result.getIdentifier());
    }

    @Test
    void deleteTest() {
        warehouseService.delete("WH4");

        Mockito.verify(warehouseRepository).deleteByIdentifier("WH4");
    }
}