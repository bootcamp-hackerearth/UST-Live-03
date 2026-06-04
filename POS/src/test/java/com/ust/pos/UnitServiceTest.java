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

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @InjectMocks
    private UnitServiceImpl unitServiceImpl;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Unit unit = new Unit();
        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);
        UnitDto response = unitServiceImpl.save(unitDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Unit unit = new Unit();
        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);
        UnitDto response = unitServiceImpl.save(unitDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        assertFalse(response.isSuccess());

    }

    @Test
    void findByIdentifierTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto response = unitServiceImpl.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());

    }

    @Test
    void updateTest() {

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(existingUnit);
        Mockito.when(unitRepository.save(existingUnit)).thenReturn(existingUnit);

        UnitDto response = unitServiceImpl.update(unitDto);
        assertTrue(response.isSuccess());

    }

    @Test
    void updateTestFailure() {

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(null);

        UnitDto response = unitServiceImpl.update(unitDto);
        assertFalse(response.isSuccess());

    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(unitRepository).deleteByIdentifier("Admin");

        unitServiceImpl.delete("Admin");
        Mockito.verify(unitRepository).deleteByIdentifier("Admin");

    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable = PageRequest.of(0, 5);

        Unit unit = new Unit();
        unit.setIdentifier("U1");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("U1");

        List<Unit> units = List.of(unit);
        List<UnitDto> unitDtos = List.of(unitDto);

        Page<Unit> unitPage = new PageImpl<>(units, pageable, units.size());

        Mockito.when(unitRepository.findAll(pageable)).thenReturn(unitPage);

        Mockito.when(modelMapper.map(Mockito.eq(unitPage.getContent()), Mockito.any(java.lang.reflect.Type.class))).thenReturn(unitDtos);

        List<UnitDto> response = unitServiceImpl.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("U1", response.get(0).getIdentifier());

    }

    @Test
    void toggleStatusTest_TrueToFalse() {

        Unit unit = new Unit();
        unit.setIdentifier("M1");
        unit.setStatus(true);

        Mockito.when(unitRepository.findByIdentifier("M1")).thenReturn(unit);

        unitServiceImpl.toggleStatus("M1");
        assertFalse(unit.isStatus()); // toggled
        Mockito.verify(unitRepository).save(unit);

    }

    @Test
    void toggleStatusTest_FalseToTrue() {

        Unit unit = new Unit();
        unit.setIdentifier("M1");
        unit.setStatus(false);

        Mockito.when(unitRepository.findByIdentifier("M1")).thenReturn(unit);

        unitServiceImpl.toggleStatus("M1");

        assertTrue(unit.isStatus());
        Mockito.verify(unitRepository).save(unit);

    }

    @Test
    void toggleStatusTest_NotFound() {

        Mockito.when(unitRepository.findByIdentifier("M1")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> unitServiceImpl.toggleStatus("M1"));
        assertEquals("Unit not found", exception.getMessage());

    }

    @Test
    void findActiveUnitTest() {

        Unit model = new Unit();
        model.setIdentifier("M1");
        model.setStatus(true);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("M1");

        List<Unit> unitList = List.of(model);
        List<UnitDto> dtoList = List.of(dto);

        Mockito.when(unitRepository.findByStatusTrue()).thenReturn(unitList);
        Mockito.when(modelMapper.map(Mockito.eq(unitList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(dtoList);

        List<UnitDto> response = unitServiceImpl.findActiveUnits();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("M1", response.get(0).getIdentifier());
        Mockito.verify(unitRepository).findByStatusTrue();

    }

}