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
    void findByIdentifier_Found() {

        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");

        when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        UnitDto result =
                unitService.findByIdentifier("UNIT001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                "UNIT001",
                result.getIdentifier());
    }

    @Test
    void save_NewUnit() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");

        Unit unit = new Unit();

        when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(null);

        when(modelMapper.map(dto, Unit.class))
                .thenReturn(unit);

        when(unitRepository.save(unit))
                .thenReturn(unit);

        UnitDto result =
                unitService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                "UNIT001",
                result.getIdentifier());

        verify(unitRepository).save(unit);
    }

    @Test
    void save_UnitAlreadyExists() {

        Unit existing = new Unit();

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");

        when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(existing);

        UnitDto result =
                unitService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(unitRepository, never()).save(any());
    }

    @Test
    void update_UnitExists() {

        Unit existing = new Unit();

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");

        when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(existing);

        when(unitRepository.save(existing))
                .thenReturn(existing);

        UnitDto result =
                unitService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                "UNIT001",
                result.getIdentifier());

        verify(modelMapper).map(dto, existing);
        verify(unitRepository).save(existing);
    }

    @Test
    void update_UnitNotFound() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");

        when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(null);

        UnitDto result =
                unitService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(unitRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        doNothing().when(unitRepository)
                .deleteByIdentifier("UNIT001");

        unitService.delete("UNIT001");

        verify(unitRepository)
                .deleteByIdentifier("UNIT001");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Unit> units =
                List.of(new Unit(), new Unit());

        Page<Unit> page =
                new PageImpl<>(
                        units,
                        pageable,
                        2
                );

        List<UnitDto> dtoList =
                List.of(new UnitDto(), new UnitDto());

        when(unitRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(units), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<UnitDto> result =
                unitService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                2,
                result.getContent().size());

        Assertions.assertEquals(
                0,
                result.getPage());

        Assertions.assertEquals(
                10,
                result.getSizePerPage());

        Assertions.assertEquals(
                1,
                result.getTotalPages());

        Assertions.assertEquals(
                2,
                result.getTotalRecords());

        verify(unitRepository)
                .findAll(pageable);

        verify(modelMapper)
                .map(eq(units), any(Type.class));
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Unit> emptyList = List.of();

        Page<Unit> page =
                new PageImpl<>(emptyList);

        when(unitRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(emptyList), any(Type.class)))
                .thenReturn(List.of());

        WsDto<UnitDto> result =
                unitService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertTrue(
                result.getContent().isEmpty());

        Assertions.assertEquals(
                0,
                result.getTotalRecords());

        verify(unitRepository)
                .findAll(pageable);
    }

    @Test
    void toggleStatus_UnitFound() {

        Unit unit = new Unit();
        unit.setStatus(true);

        when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(unit);

        unitService.toggleStatus("UNIT001");

        Assertions.assertFalse(unit.isStatus());

        verify(unitRepository)
                .save(unit);
    }

    @Test
    void toggleStatus_UnitNotFound() {

        when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(null);

        unitService.toggleStatus("UNIT001");

        verify(unitRepository, never())
                .save(any());
    }
}