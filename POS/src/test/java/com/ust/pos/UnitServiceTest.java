package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private UnitServiceImpl unitService;

    private Unit unit;
    private UnitDto unitDto;

    @BeforeEach
    void setUp() {

        unit = new Unit();
        unit.setIdentifier("UNIT1");
        unit.setStatus(true);
        unit.setDeleted(false);

        unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT1");
    }

    @Test
    void testFindByIdentifier() {

        when(unitRepository.findByIdentifier("UNIT1"))
                .thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(unitDto);

        UnitDto result = unitService.findByIdentifier("UNIT1");

        assertNotNull(result);
        assertEquals("UNIT1", result.getIdentifier());
    }

    @Test
    void testSave_NewUnit() {

        when(unitRepository.findByIdentifier("UNIT1"))
                .thenReturn(null);

        when(modelMapper.map(unitDto, Unit.class))
                .thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);

        verify(unitRepository).save(unit);
    }

    @Test
    void testSave_AlreadyExists() {

        when(unitRepository.findByIdentifier("UNIT1"))
                .thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_SoftDeleted() {

        unit.setDeleted(true);

        when(unitRepository.findByIdentifier("UNIT1"))
                .thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    @Test
    void testUpdate_Success() {

        when(unitRepository.findByIdentifier("UNIT1"))
                .thenReturn(unit);

        doNothing().when(modelMapper)
                .map(unitDto, unit);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);

        verify(unitRepository).save(unit);
    }

    @Test
    void testUpdate_NotFound() {

        when(unitRepository.findByIdentifier("UNIT1"))
                .thenReturn(null);

        UnitDto result = unitService.update(unitDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testDelete() {

        when(unitRepository.findByIdentifier("UNIT1"))
                .thenReturn(unit);

        unitService.delete("UNIT1");

        assertTrue(unit.isDeleted());
        assertFalse(unit.isStatus());

        verify(unitRepository).save(unit);
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Unit> page =
                new PageImpl<>(Collections.singletonList(unit));

        when(unitRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(unitDto));

        WsDto<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Unit> specification =
                mock(Specification.class);

        Page<Unit> page =
                new PageImpl<>(Collections.singletonList(unit));

        when(unitRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(unitDto));

        WsDto<UnitDto> result =
                unitService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());

        verify(unitRepository)
                .findAll(specification, pageable);
    }

    @Test
    void testChangeToggleStatus() {

        when(unitRepository.findByIdentifier("UNIT1"))
                .thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(unitDto);

        UnitDto result =
                unitService.changeToggleStatus("UNIT1", false);

        assertNotNull(result);
        assertFalse(unit.isStatus());

        verify(unitRepository).save(unit);
    }

    @Test
    void testChangeToggleStatus_UnitNotFound() {

        when(unitRepository.findByIdentifier("UNIT1"))
                .thenReturn(null);

        when(modelMapper.map(null, UnitDto.class))
                .thenReturn(null);

        UnitDto result =
                unitService.changeToggleStatus("UNIT1", false);

        assertNull(result);
    }

    @Test
    void testFindActiveStatus() {

        Unit inactiveUnit = new Unit();
        inactiveUnit.setStatus(false);

        when(unitRepository.findAll())
                .thenReturn(List.of(unit, inactiveUnit));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(unitDto));

        List<UnitDto> result =
                unitService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}