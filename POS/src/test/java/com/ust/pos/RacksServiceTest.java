package com.ust.pos;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.modell.Racks;
import com.ust.pos.modell.RacksRepository;
import com.ust.pos.racks.service.impl.RacksServiceImpl;
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
class RacksServiceTest {

    @InjectMocks
    private RacksServiceImpl service;

    @Mock
    private RacksRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifier_shouldHandleBothCases() {
        Racks racks = new Racks();
        racks.setIdentifier("R1");
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        when(repository.findByIdentifier("R1")).thenReturn(racks);
        when(mapper.map(racks, RacksDto.class)).thenReturn(dto);
        assertEquals("R1",
                service.findByIdentifier("R1").getIdentifier());
        when(repository.findByIdentifier("X")).thenReturn(null);
        when(mapper.map(null, RacksDto.class)).thenReturn(null);
        assertNull(service.findByIdentifier("X"));
    }

    @Test
    void save_shouldHandleAllCases() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        Racks racks = new Racks();
        racks.setIdentifier("R1");
        when(repository.findByIdentifier("R1")).thenReturn(null);
        when(mapper.map(dto, Racks.class)).thenReturn(racks);
        RacksDto result = service.save(dto);
        verify(mapper).map(dto, Racks.class);
        verify(repository).save(racks);
        assertEquals("R1", result.getIdentifier());
        when(repository.findByIdentifier("R1")).thenReturn(racks);
        RacksDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void update_shouldHandleBothCases() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        Racks racks = new Racks();
        racks.setIdentifier("R1");
        when(repository.findByIdentifier("R1")).thenReturn(racks);
        service.update(dto);
        verify(mapper).map(dto, racks);
        verify(repository).save(racks);
        when(repository.findByIdentifier("X")).thenReturn(null);
        dto.setIdentifier("X");
        RacksDto fail = service.update(dto);
        assertFalse(fail.isSuccess());
        assertTrue(fail.getMessage().contains("not found"));
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete("R1");
        verify(repository).deleteByIdentifier("R1");
    }

    @Test
    void findAll_shouldHandleDataAndEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<RacksDto>>() {}.getType();
        Racks racks = new Racks();
        racks.setIdentifier("R1");
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        Page<Racks> page =
                new PageImpl<>(List.of(racks), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));
        assertEquals(1, service.findAll(pageable).size());
        Page<Racks> empty =
                new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(empty);
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findAll(pageable).isEmpty());
    }

    @Test
    void toggleAndFindAllActive_shouldHandleAllCases() {
        Racks racks = new Racks();
        racks.setIdentifier("R1");
        racks.setStatus(true);
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        when(repository.findByIdentifier("R1")).thenReturn(racks);
        service.toggleStatus("R1");
        assertFalse(racks.isStatus());
        verify(repository).save(racks);
        when(repository.findByIdentifier("X")).thenReturn(null);
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.toggleStatus("X")
        );
        assertEquals("Shelf not found", ex.getMessage());
        when(repository.findByStatusTrue()).thenReturn(List.of(racks));
        when(mapper.map(racks, RacksDto.class)).thenReturn(dto);
        List<RacksDto> result = service.findAllActive();
        assertEquals(1, result.size());
        when(repository.findByStatusTrue()).thenReturn(List.of());
        assertTrue(service.findAllActive().isEmpty());
    }
}
