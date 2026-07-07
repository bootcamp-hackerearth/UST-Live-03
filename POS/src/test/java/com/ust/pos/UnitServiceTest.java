package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.models.Unit;
import com.ust.pos.models.UnitRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @InjectMocks
    private UnitServiceImpl unitService;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Unit entity = new Unit();
        when(unitRepository.findByIdentifier("U1")).thenReturn(null);
        when(modelMapper.map(dto, Unit.class)).thenReturn(entity);
        when(unitRepository.save(entity)).thenReturn(entity);
        UnitDto result = unitService.save(dto);
        assertTrue(result.isSuccess());
        assertEquals("U1", result.getIdentifier());
        verify(unitRepository).save(entity);
        Unit duplicate = new Unit();
        duplicate.setDeleted(false);
        when(unitRepository.findByIdentifier("U2")).thenReturn(duplicate);
        UnitDto duplicateDto = new UnitDto();
        duplicateDto.setIdentifier("U2");
        result = unitService.save(duplicateDto);
        assertFalse(result.isSuccess());
        Unit deleted = new Unit();
        deleted.setDeleted(true);
        when(unitRepository.findByIdentifier("U3")).thenReturn(deleted);
        UnitDto deletedDto = new UnitDto();
        deletedDto.setIdentifier("U3");
        result = unitService.save(deletedDto);
        assertFalse(result.isSuccess());
    }

    @Test
    void findByIdentifierUpdateAndDeleteTest() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        UnitDto result = unitService.findByIdentifier("U1");
        assertNotNull(result);
        assertEquals("U1", result.getIdentifier());
        result = unitService.update(dto);
        assertTrue(result.isSuccess());
        verify(modelMapper).map(dto, unit);
        verify(unitRepository).save(unit);
        unitService.delete("U1");
        assertTrue(unit.getDeleted());
        verify(unitRepository, atLeastOnce()).save(unit);
        when(unitRepository.findByIdentifierAndDeletedFalse("U2")).thenReturn(null);
        UnitDto updateFail = new UnitDto();
        updateFail.setIdentifier("U2");
        result = unitService.update(updateFail);
        assertFalse(result.isSuccess());
        when(unitRepository.findByIdentifierAndDeletedFalse("U3")).thenReturn(null);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> unitService.findByIdentifier("U3"));
        assertEquals("Unit with identifier 'U3' not found", ex.getMessage()
        );
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        Page<Unit> page = new PageImpl<>(List.of(unit), pageable, 1);
        when(unitRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new UnitDto()));
        WsDto<UnitDto> result = unitService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Unit> specification = mock(Specification.class);
        Page<Unit> page = new PageImpl<>(List.of(new Unit()), pageable, 1);
        when(unitRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new UnitDto()));
        WsDto<UnitDto> result = unitService.findAll(specification, pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        verify(unitRepository).findAll(specification, pageable);
    }

    @Test
    void toggleStatusTest() {
        when(unitRepository.save(any(Unit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Unit.class), eq(UnitDto.class))).thenReturn(new UnitDto());
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        unit.setStatus(true);
        when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(unit);
        unitService.toggleStatus("U1");
        assertFalse(unit.getStatus());
        unit.setStatus(false);
        when(unitRepository.findByIdentifierAndDeletedFalse("U2")).thenReturn(unit);
        unitService.toggleStatus("U2");
        assertTrue(unit.getStatus());
        unit.setStatus(null);
        when(unitRepository.findByIdentifierAndDeletedFalse("U3")).thenReturn(unit);
        unitService.toggleStatus("U3");
        assertTrue(unit.getStatus());
        when(unitRepository.findByIdentifierAndDeletedFalse("U4")).thenReturn(null);
        NullPointerException ex = assertThrows(NullPointerException.class, () -> unitService.toggleStatus("U4"));
        assertTrue(ex.getMessage().contains("Unit not found with identifier: U4"));
    }

    @Test
    void findAllActiveTest() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        when(unitRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(unit));
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        List<UnitDto> result = unitService.findAllActive();
        assertEquals(1, result.size());
        assertEquals("U1", result.get(0).getIdentifier());
        when(unitRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of());
        assertTrue(unitService.findAllActive().isEmpty());
    }
}