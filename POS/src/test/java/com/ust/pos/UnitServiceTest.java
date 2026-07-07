package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
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

    private Unit unitEntity;
    private UnitDto unitDto;

    @BeforeEach
    void setUp() {
        unitEntity = new Unit();
        unitEntity.setId(1L);
        unitEntity.setIdentifier("UNT-KG");
        unitEntity.setStatus(true);
        unitEntity.setDeleted(false);

        unitDto = new UnitDto();
        unitDto.setIdentifier("UNT-KG");
    }

    @Test
    void testFindByIdentifier() {
        when(unitRepository.findByIdentifier("UNT-KG")).thenReturn(unitEntity);

        UnitDto result = unitService.findByIdentifier("UNT-KG");

        assertNotNull(result);
        assertEquals("UNT-KG", result.getIdentifier());
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
    void testSave_WhenUnitExistsAndNotDeleted() {
        when(unitRepository.findByIdentifier("UNT-KG")).thenReturn(unitEntity);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenUnitWasPreviouslyDeleted() {
        unitEntity.setDeleted(true);
        when(unitRepository.findByIdentifier("UNT-KG")).thenReturn(unitEntity);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(unitRepository.findByIdentifier("UNT-KG")).thenReturn(null);
        when(unitRepository.save(any(Unit.class))).thenReturn(unitEntity);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Unit created successfully", result.getMessage());
    }

    @Test
    void testUpdate_WhenUnitNotFound() {
        when(unitRepository.findByIdentifier("UNT-KG")).thenReturn(null);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenUnitgetDeleted() {
        unitEntity.setDeleted(true);
        when(unitRepository.findByIdentifier("UNT-KG")).thenReturn(unitEntity);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        when(unitRepository.findByIdentifier("UNT-KG")).thenReturn(unitEntity);
        when(unitRepository.save(any(Unit.class))).thenReturn(unitEntity);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Unit updated successfully", result.getMessage());
    }

    @Test
    void testDelete_WhenUnitNotFound() {
        when(unitRepository.findByIdentifier("UNT-KG")).thenReturn(null);

        unitService.delete("UNT-KG");

        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void testDelete_Success() {
        when(unitRepository.findByIdentifier("UNT-KG")).thenReturn(unitEntity);
        when(unitRepository.save(any(Unit.class))).thenReturn(unitEntity);

        unitService.delete("UNT-KG");

        verify(unitRepository, times(1)).save(unitEntity);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Unit> entityList = Collections.singletonList(unitEntity);
        Page<Unit> page = new PageImpl<>(entityList, pageable, 1);

        when(unitRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getPage());
    }

    @Test
    void testToggleStatus_WhenUnitNotFound() {
        when(unitRepository.findByIdentifier("UNT-KG")).thenReturn(null);

        UnitDto result = unitService.toggleStatus("UNT-KG");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus_Success() {
        unitEntity.setStatus(true);
        when(unitRepository.findByIdentifier("UNT-KG")).thenReturn(unitEntity);
        when(unitRepository.save(any(Unit.class))).thenReturn(unitEntity);

        UnitDto result = unitService.toggleStatus("UNT-KG");

        assertNotNull(result);
        assertFalse(result.getStatus());
    }

    @Test
    void testFindIfTrue() {
        List<Unit> activeUnits = Collections.singletonList(unitEntity);
        when(unitRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(activeUnits);

        List<UnitDto> result = unitService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}