package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    public static final String INVALID = "INVALID";
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

        when(repository.findByIdentifierAndDeletedFalse("NOTFOUND"))
                .thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByIdentifier("NOTFOUND")
                );

        assertEquals(
                "Unit with identifier 'NOTFOUND' not found",
                exception.getMessage()
        );
    }

    @Test
    void saveTest() {

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

        Unit existing = new Unit();
        existing.setDeleted(false);

        when(repository.findByIdentifier("KG"))
                .thenReturn(existing);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Warehouse with identifier - KG already exists",
                result.getMessage()
        );

        existing.setDeleted(true);

        when(repository.findByIdentifier("KG"))
                .thenReturn(existing);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Unit with Identifier KG already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateTest() {

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

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        UnitDto invalidDto = new UnitDto();
        invalidDto.setIdentifier(INVALID);

        result = service.update(invalidDto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Warehouse with identifier - INVALID not found",
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
                new PageImpl<>(
                        List.of(new Unit()),
                        pageable,
                        1
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new UnitDto()));

        WsDto<UnitDto> result = service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        Specification<Unit> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<UnitDto> specResult =
                service.findAll(specification, pageable);

        assertEquals(1, specResult.getDtoList().size());
        assertEquals(1, specResult.getTotalRecords());
        assertEquals(1, specResult.getTotalPage());
        assertEquals(10, specResult.getSizePerPage());
        assertEquals(0, specResult.getPage());

        verify(repository).findAllByDeletedFalse(pageable);
        verify(repository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void toggleStatusTest() {

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

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.toggleStatus(INVALID)
                );

        assertEquals(
                "Unit not found with identifier: INVALID",
                exception.getMessage()
        );
    }

    @Test
    void toggleStatusFalseBranchTest() {

        Unit unit = new Unit();
        unit.setStatus(false);

        UnitDto dto = new UnitDto();

        when(repository.findByIdentifierAndDeletedFalse("KG3"))
                .thenReturn(unit);

        when(repository.save(unit))
                .thenReturn(unit);

        when(mapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        UnitDto result = service.toggleStatus("KG3");

        assertNotNull(result);

        assertTrue(unit.getStatus());

        verify(repository).save(unit);
    }
}