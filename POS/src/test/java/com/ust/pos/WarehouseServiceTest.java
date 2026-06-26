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
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("S1");
        Mockito.when(warehouseRepository.findByIdentifier("S1")).thenReturn(null);
        Warehouse warehouse = new Warehouse();
        Mockito.when(modelMapper.map(dto, Warehouse.class)).thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        WarehouseDto response = warehouseService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestAlreadyExists() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("S1");
        Warehouse existing = new Warehouse();
        existing.setDeleted(false);
        Mockito.when(warehouseRepository.findByIdentifier("S1")).thenReturn(existing);
        WarehouseDto response = warehouseService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestDeletedExists() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("S1");
        Warehouse existing = new Warehouse();
        existing.setDeleted(true);
        Mockito.when(warehouseRepository.findByIdentifier("S1")).thenReturn(existing);
        WarehouseDto response = warehouseService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("S1");
        Warehouse existing = new Warehouse();
        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(warehouseRepository.save(existing)).thenReturn(existing);
        WarehouseDto response = warehouseService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("S1");
        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        WarehouseDto response = warehouseService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Warehouse warehouse = new Warehouse();
        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        warehouseService.delete("S1");
        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void deleteNullTest() {
        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        warehouseService.delete("S1");
        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllTest() {
        Warehouse warehouse = new Warehouse();
        WarehouseDto dto = new WarehouseDto();
        List<Warehouse> list = List.of(warehouse);
        List<WarehouseDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Warehouse> page = new PageImpl<>(list);
        Mockito.when(warehouseRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        WsDto<WarehouseDto> response = warehouseService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findByIdentifierTest() {
        Warehouse warehouse = new Warehouse();
        WarehouseDto dto = new WarehouseDto();
        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);
        WarehouseDto response = warehouseService.findByIdentifier("S1");
        Assertions.assertNotNull(response);
    }

    @Test
    void updateStatusTest() {
        Warehouse warehouse = new Warehouse();
        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        warehouseService.updateStatus("S1", true);
        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void updateStatusNullTest() {
        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        warehouseService.updateStatus("S1", true);
        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllActiveTest() {
        Warehouse warehouse = new Warehouse();
        WarehouseDto dto = new WarehouseDto();
        List<Warehouse> list = List.of(warehouse);
        List<WarehouseDto> dtoList = List.of(dto);
        Mockito.when(warehouseRepository.findByStatusAndDeletedFalse(true)).thenReturn(list);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        List<WarehouseDto> response = warehouseService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }
}