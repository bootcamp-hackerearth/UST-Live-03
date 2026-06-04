package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
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
    void findByIdentifier_shouldHandleBothCases() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        when(repository.findByIdentifier("WH1")).thenReturn(warehouse);
        when(mapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);
        assertEquals("WH1", service.findByIdentifier("WH1").getIdentifier());
        when(repository.findByIdentifier("X")).thenReturn(null);
        when(mapper.map(null, WarehouseDto.class)).thenReturn(null);
        assertNull(service.findByIdentifier("X"));
    }

    @Test
    void save_shouldHandleAllCases() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");
        when(repository.findByIdentifier("WH1")).thenReturn(null);
        when(mapper.map(dto, Warehouse.class)).thenReturn(warehouse);
        service.save(dto);
        verify(mapper).map(dto, Warehouse.class);
        verify(repository).save(warehouse);
        when(repository.findByIdentifier("WH1")).thenReturn(warehouse);
        WarehouseDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void update_shouldHandleBothCases() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");
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
    void delete_shouldCallRepository() {
        service.delete("WH1");
        verify(repository).deleteByIdentifier("WH1");
    }

    @Test
    void findAllAndToggle_shouldHandleAllCases() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<WarehouseDto>>() {}.getType();
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");
        warehouse.setStatus(true);
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        Page<Warehouse> page =
                new PageImpl<>(List.of(warehouse), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));
        assertEquals(1, service.findAll(pageable).size());
        Page<Warehouse> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findAll(pageable).isEmpty());
        when(repository.findByIdentifier("WH1")).thenReturn(warehouse);
        service.toggleStatus("WH1");
        assertFalse(warehouse.isStatus());
        verify(repository).save(warehouse);
        when(repository.findByIdentifier("X")).thenReturn(null);
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.toggleStatus("X")
        );
        assertEquals("Shelf not found", ex.getMessage());
    }
}
