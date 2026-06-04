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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @InjectMocks
    private UnitServiceImpl unitService;

    @Mock
    private UnitRepository unitRepository;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit unit = new Unit();
        unit.setIdentifier("KG");

        when(unitRepository.findByIdentifier("KG")).thenReturn(null);
        when(modelMapper.map(dto, Unit.class)).thenReturn(unit);

        UnitDto response = unitService.save(dto);

        assertEquals("KG", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        verify(unitRepository).save(unit);
    }

    @Test
    void saveFailureTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit unit = new Unit();
        unit.setIdentifier("KG");

        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);

        UnitDto response = unitService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(unitRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit unit = new Unit();
        unit.setIdentifier("KG");

        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);

        UnitDto response = unitService.update(dto);

        assertEquals("KG", response.getIdentifier());
        verify(modelMapper).map(dto, unit);
        verify(unitRepository).save(unit);
    }

    @Test
    void updateFailureTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        when(unitRepository.findByIdentifier("KG")).thenReturn(null);

        UnitDto response = unitService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(unitRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        unitService.delete("KG");
        verify(unitRepository).deleteByIdentifier("KG");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);

        UnitDto response = unitService.findByIdentifier("KG");

        assertNotNull(response);
        assertEquals("KG", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(unitRepository.findByIdentifier("KG")).thenReturn(null);

        UnitDto response = unitService.findByIdentifier("KG");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Unit> units = List.of(new Unit(), new Unit());
        Page<Unit> page = new PageImpl<>(units);

        List<UnitDto> dtoList = List.of(new UnitDto(), new UnitDto());

        when(unitRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(units),
                any(java.lang.reflect.Type.class)
        )).thenReturn(dtoList);

        List<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(unitRepository).findAll(pageable);
    }

    @Test
    void toggleStatusSuccessTest() {
        Unit unit = new Unit();
        unit.setStatus(true);

        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);

        unitService.toggleStatus("KG");

        Assertions.assertFalse(unit.isStatus());
        verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusFailureTest() {
        when(unitRepository.findByIdentifier("KG")).thenReturn(null);

        unitService.toggleStatus("KG");

        verify(unitRepository, never()).save(any());
    }
}