package com.ust.pos;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
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
    void findByIdentifierTest() {
        Racks racks = new Racks();
        racks.setIdentifier("R1");
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        when(repository.findByIdentifier("R1")).thenReturn(racks);
        when(mapper.map(racks, RacksDto.class)).thenReturn(dto);

        assertEquals("R1", service.findByIdentifier("R1").getIdentifier());

        when(repository.findByIdentifier("X")).thenReturn(null);
        when(mapper.map(null, RacksDto.class)).thenReturn(null);

        assertNull(service.findByIdentifier("X"));
    }

    @Test
    void saveTest() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        Racks racks = new Racks();
        racks.setIdentifier("R1");

        when(repository.findByIdentifier("R1")).thenReturn(null);
        when(mapper.map(dto, Racks.class)).thenReturn(racks);

        RacksDto result = service.save(dto);
        verify(repository).save(racks);
        assertEquals("R1", result.getIdentifier());

        when(repository.findByIdentifier("R1")).thenReturn(racks);

        RacksDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void updateTest() {
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
    void deleteTest() {
        service.delete("R1");
        verify(repository).deleteByIdentifier("R1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<RacksDto>>(){}.getType();

        Racks racks = new Racks();
        racks.setIdentifier("R1");
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Page<Racks> page = new PageImpl<>(List.of(racks), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(page.getContent(), type)).thenReturn(List.of(dto));

        WsDto<RacksDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());

        Page<Racks> empty = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(empty);
        when(mapper.map(empty.getContent(), type)).thenReturn(List.of());

        WsDto<RacksDto> emptyResult = service.findAll(pageable);
        assertTrue(emptyResult.getDtoList().isEmpty());
    }

    @Test
    void findAllActiveTest() {
        Racks racks = new Racks();
        racks.setIdentifier("R1");
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        when(repository.findByStatusTrue()).thenReturn(List.of(racks));
        when(mapper.map(racks, RacksDto.class)).thenReturn(dto);

        List<RacksDto> result = service.findAllActive();
        assertEquals(1, result.size());

        when(repository.findByStatusTrue()).thenReturn(List.of());
        assertTrue(service.findAllActive().isEmpty());
    }
}