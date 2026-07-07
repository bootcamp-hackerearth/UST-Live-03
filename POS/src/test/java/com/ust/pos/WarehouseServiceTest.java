package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH-01");
        dto.setSuccess(true);

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH-01");

        Mockito.when(warehouseRepository.findByIdentifier("WH-01")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Warehouse.class)).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertNull(result.getMessage());
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH-01");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setDeleted(false);

        Mockito.when(warehouseRepository.findByIdentifier("WH-01")).thenReturn(existingWarehouse);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureAlreadyDeletedTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH-01");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setDeleted(true);

        Mockito.when(warehouseRepository.findByIdentifier("WH-01")).thenReturn(existingWarehouse);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was deleted"));
        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH-01");
        dto.setSuccess(true);

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("WH-01");

        Mockito.when(warehouseRepository.findByIdentifier("WH-01")).thenReturn(existingWarehouse);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertTrue(result.isSuccess());
        verify(modelMapper).map(dto, existingWarehouse);
        verify(warehouseRepository).save(existingWarehouse);
    }

    @Test
    void updateFailureNotFoundTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH-01");

        Mockito.when(warehouseRepository.findByIdentifier("WH-01")).thenReturn(null);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Warehouse warehouse = new Warehouse();
        Mockito.when(warehouseRepository.findByIdentifier("WH-01")).thenReturn(warehouse);

        warehouseService.delete("WH-01");

        verify(warehouseRepository).findByIdentifier("WH-01");
    }

    @Test
    void findAllSuccessTest() {
        Warehouse w1 = new Warehouse();
        List<Warehouse> warehouseList = List.of(w1);

        WarehouseDto d1 = new WarehouseDto();
        List<WarehouseDto> warehouseDtos = List.of(d1);

        Page<Warehouse> page = new PageImpl<>(warehouseList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(warehouseRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(warehouseList), Mockito.any(Type.class))).thenReturn(warehouseDtos);

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Warehouse warehouse = new Warehouse();
        WarehouseDto dto = new WarehouseDto();

        Mockito.when(warehouseRepository.findByIdentifierAndIsDeletedFalse("WH-01")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);

        WarehouseDto result = warehouseService.findByIdentifier("WH-01");

        Assertions.assertNotNull(result);
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(warehouseRepository.findByIdentifierAndIsDeletedFalse("WH-01")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            warehouseService.findByIdentifier("WH-01");
        });
    }

    @Test
    void findAllSpecificationSuccessTest() {
        Warehouse warehouse = new Warehouse();
        List<Warehouse> warehouseList = List.of(warehouse);

        WarehouseDto dto = new WarehouseDto();
        List<WarehouseDto> warehouseDtos = List.of(dto);

        Page<Warehouse> page = new PageImpl<>(warehouseList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Warehouse> specification = Mockito.mock(Specification.class);

        Mockito.when(warehouseRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(warehouseList), Mockito.any(Type.class))).thenReturn(warehouseDtos);

        WsDto<WarehouseDto> result = warehouseService.findAll(specification, pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }
}