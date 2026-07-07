package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    private static final String ID = "WH001";
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private WarehouseServiceImpl service;

    private Warehouse warehouse() {

        Warehouse w = new Warehouse();

        w.setIdentifier(ID);
        w.setStatus(true);

        return w;
    }

    @Test
    void saveSuccess() {

        WarehouseDto dto = new WarehouseDto();

        dto.setIdentifier(" WH001 ");

        Warehouse warehouse = warehouse();

        when(warehouseRepository.findByIdentifier("WH001")).thenReturn(null);

        when(modelMapper.map(dto, Warehouse.class)).thenReturn(warehouse);

        WarehouseDto response = new WarehouseDto();

        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(response);

        WarehouseDto result = service.save(dto);

        assertTrue(result.isSuccess());

        verify(warehouseRepository).save(warehouse);

    }

    @Test
    void saveAlreadyExists() {

        Warehouse existing = warehouse();

        WarehouseDto dto = new WarehouseDto();

        dto.setIdentifier(ID);

        when(warehouseRepository.findByIdentifier(ID)).thenReturn(existing);

        WarehouseDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Warehouse with identifier WH001 already exists", result.getMessage());

    }

    @Test
    void saveSoftDeleted() {

        Warehouse existing = warehouse();

        existing.setDeleted(true);

        WarehouseDto dto = new WarehouseDto();

        dto.setIdentifier(ID);

        when(warehouseRepository.findByIdentifier(ID)).thenReturn(existing);

        WarehouseDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertTrue(result.getMessage().contains("soft deleted"));

    }

    @Test
    void updateSuccess() {

        Warehouse warehouse = warehouse();

        warehouse.setStatus(true);

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier(ID);

        when(warehouseRepository.findByIdentifier(ID)).thenReturn(warehouse);

        doNothing().when(modelMapper).map(dto, warehouse);

        WarehouseDto response = new WarehouseDto();

        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(response);

        WarehouseDto result = service.update(dto);

        assertTrue(result.isSuccess());

        verify(modelMapper).map(dto, warehouse);

        verify(warehouseRepository).save(warehouse);

    }

    @Test
    void updateNotFound() {

        WarehouseDto dto = new WarehouseDto();

        dto.setIdentifier(ID);

        when(warehouseRepository.findByIdentifier(ID)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.update(dto));

    }

    @Test
    void findByIdentifierSuccess() {

        Warehouse warehouse = warehouse();

        WarehouseDto dto = new WarehouseDto();

        when(warehouseRepository.findByIdentifier(ID)).thenReturn(warehouse);

        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);

        WarehouseDto result = service.findByIdentifier(ID);

        assertNotNull(result);

    }

    @Test
    void findByIdentifierNotFound() {

        when(warehouseRepository.findByIdentifier(ID)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.findByIdentifier(ID));

    }

    @Test
    void findAllSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Warehouse> entities = List.of(warehouse());

        List<WarehouseDto> dtos = List.of(new WarehouseDto());

        Page<Warehouse> page = new PageImpl<>(entities, pageable, 1);

        when(warehouseRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtos);

        WsDto<WarehouseDto> ws = service.findAll(pageable);

        assertEquals(1, ws.getDtoList().size());

    }

    @Test
    void findAllEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Warehouse> page = new PageImpl<>(Collections.emptyList());

        when(warehouseRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        WsDto<WarehouseDto> ws = service.findAll(pageable);

        assertEquals(0, ws.getDtoList().size());

    }

    @Test
    void findAllSpecificationSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Warehouse> spec = mock(Specification.class);

        List<Warehouse> warehouses = List.of(warehouse());

        List<WarehouseDto> dtos = List.of(new WarehouseDto());

        Page<Warehouse> page = new PageImpl<>(warehouses);

        when(warehouseRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtos);

        WsDto<WarehouseDto> ws = service.findAll(spec, pageable, "warehouse");

        assertEquals("warehouse", ws.getKeyword());

        assertEquals(1, ws.getDtoList().size());

    }

    @Test
    void findAllSpecificationEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Warehouse> spec = mock(Specification.class);

        Page<Warehouse> page = new PageImpl<>(Collections.emptyList());

        when(warehouseRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        WsDto<WarehouseDto> ws = service.findAll(spec, pageable, "abc");

        assertEquals("abc", ws.getKeyword());

        assertEquals(0, ws.getDtoList().size());

    }

    @Test
    void deleteSuccess() {

        Warehouse warehouse = warehouse();

        when(warehouseRepository.findByIdentifier(ID)).thenReturn(warehouse);

        assertTrue(service.delete(ID));

        verify(warehouseRepository).save(warehouse);

    }

    @Test
    void deleteNotFound() {

        when(warehouseRepository.findByIdentifier(ID)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(ID));

    }

    @Test
    void toggleStatusTrueFalse() {

        Warehouse warehouse = warehouse();

        warehouse.setStatus(true);

        when(warehouseRepository.findByIdentifier(ID)).thenReturn(warehouse);

        service.toggleStatus(ID);

        assertFalse(warehouse.isStatus());

        verify(warehouseRepository).save(warehouse);

    }

    @Test
    void toggleStatusFalseTrue() {

        Warehouse warehouse = warehouse();

        warehouse.setStatus(false);

        when(warehouseRepository.findByIdentifier(ID)).thenReturn(warehouse);

        service.toggleStatus(ID);

        assertTrue(warehouse.isStatus());

    }

    @Test
    void toggleStatusNotFound() {

        when(warehouseRepository.findByIdentifier(ID)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.toggleStatus(ID));

    }

    @Test
    void findIfTrueSuccess() {

        List<Warehouse> warehouses = List.of(warehouse());

        List<WarehouseDto> dtos = List.of(new WarehouseDto());

        when(warehouseRepository.findByStatusIsTrue()).thenReturn(warehouses);

        when(modelMapper.map(eq(warehouses), any(Type.class))).thenReturn(dtos);

        List<WarehouseDto> result = service.findIfTrue();

        assertEquals(1, result.size());

    }

    @Test
    void findIfTrueEmpty() {

        when(warehouseRepository.findByStatusIsTrue()).thenReturn(Collections.emptyList());

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        List<WarehouseDto> result = service.findIfTrue();

        assertTrue(result.isEmpty());

    }

}