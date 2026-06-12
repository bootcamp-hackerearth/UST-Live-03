package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Warehouse;
import com.ust.pos.modell.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @InjectMocks
    private WarehouseServiceImpl service;

    @Mock
    private WarehouseRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifierTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        when(repository.findByIdentifier("WH1")).thenReturn(warehouse);
        when(mapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);

        WarehouseDto success = service.findByIdentifier("WH1");
        assertEquals("WH1", success.getIdentifier());
        assertTrue(success.isSuccess());

        when(repository.findByIdentifier("X")).thenReturn(null);

        WarehouseDto failure = service.findByIdentifier("X");
        assertNotNull(failure);
        assertFalse(failure.isSuccess());
        assertEquals("Warehouse not found", failure.getMessage());
    }

    @Test
    void saveTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        when(repository.findByIdentifier("WH1")).thenReturn(null);
        when(mapper.map(dto, Warehouse.class)).thenReturn(warehouse);

        service.save(dto);
        verify(repository).save(warehouse);

        when(repository.findByIdentifier("WH1")).thenReturn(warehouse);

        WarehouseDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void updateTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        when(repository.findByIdentifier("WH1")).thenReturn(warehouse);

        service.update(dto);
        verify(mapper).map(dto, warehouse);
        verify(repository).save(warehouse);

        when(repository.findByIdentifier("X")).thenReturn(null);
        dto.setIdentifier("X");

        WarehouseDto fail = service.update(dto);
        assertFalse(fail.isSuccess());
        assertTrue(fail.getMessage().contains("not found"));
    }

    @Test
    void deleteTest() {
        service.delete("WH1");
        verify(repository).deleteByIdentifier("WH1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Type type = new TypeToken<List<WarehouseDto>>() {}.getType();

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Page<Warehouse> page = new PageImpl<>(List.of(warehouse), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(page.getContent(), type)).thenReturn(List.of(dto));

        WsDto<WarehouseDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());

        Page<Warehouse> empty = new PageImpl<>(List.of(), pageable, 0);

        when(repository.findAll(pageable)).thenReturn(empty);
        when(mapper.map(empty.getContent(), type)).thenReturn(List.of());

        WsDto<WarehouseDto> emptyResult = service.findAll(pageable);
        assertTrue(emptyResult.getDtoList().isEmpty());
    }

    @Test
    void toggleStatusTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");
        warehouse.setStatus(true);

        when(repository.findByIdentifier("WH1")).thenReturn(warehouse);
        when(repository.save(any(Warehouse.class))).thenReturn(warehouse);

        service.toggleStatus("WH1");
        assertFalse(warehouse.getStatus());
        verify(repository).save(warehouse);

        when(repository.findByIdentifier("X")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.toggleStatus("X"));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void findAllActiveTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        when(repository.findByStatusTrue()).thenReturn(List.of(warehouse));
        when(mapper.map(any(Warehouse.class), eq(WarehouseDto.class))).thenReturn(dto);

        List<WarehouseDto> result = service.findAllActive();
        assertEquals(1, result.size());

        when(repository.findByStatusTrue()).thenReturn(List.of());

        List<WarehouseDto> empty = service.findAllActive();
        assertTrue(empty.isEmpty());
    }
}