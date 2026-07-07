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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

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
    void findByIdentifierTest() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        when(unitRepository.findByIdentifierAndIsDeletedFalse("KG"))
                .thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        UnitDto result = unitService.findByIdentifier("KG");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("KG", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(unitRepository.findByIdentifierAndIsDeletedFalse("KG"))
                .thenReturn(null);

        ResourceNotFoundException exception =
                Assertions.assertThrows(
                        ResourceNotFoundException.class,
                        () -> unitService.findByIdentifier("KG"));

        Assertions.assertEquals(
                "Unit with identifier 'KG' not found",
                exception.getMessage());
    }

    @Test
    void saveSuccessTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit unit = new Unit();

        when(unitRepository.findByIdentifier("KG"))
                .thenReturn(null);

        when(modelMapper.map(dto, Unit.class))
                .thenReturn(unit);

        UnitDto result = unitService.save(dto);

        Assertions.assertEquals("KG", result.getIdentifier());

        verify(unitRepository).save(unit);
    }

    @Test
    void saveAlreadyExistsTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit existingUnit = new Unit();
        existingUnit.setDeleted(false);

        when(unitRepository.findByIdentifier("KG"))
                .thenReturn(existingUnit);

        UnitDto result = unitService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Unit with identifier - KG already exists",
                result.getMessage()
        );

        verify(unitRepository, never()).save(any());
    }

    @Test
    void saveDeletedUnitTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit existingUnit = new Unit();
        existingUnit.setDeleted(true);

        when(unitRepository.findByIdentifier("KG"))
                .thenReturn(existingUnit);

        UnitDto result = unitService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Unit with identifier - KG was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(unitRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit existingUnit = new Unit();

        when(unitRepository.findByIdentifier("KG"))
                .thenReturn(existingUnit);

        UnitDto result = unitService.update(dto);

        Assertions.assertEquals("KG", result.getIdentifier());

        verify(modelMapper).map(dto, existingUnit);
        verify(unitRepository).save(existingUnit);
    }

    @Test
    void updateFailureTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        when(unitRepository.findByIdentifier("KG"))
                .thenReturn(null);

        UnitDto result = unitService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Unit with identifier - KG not found",
                result.getMessage()
        );

        verify(unitRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Unit unit = new Unit();

        when(unitRepository.findByIdentifier("KG"))
                .thenReturn(unit);

        unitService.delete("KG");

        verify(unitRepository).findByIdentifier("KG");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Unit> units = List.of(
                new Unit(),
                new Unit()
        );

        Page<Unit> page = new PageImpl<>(units, pageable, 2);

        List<UnitDto> dtoList = List.of(
                new UnitDto(),
                new UnitDto()
        );

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        when(unitRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(units, listType))
                .thenReturn(dtoList);

        WsDto<UnitDto> result = unitService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Unit unit = new Unit();
        unit.setStatus(false);

        when(unitRepository.findByIdentifier("KG"))
                .thenReturn(unit);

        unitService.toggleStatus("KG");

        Assertions.assertTrue(unit.getStatus());

        verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Unit unit = new Unit();
        unit.setStatus(true);

        when(unitRepository.findByIdentifier("KG"))
                .thenReturn(unit);

        unitService.toggleStatus("KG");

        Assertions.assertFalse(unit.getStatus());

        verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusNullToTrueTest() {
        Unit unit = new Unit();
        unit.setStatus(null);

        when(unitRepository.findByIdentifier("KG"))
                .thenReturn(unit);

        unitService.toggleStatus("KG");

        Assertions.assertTrue(unit.getStatus());

        verify(unitRepository).save(unit);
    }
}