package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @InjectMocks
    private WarehouseServiceImpl service;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Warehouse> page = new PageImpl<>(List.of(new Warehouse()), pageable, 1);

        when(warehouseRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new WarehouseDto()));

        WsDto<WarehouseDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findByIdentifierTest() {
        Warehouse entity = new Warehouse();
        WarehouseDto dto = new WarehouseDto();

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(entity);
        when(modelMapper.map(entity, WarehouseDto.class)).thenReturn(dto);

        WarehouseDto result = service.findByIdentifier("W1");

        assertNotNull(result);
    }

    @Test
    void saveSuccessTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);
        when(modelMapper.map(dto, Warehouse.class)).thenReturn(new Warehouse());

        WarehouseDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(warehouseRepository).save(any());
    }

    @Test
    void saveDuplicateActiveTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse existing = new Warehouse();
        existing.setDeleted(false);

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(existing);

        WarehouseDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void saveDuplicateDeletedTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse existing = new Warehouse();
        existing.setDeleted(true);

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(existing);

        WarehouseDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void updateSuccessTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse existing = new Warehouse();

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(existing);

        WarehouseDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(warehouseRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);

        WarehouseDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setDeleted(false);

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        service.delete("W1");

        assertTrue(warehouse.isDeleted());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setStatus(true);

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        service.toggleStatus("W1");

        assertFalse(warehouse.isStatus());
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setStatus(false);

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        service.toggleStatus("W1");

        assertTrue(warehouse.isStatus());
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);

        service.toggleStatus("W1");

        verify(warehouseRepository, never()).save(any());
    }
}