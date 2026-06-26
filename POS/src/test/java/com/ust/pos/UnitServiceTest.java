package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
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

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UnitServiceImpl unitService;

    @Test
    void findByIdentifierTest() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT-01");
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT-01");

        Mockito.when(unitRepository.findByIdentifier("UNIT-01")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto response = unitService.findByIdentifier("UNIT-01");

        Assertions.assertEquals("UNIT-01", response.getIdentifier());
    }

    @Test
    void toggleTestActive() {
        Unit unit = new Unit();
        unit.setStatus(false);
        UnitDto unitDto = new UnitDto();
        unitDto.setStatus(true);

        Mockito.when(unitRepository.findByIdentifier("UNIT-01")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto response = unitService.toggleStatus("UNIT-01");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleTestInactive() {
        Unit unit = new Unit();
        unit.setStatus(true);
        UnitDto unitDto = new UnitDto();
        unitDto.setStatus(false);

        Mockito.when(unitRepository.findByIdentifier("UNIT-01")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto response = unitService.toggleStatus("UNIT-01");

        Assertions.assertFalse(response.isStatus());
    }

    @Test
    void saveTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT-01");

        Mockito.when(unitRepository.findByIdentifier("UNIT-01")).thenReturn(null);
        Unit unit = new Unit();
        Mockito.when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("UNIT-01", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT-01");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("UNIT-01");
        existingUnit.setDeleted(false);

        Mockito.when(unitRepository.findByIdentifier("UNIT-01")).thenReturn(existingUnit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT-01");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("UNIT-01");
        existingUnit.setDeleted(true);

        Mockito.when(unitRepository.findByIdentifier("UNIT-01")).thenReturn(existingUnit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT-01");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("UNIT-01");

        Mockito.when(unitRepository.findByIdentifier("UNIT-01")).thenReturn(existingUnit);
        Mockito.when(unitRepository.save(existingUnit)).thenReturn(existingUnit);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT-01");

        Mockito.when(unitRepository.findByIdentifier("UNIT-01")).thenReturn(null);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT-01");

        Mockito.when(unitRepository.findByIdentifier("UNIT-01")).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);

        boolean response = unitService.delete("UNIT-01");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(unitRepository.findByIdentifier("UNIT-01")).thenReturn(null);

        boolean response = unitService.delete("UNIT-01");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Unit unit = new Unit();
        List<Unit> unitList = List.of(unit);
        Page<Unit> unitPage = new PageImpl<>(unitList, pageable, unitList.size());

        UnitDto unitDto = new UnitDto();
        List<UnitDto> unitDtos = List.of(unitDto);

        Mockito.when(unitRepository.findByDeletedFalse(pageable)).thenReturn(unitPage);
        Mockito.when(modelMapper.map(Mockito.eq(unitList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(unitDtos);

        WsDto<UnitDto> response = unitService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void findByStatusTest() {
        Unit unit = new Unit();
        List<Unit> unitList = List.of(unit);
        UnitDto unitDto = new UnitDto();
        List<UnitDto> unitDtos = List.of(unitDto);

        Mockito.when(unitRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(unitList);
        Mockito.when(modelMapper.map(Mockito.eq(unitList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(unitDtos);

        List<UnitDto> response = unitService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }
}