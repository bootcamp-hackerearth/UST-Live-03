package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Unit;
import com.ust.pos.modell.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
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
class UnitServiceTest {

    @InjectMocks
    private UnitServiceImpl service;

    @Mock
    private UnitRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifierTest() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        when(repository.findByIdentifier("KG")).thenReturn(unit);
        when(mapper.map(unit, UnitDto.class)).thenReturn(dto);

        assertEquals("KG", service.findByIdentifier("KG").getIdentifier());

        when(repository.findByIdentifier("X")).thenReturn(null);
        when(mapper.map(null, UnitDto.class)).thenReturn(null);

        assertNull(service.findByIdentifier("X"));
    }

    @Test
    void saveTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        Unit unit = new Unit();
        unit.setIdentifier("KG");

        when(repository.findByIdentifier("KG")).thenReturn(null);
        when(mapper.map(dto, Unit.class)).thenReturn(unit);

        UnitDto result = service.save(dto);
        verify(repository).save(unit);
        assertTrue(result.getMessage() == null || result.isSuccess());

        when(repository.findByIdentifier("KG")).thenReturn(unit);

        UnitDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void updateTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        Unit unit = new Unit();
        unit.setIdentifier("KG");

        when(repository.findByIdentifier("KG")).thenReturn(unit);

        service.update(dto);
        verify(mapper).map(dto, unit);
        verify(repository).save(unit);

        when(repository.findByIdentifier("X")).thenReturn(null);
        dto.setIdentifier("X");

        UnitDto failure = service.update(dto);
        assertFalse(failure.isSuccess());
        assertTrue(failure.getMessage().contains("not found"));
    }

    @Test
    void deleteTest() {
        service.delete("KG");
        verify(repository).deleteByIdentifier("KG");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<UnitDto>>(){}.getType();

        Unit unit = new Unit();
        unit.setIdentifier("KG");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Page<Unit> page = new PageImpl<>(List.of(unit), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(page.getContent(), type)).thenReturn(List.of(dto));

        WsDto<UnitDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());

        Page<Unit> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(emptyPage.getContent(), type)).thenReturn(List.of());

        WsDto<UnitDto> empty = service.findAll(pageable);
        assertTrue(empty.getDtoList().isEmpty());
    }

    @Test
    void toggleStatusTest() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unit.setStatus(true);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        when(repository.findByIdentifier("KG")).thenReturn(unit);

        when(repository.save(any(Unit.class))).thenReturn(unit);

        when(mapper.map(any(Unit.class), eq(UnitDto.class))).thenReturn(dto);

        service.toggleStatus("KG");
        assertFalse(unit.getStatus());

        service.toggleStatus("KG");
        assertTrue(unit.getStatus());

        verify(repository, times(2)).save(unit);

        when(repository.findByIdentifier("X")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.toggleStatus("X"));

        assertTrue(ex.getMessage().contains("Unit not found with identifier"));
    }
}