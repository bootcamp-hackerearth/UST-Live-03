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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
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
    void findByIdentifier_Found() {

        Unit unit = new Unit();
        unit.setIdentifier("U1");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        when(unitRepository.findByIdentifier("U1"))
                .thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        UnitDto result = unitService.findByIdentifier("U1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("U1", result.getIdentifier());
    }

    @Test
    void save_NewUnit() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Unit unit = new Unit();

        when(unitRepository.findByIdentifier("U1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Unit.class))
                .thenReturn(unit);

        when(unitRepository.save(unit))
                .thenReturn(unit);

        UnitDto result = unitService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("U1", result.getIdentifier());
        verify(unitRepository).save(unit);
    }

    @Test
    void save_UnitExists() {

        Unit existing = new Unit();

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        when(unitRepository.findByIdentifier("U1"))
                .thenReturn(existing);

        UnitDto result = unitService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(unitRepository, never()).save(any());
    }

    @Test
    void update_UnitExists() {

        Unit existing = new Unit();

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        when(unitRepository.findByIdentifier("U1"))
                .thenReturn(existing);

        when(unitRepository.save(existing))
                .thenReturn(existing);

        UnitDto result = unitService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("U1", result.getIdentifier());
        verify(modelMapper).map(dto, existing);
        verify(unitRepository).save(existing);
    }

    @Test
    void update_UnitNotFound() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        when(unitRepository.findByIdentifier("U1"))
                .thenReturn(null);

        UnitDto result = unitService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(unitRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        Unit unit = new Unit();

        when(unitRepository.findByIdentifier("U1"))
                .thenReturn(unit);

        unitService.delete("U1");

        verify(unitRepository).findByIdentifier("U1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Unit unit1 = new Unit();
        Unit unit2 = new Unit();

        Page<Unit> page = new PageImpl<>(
                List.of(unit1, unit2),
                pageable,
                2
        );

        List<UnitDto> dtoList =
                List.of(new UnitDto(), new UnitDto());

        when(unitRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<UnitDto> result =
                unitService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2,
                result.getContent().size());
        Assertions.assertEquals(0,
                result.getPage());
        Assertions.assertEquals(10,
                result.getSizePerPage());
        Assertions.assertEquals(1,
                result.getTotalPages());
        Assertions.assertEquals(2,
                result.getTotalRecords());

        verify(unitRepository)
                .findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Unit> page =
                new PageImpl<>(List.of(), pageable, 0);

        when(unitRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<UnitDto> result =
                unitService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(
                result.getContent().isEmpty());
        Assertions.assertEquals(0,
                result.getTotalRecords());

        verify(unitRepository)
                .findByIsDeletedFalse(pageable);
    }

    @Test
    void toggleStatus_Test() {

        Unit unit = new Unit();
        unit.setStatus(true);

        when(unitRepository.findByIdentifier("U1"))
                .thenReturn(unit);

        unitService.toggleStatus("U1");

        Assertions.assertFalse(unit.isStatus());
        verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatus_NotFound() {

        when(unitRepository.findByIdentifier("U1"))
                .thenReturn(null);

        unitService.toggleStatus("U1");

        verify(unitRepository, never()).save(any());
    }
}