package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private UnitServiceImpl unitService;

    private Unit unit;
    private UnitDto unitDto;

    @BeforeEach
    void setUp() {
        unit = new Unit();
        unit.setId(1L);
        unit.setIdentifier("UNT-001");
        unit.setStatus(true);
        unit.setDeleted(false);

        unitDto = new UnitDto();
        unitDto.setIdentifier("UNT-001");
    }

    @Test
    void testFindByIdentifier_Success() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(unit);

        UnitDto result = unitService.findByIdentifier("UNT-001");

        assertNotNull(result);
        assertEquals("UNT-001", result.getIdentifier());
        verify(unitRepository, times(1)).findByIdentifier("UNT-001");
    }

    @Test
    void testFindByIdentifier_ThrowsResourceNotFoundException() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> unitService.findByIdentifier("UNT-001"));
        verify(unitRepository, times(1)).findByIdentifier("UNT-001");
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> unitService.save(null));
    }

    @Test
    void testSave_WhenIdentifierIsNull() {
        unitDto.setIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> unitService.save(unitDto));
    }

    @Test
    void testSave_WhenUnitAlreadyExistsAndNotDeleted() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void testSave_WhenUnitAlreadyExistsButDeleted() {
        unit.setDeleted(true);
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void testSave_Success() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(null);
        when(unitRepository.save(any(Unit.class))).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Unit created successfully", result.getMessage());
        verify(unitRepository, times(1)).save(any(Unit.class));
    }

    @Test
    void testUpdate_WhenUnitNotFound() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(null);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void testUpdate_WhenUnitDeleted() {
        unit.setDeleted(true);
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(unit);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void testUpdate_Success() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(unit);
        when(unitRepository.save(any(Unit.class))).thenReturn(unit);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Unit updated successfully", result.getMessage());
        verify(unitRepository, times(1)).save(any(Unit.class));
    }

    @Test
    void testDelete_WhenUnitNotFound() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(null);

        unitService.delete("UNT-001");

        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void testDelete_Success() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(unit);
        when(unitRepository.save(any(Unit.class))).thenReturn(unit);

        unitService.delete("UNT-001");

        verify(unitRepository, times(1)).save(any(Unit.class));
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Unit> page = new PageImpl<>(Collections.singletonList(unit), pageable, 1);
        when(unitRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());
        verify(unitRepository, times(1)).findByDeletedFalse(pageable);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Unit> page = new PageImpl<>(Collections.singletonList(unit), pageable, 1);
        Specification<Unit> spec = mock(Specification.class);
        when(unitRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        WsDto<UnitDto> result = unitService.findAll(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());
        verify(unitRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void testToggleStatus_WhenUnitNotFound() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(null);

        UnitDto result = unitService.toggleStatus("UNT-001");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void testToggleStatus_Success() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(unit);
        when(unitRepository.save(any(Unit.class))).thenReturn(unit);

        UnitDto result = unitService.toggleStatus("UNT-001");

        assertNotNull(result);
        assertFalse(result.isStatus());
        verify(unitRepository, times(1)).save(any(Unit.class));
    }

    @Test
    void testFindIfTrue() {
        when(unitRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(Collections.singletonList(unit));

        List<UnitDto> result = unitService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("UNT-001", result.get(0).getIdentifier());
        verify(unitRepository, times(1)).findByStatusIsTrueAndDeletedFalse();
    }
}