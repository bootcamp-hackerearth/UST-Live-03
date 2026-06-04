package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.modell.Shelf;
import com.ust.pos.modell.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
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
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl service;

    @Mock
    private ShelfRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifier_shouldHandleBothCases() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        when(repository.findByIdentifier("S1")).thenReturn(shelf);
        when(mapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        assertEquals("S1",
                service.findByIdentifier("S1").getIdentifier());
        when(repository.findByIdentifier("X")).thenReturn(null);
        when(mapper.map(null, ShelfDto.class)).thenReturn(null);
        assertNull(service.findByIdentifier("X"));
    }

    @Test
    void save_shouldHandleAllCases() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");
        when(repository.findByIdentifier("S1")).thenReturn(null);
        when(mapper.map(dto, Shelf.class)).thenReturn(shelf);
        service.save(dto);
        verify(mapper).map(dto, Shelf.class);
        verify(repository).save(shelf);
        when(repository.findByIdentifier("S1")).thenReturn(shelf);
        ShelfDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void update_shouldHandleBothCases() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");
        when(repository.findByIdentifier("S1")).thenReturn(shelf);
        service.update(dto);
        verify(mapper).map(dto, shelf);
        verify(repository).save(shelf);
        when(repository.findByIdentifier("X")).thenReturn(null);
        dto.setIdentifier("X");
        ShelfDto fail = service.update(dto);
        assertFalse(fail.isSuccess());
        assertTrue(fail.getMessage().contains("not found"));
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete("S1");
        verify(repository).deleteByIdentifier("S1");
    }

    @Test
    void findAll_shouldHandleDataAndEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<ShelfDto>>() {}.getType();
        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Page<Shelf> page =
                new PageImpl<>(List.of(shelf), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));
        assertEquals(1, service.findAll(pageable).size());
        Page<Shelf> empty =
                new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(empty);
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findAll(pageable).isEmpty());
    }

    @Test
    void toggleAndFindAllActive_shouldCoverAllCases() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");
        shelf.setStatus(true);
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        when(repository.findByIdentifier("S1")).thenReturn(shelf);
        service.toggleStatus("S1");
        assertFalse(shelf.isStatus());
        verify(repository).save(shelf);
        when(repository.findByIdentifier("X")).thenReturn(null);
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.toggleStatus("X")
        );
        assertEquals("Shelf not found", ex.getMessage());
        when(repository.findByStatusTrue()).thenReturn(List.of(shelf));
        when(mapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        List<ShelfDto> result = service.findAllActive();
        assertEquals(1, result.size());
        when(repository.findByStatusTrue()).thenReturn(List.of());
        assertTrue(service.findAllActive().isEmpty());
    }
}