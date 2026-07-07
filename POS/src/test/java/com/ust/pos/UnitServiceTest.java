package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    private static final String IDENTIFIER = "KG";
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private UnitServiceImpl unitService;

    @Test
    void findByIdentifierSuccess() {

        Unit unit = new Unit();
        unit.setIdentifier(IDENTIFIER);

        UnitDto dto = new UnitDto();

        when(unitRepository.findByIdentifier(IDENTIFIER)).thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);

        UnitDto result = unitService.findByIdentifier(IDENTIFIER);

        assertNotNull(result);

    }

    @Test
    void findByIdentifierNotFound() {

        when(unitRepository.findByIdentifier(IDENTIFIER)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> unitService.findByIdentifier(IDENTIFIER));

    }

    @Test
    void toggleStatusTrueToFalse() {

        Unit unit = new Unit();

        unit.setStatus(true);

        when(unitRepository.findByIdentifier(IDENTIFIER)).thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class)).thenReturn(new UnitDto());

        unitService.toggleStatus(IDENTIFIER);

        assertFalse(unit.isStatus());

        verify(unitRepository).save(unit);

    }

    @Test
    void toggleStatusFalseToTrue() {

        Unit unit = new Unit();

        unit.setStatus(false);

        when(unitRepository.findByIdentifier(IDENTIFIER)).thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class)).thenReturn(new UnitDto());

        unitService.toggleStatus(IDENTIFIER);

        assertTrue(unit.isStatus());

    }

    @Test
    void saveSuccess() {

        UnitDto dto = new UnitDto();

        dto.setIdentifier(IDENTIFIER);

        Unit unit = new Unit();

        when(unitRepository.findByIdentifier(IDENTIFIER)).thenReturn(null);

        when(modelMapper.map(dto, Unit.class)).thenReturn(unit);

        UnitDto result = unitService.save(dto);

        verify(unitRepository).save(unit);

        assertEquals(IDENTIFIER, result.getIdentifier());

    }

    @Test
    void saveAlreadyExists() {

        Unit existing = new Unit();

        existing.setIdentifier(IDENTIFIER);

        UnitDto dto = new UnitDto();

        dto.setIdentifier(IDENTIFIER);

        when(unitRepository.findByIdentifier(IDENTIFIER)).thenReturn(existing);

        UnitDto result = unitService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Unit with identifier - KG already exists", result.getMessage());

    }

    @Test
    void saveSoftDeleted() {

        Unit existing = new Unit();

        existing.setDeleted(true);

        UnitDto dto = new UnitDto();

        dto.setIdentifier(IDENTIFIER);

        when(unitRepository.findByIdentifier(IDENTIFIER)).thenReturn(existing);

        UnitDto result = unitService.save(dto);

        assertFalse(result.isSuccess());

        assertTrue(result.getMessage().contains("soft deleted"));

    }

    @Test
    void updateSuccess() {

        Unit existing = new Unit();

        existing.setIdentifier(IDENTIFIER);

        UnitDto dto = new UnitDto();

        dto.setIdentifier(IDENTIFIER);

        when(unitRepository.findByIdentifier(IDENTIFIER)).thenReturn(existing);

        UnitDto result = unitService.update(dto);

        verify(modelMapper).map(dto, existing);

        verify(unitRepository).save(existing);

        assertEquals(dto, result);

    }

    @Test
    void updateNotFound() {

        UnitDto dto = new UnitDto();

        dto.setIdentifier(IDENTIFIER);

        when(unitRepository.findByIdentifier(IDENTIFIER)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> unitService.update(dto));

    }

    @Test
    void deleteSuccess() {

        Unit unit = new Unit();

        when(unitRepository.findByIdentifier(IDENTIFIER)).thenReturn(unit);

        boolean result = unitService.delete(IDENTIFIER);

        assertTrue(result);

        verify(unitRepository).save(unit);

    }

    @Test
    void deleteNotFound() {

        when(unitRepository.findByIdentifier(IDENTIFIER)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> unitService.delete(IDENTIFIER));

    }

    @Test
    void findAllSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Unit> entities = List.of(new Unit());

        List<UnitDto> dtos = List.of(new UnitDto());

        Page<Unit> page = new PageImpl<>(entities, pageable, 1);

        when(unitRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtos);

        WsDto<UnitDto> result = unitService.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

        assertEquals(1, result.getTotalRecords());

    }

    @Test
    void findAllEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Unit> page = new PageImpl<>(Collections.emptyList());

        when(unitRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        WsDto<UnitDto> result = unitService.findAll(pageable);

        assertEquals(0, result.getDtoList().size());

    }

    @Test
    void findAllSpecificationSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Unit> spec = mock(Specification.class);

        List<Unit> entities = List.of(new Unit());

        List<UnitDto> dtos = List.of(new UnitDto());

        Page<Unit> page = new PageImpl<>(entities);

        when(unitRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtos);

        WsDto<UnitDto> result = unitService.findAll(spec, pageable, "kg");

        assertEquals("kg", result.getKeyword());

        assertEquals(1, result.getDtoList().size());

    }

    @Test
    void findAllSpecificationEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Unit> spec = mock(Specification.class);

        Page<Unit> page = new PageImpl<>(Collections.emptyList());

        when(unitRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        WsDto<UnitDto> result = unitService.findAll(spec, pageable, "abc");

        assertEquals("abc", result.getKeyword());

        assertEquals(0, result.getDtoList().size());

    }

    @Test
    void findIfTrueSuccess() {

        List<Unit> units = List.of(new Unit());

        List<UnitDto> dtos = List.of(new UnitDto());

        when(unitRepository.findByStatusIsTrue()).thenReturn(units);

        when(modelMapper.map(eq(units), any(Type.class))).thenReturn(dtos);

        List<UnitDto> result = unitService.findIfTrue();

        assertEquals(1, result.size());

    }

    @Test
    void findIfTrueEmpty() {

        when(unitRepository.findByStatusIsTrue()).thenReturn(Collections.emptyList());

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        List<UnitDto> result = unitService.findIfTrue();

        assertTrue(result.isEmpty());

    }

}