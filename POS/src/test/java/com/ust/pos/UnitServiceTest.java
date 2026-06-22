package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UnitServiceImpl unitService;

    private Unit unit;
    private UnitDto unitDto;

    @BeforeEach
    void setUp() {
        unit = new Unit();
        unit.setIdentifier("UNIT1");
        unit.setStatus(true);

        unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT1");
        unitDto.setStatus(true);
    }

    @Test
    void findByIdentifierTest() {
        Mockito.when(unitRepository.findByIdentifier("UNIT1")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.findByIdentifier("UNIT1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("UNIT1", result.getIdentifier());

        Mockito.verify(unitRepository).findByIdentifier("UNIT1");
        Mockito.verify(modelMapper).map(unit, UnitDto.class);
    }

    @Test
    void findByIdentifierNullTest() {
        Mockito.when(unitRepository.findByIdentifier("UNIT1")).thenReturn(null);
        Mockito.when(modelMapper.map(null, UnitDto.class)).thenReturn(null);

        UnitDto result = unitService.findByIdentifier("UNIT1");

        Assertions.assertNull(result);
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        unit.setStatus(true);

        Mockito.when(unitRepository.findByIdentifier("UNIT1")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.toggleStatus("UNIT1");

        Assertions.assertNotNull(result);
        Assertions.assertFalse(unit.isStatus());

        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        unit.setStatus(false);

        Mockito.when(unitRepository.findByIdentifier("UNIT1")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.toggleStatus("UNIT1");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(unit.isStatus());

        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void saveSuccessTest() {
        Mockito.when(unitRepository.findByIdentifier("UNIT1")).thenReturn(null);
        Mockito.when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("UNIT1", result.getIdentifier());

        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void saveAlreadyExistsTest() {
        Mockito.when(unitRepository.findByIdentifier("UNIT1")).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Unit with identifier - UNIT1 already exists", result.getMessage());

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveSoftDeletedTest() {
        unit.setDeleted(true);

        Mockito.when(unitRepository.findByIdentifier("UNIT1")).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Unit with identifier - UNIT1 has been soft deleted.(Rollback by changing status", result.getMessage());

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        Mockito.when(unitRepository.findByIdentifier("UNIT1")).thenReturn(unit);

        UnitDto result = unitService.update(unitDto);

        Assertions.assertNotNull(result);

        Mockito.verify(modelMapper).map(unitDto, unit);
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void updateNotFoundTest() {
        Mockito.when(unitRepository.findByIdentifier("UNIT1")).thenReturn(null);

        UnitDto result = unitService.update(unitDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Unit with identifier - UNIT1 not found", result.getMessage());

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Mockito.when(unitRepository.findByIdentifier("UNIT1")).thenReturn(unit);

        boolean result = unitService.delete("UNIT1");

        Assertions.assertTrue(result);

        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void deleteNotFoundTest() {
        Mockito.when(unitRepository.findByIdentifier("UNIT1")).thenReturn(null);

        boolean result = unitService.delete("UNIT1");

        Assertions.assertFalse(result);

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Unit> unitList = List.of(unit);
        Page<Unit> unitPage = new PageImpl<>(unitList);

        List<UnitDto> dtoList = List.of(unitDto);

        Mockito.when(unitRepository.findByDeletedFalse(pageable)).thenReturn(unitPage);
        Mockito.when(modelMapper.map(Mockito.eq(unitPage.getContent()), Mockito.any(Type.class))).thenReturn(dtoList);

        WsDto<UnitDto> result = unitService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1L, result.getTotalRecords());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Unit> unitPage = new PageImpl<>(List.of());

        Mockito.when(unitRepository.findByDeletedFalse(pageable)).thenReturn(unitPage);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());

        WsDto<UnitDto> result = unitService.findAll(pageable);

        Assertions.assertTrue(result.getDtoList().isEmpty());
        Assertions.assertEquals(0L, result.getTotalRecords());
    }

    @Test
    void findIfTrueTest() {
        List<Unit> unitList = List.of(unit);
        List<UnitDto> dtoList = List.of(unitDto);

        Mockito.when(unitRepository.findByStatusIsTrue()).thenReturn(unitList);
        Mockito.when(modelMapper.map(Mockito.eq(unitList), Mockito.any(Type.class))).thenReturn(dtoList);

        List<UnitDto> result = unitService.findIfTrue();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());

        Mockito.verify(unitRepository).findByStatusIsTrue();
    }

    @Test
    void findIfTrueEmptyTest() {
        Mockito.when(unitRepository.findByStatusIsTrue()).thenReturn(List.of());
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());

        List<UnitDto> result = unitService.findIfTrue();

        Assertions.assertTrue(result.isEmpty());
    }

}
