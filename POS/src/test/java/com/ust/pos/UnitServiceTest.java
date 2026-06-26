package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
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
class UnitServiceTest {

    @InjectMocks
    private UnitServiceImpl service;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Unit> page = new PageImpl<>(List.of(new Unit()), pageable, 1);

        when(unitRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new UnitDto()));

        WsDto<UnitDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findByIdentifierTest() {
        Unit unit = new Unit();
        UnitDto dto = new UnitDto();

        when(unitRepository.findByIdentifier("U1")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);

        UnitDto result = service.findByIdentifier("U1");

        assertNotNull(result);
    }

    @Test
    void saveSuccessTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        when(unitRepository.findByIdentifier("U1")).thenReturn(null);
        when(modelMapper.map(dto, Unit.class)).thenReturn(new Unit());

        UnitDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(unitRepository).save(any());
    }

    @Test
    void saveDuplicateActiveTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Unit existing = new Unit();
        existing.setDeleted(false);

        when(unitRepository.findByIdentifier("U1")).thenReturn(existing);

        UnitDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(unitRepository, never()).save(any());
    }

    @Test
    void saveDuplicateDeletedTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Unit existing = new Unit();
        existing.setDeleted(true);

        when(unitRepository.findByIdentifier("U1")).thenReturn(existing);

        UnitDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void updateSuccessTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Unit existing = new Unit();

        when(unitRepository.findByIdentifier("U1")).thenReturn(existing);

        UnitDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(unitRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        when(unitRepository.findByIdentifier("U1")).thenReturn(null);

        UnitDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(unitRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Unit unit = new Unit();
        unit.setDeleted(false);

        when(unitRepository.findByIdentifier("U1")).thenReturn(unit);

        service.delete("U1");

        assertTrue(unit.isDeleted());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Unit unit = new Unit();
        unit.setStatus(true);

        when(unitRepository.findByIdentifier("U1")).thenReturn(unit);

        service.toggleStatus("U1");

        assertFalse(unit.isStatus());
        verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Unit unit = new Unit();
        unit.setStatus(false);

        when(unitRepository.findByIdentifier("U1")).thenReturn(unit);

        service.toggleStatus("U1");

        assertTrue(unit.isStatus());
        verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(unitRepository.findByIdentifier("U1")).thenReturn(null);

        service.toggleStatus("U1");

        verify(unitRepository, never()).save(any());
    }

    @Test
    void findActiveUnitTest() {
        when(unitRepository.findByStatus(true)).thenReturn(List.of(new Unit(), new Unit()));

        List<Unit> result = service.findActiveUnit();

        assertEquals(2, result.size());
    }
}