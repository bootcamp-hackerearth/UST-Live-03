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

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @InjectMocks
    private UnitServiceImpl unitService;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTestSuccess() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("Admin");

        Unit unit = new Unit();

        when(unitRepository.findByIdentifier("Admin")).thenReturn(null);
        when(modelMapper.map(dto, Unit.class)).thenReturn(unit);

        UnitDto response = unitService.save(dto);

        assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(unitRepository).save(unit);
    }

    @Test
    void saveTestFailure() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("Admin");

        Unit unit = new Unit();
        unit.setIdentifier("Admin");

        when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);

        UnitDto response = unitService.save(dto);

        assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());

        verify(unitRepository, Mockito.never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("Admin");

        Unit unit = new Unit();

        when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);

        UnitDto response = unitService.update(dto);

        assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(modelMapper).map(dto, unit);
        verify(unitRepository).save(unit);
    }

    @Test
    void updateTestFailure() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("Admin");

        when(unitRepository.findByIdentifier("Admin")).thenReturn(null);

        UnitDto response = unitService.update(dto);

        assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());

        verify(unitRepository, Mockito.never()).save(any());
    }

    @Test
    void deleteTest() {
        unitService.delete("Admin");
        verify(unitRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Unit unit = new Unit();
        unit.setIdentifier("Admin");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("Admin");

        when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);

        UnitDto result = unitService.findByIdentifier("Admin");

        assertNotNull(result);
        assertEquals("Admin", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(unitRepository.findByIdentifier("Admin")).thenReturn(null);

        UnitDto result = unitService.findByIdentifier("Admin");

        Assertions.assertNull(result);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Unit> units = List.of(new Unit(), new Unit());
        Page<Unit> page = new PageImpl<>(units);

        List<UnitDto> dtoList = List.of(new UnitDto(), new UnitDto());

        when(unitRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(eq(units), any(Type.class)))
                .thenReturn(dtoList);

        List<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(unitRepository).findAll(pageable);
        verify(modelMapper).map(eq(units), any(Type.class));
    }

    @Test
    void toggleStatusTest() {
        Unit unit = new Unit();
        unit.setIdentifier("Admin");
        unit.setStatus(true);

        when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);

        unitService.toggleStatus("Admin");

        Assertions.assertFalse(unit.isStatus());
        verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Unit unit = new Unit();
        unit.setIdentifier("Admin");
        unit.setStatus(false);

        when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);

        unitService.toggleStatus("Admin");

        Assertions.assertTrue(unit.isStatus());
        verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusNullTest() {
        when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        unitService.toggleStatus("Admin");

        verify(unitRepository, Mockito.never()).save(any());
    }

    @Test
    void findActiveUnitTest() {
        List<Unit> units = List.of(new Unit());

        when(unitRepository.findByStatus(true))
                .thenReturn(units);

        List<Unit> response = unitService.findActiveUnit();

        assertNotNull(response);
        assertEquals(1, response.size());

        verify(unitRepository).findByStatus(true);
    }
}