package com.ust.pos;

import com.ust.pos.dto.UnitDto;
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
    void findByIdentifier_shouldHandleBothCases() {
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
    void save_shouldHandleAllCases() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        when(repository.findByIdentifier("KG")).thenReturn(null);
        when(mapper.map(dto, Unit.class)).thenReturn(unit);
        UnitDto result = service.save(dto);
        verify(mapper).map(dto, Unit.class);
        verify(repository).save(unit);
        assertTrue(result.isSuccess() || result.getMessage() == null);
        when(repository.findByIdentifier("KG")).thenReturn(unit);
        UnitDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void update_shouldHandleBothCases() {
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
    void delete_shouldCallRepository() {
        service.delete("KG");
        verify(repository).deleteByIdentifier("KG");
    }

    @Test
    void findAll_shouldHandleDataAndEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<UnitDto>>() {}.getType();
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        Page<Unit> page = new PageImpl<>(List.of(unit), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));
        assertEquals(1, service.findAll(pageable).size());
        Page<Unit> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findAll(pageable).isEmpty());
    }

    @Test
    void toggleStatus_shouldCoverAllBranches() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unit.setStatus(true);
        when(repository.findByIdentifier("KG")).thenReturn(unit);
        service.toggleStatus("KG");
        assertFalse(unit.isStatus());
        service.toggleStatus("KG");
        assertTrue(unit.isStatus());
        verify(repository, times(2)).save(unit);
        when(repository.findByIdentifier("X")).thenReturn(null);
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.toggleStatus("X")
        );
        assertEquals("Unit not found", ex.getMessage());
    }
}
