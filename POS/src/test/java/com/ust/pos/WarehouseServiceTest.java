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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
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
    void findByIdentifier_Found() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);

        WarehouseDto result = warehouseService.findByIdentifier("W1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("W1", result.getIdentifier());
    }

    @Test
    void save_NewWarehouse() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse warehouse = new Warehouse();

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);
        when(modelMapper.map(dto, Warehouse.class)).thenReturn(warehouse);
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("W1", result.getIdentifier());
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void save_WarehouseExists() {

        Warehouse existing = new Warehouse();

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(existing);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void update_WarehouseExists() {

        Warehouse existing = new Warehouse();

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(existing);
        when(warehouseRepository.save(existing)).thenReturn(existing);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("W1", result.getIdentifier());
        verify(modelMapper).map(dto, existing);
        verify(warehouseRepository).save(existing);
    }

    @Test
    void update_WarehouseNotFound() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        Warehouse warehouse = new Warehouse();

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        warehouseService.delete("W1");

        verify(warehouseRepository).findByIdentifier("W1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Warehouse warehouse1 = new Warehouse();
        Warehouse warehouse2 = new Warehouse();

        Page<Warehouse> page = new PageImpl<>(
                List.of(warehouse1, warehouse2),
                pageable,
                2
        );

        List<WarehouseDto> dtoList = List.of(new WarehouseDto(), new WarehouseDto());

        when(warehouseRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtoList);

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(2, result.getTotalRecords());

        verify(warehouseRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Warehouse> page = new PageImpl<>(List.of(), pageable, 0);

        when(warehouseRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of());

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalRecords());

        verify(warehouseRepository).findByIsDeletedFalse(pageable);
    }
}