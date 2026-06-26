package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Unit;
import com.ust.pos.modell.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @InjectMocks
    private UnitServiceImpl service;

    @Mock
    private UnitRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifierTest() {

        Unit unit = new Unit();
        UnitDto dto = new UnitDto();

        when(repository.findByIdentifierAndDeletedFalse("KG"))
                .thenReturn(unit);

        when(mapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        assertNotNull(service.findByIdentifier("KG"));
    }

    @Test
    void saveSuccessTest() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit unit = new Unit();
        unit.setStatus(null);

        when(repository.findByIdentifier("KG"))
                .thenReturn(null);

        when(mapper.map(dto, Unit.class))
                .thenReturn(unit);

        UnitDto result = service.save(dto);

        verify(repository).save(unit);

        assertEquals("KG", result.getIdentifier());
        assertTrue(unit.getStatus());
    }

    @Test
    void saveDuplicateTest() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit existing = new Unit();
        existing.setDeleted(false);

        when(repository.findByIdentifier("KG"))
                .thenReturn(existing);

        UnitDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Warehouse with identifier - KG already exists",
                result.getMessage()
        );
    }

    @Test
    void saveSoftDeletedTest() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit existing = new Unit();
        existing.setDeleted(true);

        when(repository.findByIdentifier("KG"))
                .thenReturn(existing);

        UnitDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Unit with Identifier KG already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateSuccessTest() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unit.setCreatedBy("admin");
        unit.setCreatedOn(LocalDateTime.now());

        when(repository.findByIdentifierAndDeletedFalse("KG"))
                .thenReturn(unit);

        UnitDto result = service.update(dto);

        verify(mapper).map(dto, unit);
        verify(repository).save(unit);

        assertNotNull(result);
    }

    @Test
    void updateNotFoundTest() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        when(repository.findByIdentifierAndDeletedFalse("KG"))
                .thenReturn(null);

        UnitDto result = service.update(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Warehouse with identifier - KG not found",
                result.getMessage()
        );
    }

    @Test
    void deleteTest() {

        Unit unit = new Unit();

        when(repository.findByIdentifierAndDeletedFalse("KG"))
                .thenReturn(unit)
                .thenReturn(null);

        service.delete("KG");

        verify(repository).save(unit);

        service.delete("KG");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Unit> page =
                new PageImpl<>(List.of(new Unit()), pageable, 1);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new UnitDto()));

        WsDto<UnitDto> result = service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Unit> page =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        WsDto<UnitDto> result = service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
    }

    @Test
    void toggleStatusSuccessAndNullStatusTest() {

        Unit activeUnit = new Unit();
        activeUnit.setStatus(true);

        UnitDto dto = new UnitDto();

        when(repository.findByIdentifierAndDeletedFalse("KG"))
                .thenReturn(activeUnit);

        when(repository.save(activeUnit))
                .thenReturn(activeUnit);

        when(mapper.map(activeUnit, UnitDto.class))
                .thenReturn(dto);

        service.toggleStatus("KG");

        assertFalse(activeUnit.getStatus());

        Unit nullStatusUnit = new Unit();
        nullStatusUnit.setStatus(null);

        when(repository.findByIdentifierAndDeletedFalse("KG2"))
                .thenReturn(nullStatusUnit);

        when(repository.save(nullStatusUnit))
                .thenReturn(nullStatusUnit);

        when(mapper.map(nullStatusUnit, UnitDto.class))
                .thenReturn(dto);

        service.toggleStatus("KG2");

        assertTrue(nullStatusUnit.getStatus());
    }

    @Test
    void toggleStatusNotFoundTest() {

        when(repository.findByIdentifierAndDeletedFalse("KG"))
                .thenReturn(null);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.toggleStatus("KG")
                );

        assertEquals(
                "Unit not found with identifier: KG",
                exception.getMessage()
        );
    }
}