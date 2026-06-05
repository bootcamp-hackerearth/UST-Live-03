package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UnitServiceImpl unitService;

    @Test
    void testSave_NewUnit() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Unit unit = new Unit();
        when(unitRepository.findByIdentifier("U1")).thenReturn(null);
        when(modelMapper.map(dto, Unit.class)).thenReturn(unit);
        UnitDto result = unitService.save(dto);
        assertTrue(result.isSuccess());
        verify(unitRepository).save(unit);
    }

    @Test
    void testSave_DuplicateUnit() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        when(unitRepository.findByIdentifier("U1")).thenReturn(new Unit());
        UnitDto result = unitService.save(dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(unitRepository, never()).save(any());
    }

    @Test
    void findAllWithPageableTest() {
        Unit unit = new Unit();
        unit.setIdentifier("kg");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("kg");
        List<Unit> units = List.of(unit);
        List<UnitDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Unit> unitPage = new PageImpl<>(units);
        Mockito.when(unitRepository.findAll(pageable)).thenReturn(unitPage);
        Mockito.when(modelMapper.map(Mockito.eq(units), Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<UnitDto> response = unitService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("kg", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Unit unit = new Unit();
        unit.setIdentifier("kg");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("kg");
        List<Unit> units = List.of(unit);
        List<UnitDto> dtos = List.of(dto);
        Mockito.when(unitRepository.findAll()).thenReturn(units);
        Mockito.when(modelMapper.map(Mockito.eq(units), Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<UnitDto> response = unitService.findAll(null);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void testFindByIdentifier_Found() {
        Unit unit = new Unit();
        UnitDto dto = new UnitDto();
        when(unitRepository.findByIdentifier("U1")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        UnitDto result = unitService.findByIdentifier("U1");
        assertNotNull(result);
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(unitRepository.findByIdentifier("U1")).thenReturn(null);
        UnitDto result = unitService.findByIdentifier("U1");
        assertNull(result);
    }

    @Test
    void testDelete() {
        unitService.delete("U1");
        verify(unitRepository).deleteByIdentifier("U1");
    }

    @Test
    void testUpdate_Found() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Unit existing = new Unit();
        when(unitRepository.findByIdentifier("U1")).thenReturn(existing);
        UnitDto result = unitService.update(dto);
        assertTrue(result.isSuccess());
        verify(modelMapper).map(dto, existing);
        verify(unitRepository).save(existing);
    }

    @Test
    void testUpdate_NotFound() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        when(unitRepository.findByIdentifier("U1")).thenReturn(null);
        UnitDto result = unitService.update(dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void toggleStatusSuccessTest() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unit.setStatus(false);
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("KG");
        unitDto.setStatus(true);
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);
        UnitDto response = unitService.toggleStatus("KG", true);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("KG", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
        Mockito.verify(unitRepository).findByIdentifier("KG");
        Mockito.verify(unitRepository).save(unit);
        Mockito.verify(modelMapper).map(unit, UnitDto.class);
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(null);
        Mockito.when(modelMapper.map(null, UnitDto.class)).thenReturn(null);
        UnitDto response = unitService.toggleStatus("KG", true);
        Assertions.assertNull(response);
        Mockito.verify(unitRepository).findByIdentifier("KG");
        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }
}