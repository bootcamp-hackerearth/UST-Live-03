package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
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
    void findByIdentifierTestSuccess() {
        Unit unit = new Unit();
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNT01");

        Mockito.when(unitRepository.findByIdentifier("UNT01")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto response = unitService.findByIdentifier("UNT01");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("UNT01", response.getIdentifier());
    }

    @Test
    void findByIdentifierTestNotFoundException() {
        Mockito.when(unitRepository.findByIdentifier("UNT01")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            unitService.findByIdentifier("UNT01");
        });
    }

    @Test
    void toggleStatusTest() {
        Unit unit = new Unit();
        unit.setStatus(false);
        UnitDto unitDto = new UnitDto();
        unitDto.setStatus(true);

        Mockito.when(unitRepository.findByIdentifier("UNT01")).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto response = unitService.toggleStatus("UNT01");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void saveTestSuccess() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNT01");

        Mockito.when(unitRepository.findByIdentifier("UNT01")).thenReturn(null);
        Unit unit = new Unit();
        Mockito.when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("UNT01", response.getIdentifier());
    }

    @Test
    void saveTestFailureAlreadyExists() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNT01");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("UNT01");
        existingUnit.setDeleted(false);

        Mockito.when(unitRepository.findByIdentifier("UNT01")).thenReturn(existingUnit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit with identifier - UNT01 already exists", response.getMessage());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNT01");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("UNT01");
        existingUnit.setDeleted(true);

        Mockito.when(unitRepository.findByIdentifier("UNT01")).thenReturn(existingUnit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit with identifier UNT01 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void updateTestSuccess() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNT01");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("UNT01");

        Mockito.when(unitRepository.findByIdentifier("UNT01")).thenReturn(existingUnit);
        Mockito.when(unitRepository.save(existingUnit)).thenReturn(existingUnit);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("UNT01", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNT01");

        Mockito.when(unitRepository.findByIdentifier("UNT01")).thenReturn(null);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit with identifier - UNT01 not found", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        Unit unit = new Unit();

        Mockito.when(unitRepository.findByIdentifier("UNT01")).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);

        boolean response = unitService.delete("UNT01");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(unitRepository.findByIdentifier("UNT01")).thenReturn(null);

        boolean response = unitService.delete("UNT01");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Unit unit = new Unit();
        List<Unit> unitList = List.of(unit);
        Page<Unit> unitPage = new PageImpl<>(unitList, pageable, unitList.size());

        UnitDto unitDto = new UnitDto();
        List<UnitDto> unitDtos = List.of(unitDto);

        Mockito.when(unitRepository.findByDeletedFalse(pageable)).thenReturn(unitPage);
        Mockito.when(modelMapper.map(Mockito.eq(unitList), Mockito.any(Type.class))).thenReturn(unitDtos);

        WsDto<UnitDto> response = unitService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findIfTrueTest() {
        Unit unit = new Unit();
        List<Unit> unitList = List.of(unit);
        UnitDto unitDto = new UnitDto();
        List<UnitDto> unitDtos = List.of(unitDto);

        Mockito.when(unitRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(unitList);
        Mockito.when(modelMapper.map(Mockito.eq(unitList), Mockito.any(Type.class))).thenReturn(unitDtos);

        List<UnitDto> response = unitService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Unit> specification = Mockito.mock(Specification.class);
        Unit unit = new Unit();
        List<Unit> unitList = List.of(unit);
        Page<Unit> page = new PageImpl<>(unitList, pageable, unitList.size());

        UnitDto unitDto = new UnitDto();
        List<UnitDto> unitDtos = List.of(unitDto);

        Mockito.when(unitRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(unitList), Mockito.any(Type.class))).thenReturn(unitDtos);

        WsDto<UnitDto> response = unitService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}