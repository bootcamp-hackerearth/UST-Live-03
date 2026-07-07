package com.ust.pos;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Racks;
import com.ust.pos.modell.RacksRepository;
import com.ust.pos.racks.service.impl.RacksServiceImpl;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RacksServiceTest {

    public static final String INVALID = "INVALID";
    @InjectMocks
    private RacksServiceImpl service;

    @Mock
    private RacksRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifierTest() {

        Racks racks = new Racks();
        RacksDto dto = new RacksDto();

        when(repository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(racks);

        when(mapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        assertNotNull(service.findByIdentifier("R1"));

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByIdentifier(INVALID)
                );

        assertEquals(
                "Racks with identifier 'INVALID' not found",
                exception.getMessage()
        );
    }

    @Test
    void saveTest() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Racks racks = new Racks();
        racks.setStatus(null);

        when(repository.findByIdentifier("R1"))
                .thenReturn(null);

        when(mapper.map(dto, Racks.class))
                .thenReturn(racks);

        RacksDto result = service.save(dto);

        verify(repository).save(racks);

        assertNotNull(result);
        assertTrue(racks.getStatus());

        Racks duplicate = new Racks();
        duplicate.setDeleted(false);

        when(repository.findByIdentifier("R1"))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Shelf with identifier - R1 already exists",
                result.getMessage()
        );

        duplicate.setDeleted(true);

        when(repository.findByIdentifier("R1"))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Rack with Identifier R1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateAndDeleteTest() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Racks racks = new Racks();
        racks.setIdentifier("R1");
        racks.setCreatedBy("admin");
        racks.setCreatedOn(LocalDateTime.now());

        when(repository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(racks);

        RacksDto result = service.update(dto);

        verify(mapper).map(dto, racks);
        verify(repository).save(racks);

        assertNotNull(result);

        RacksDto invalidDto = new RacksDto();
        invalidDto.setIdentifier(INVALID);

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        result = service.update(invalidDto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Shelf with identifier - INVALID not found",
                result.getMessage()
        );

        service.delete("R1");

        verify(repository, atLeast(2))
                .save(any(Racks.class));

        when(repository.findByIdentifierAndDeletedFalse("NOTFOUND"))
                .thenReturn(null);

        service.delete("NOTFOUND");
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Racks racks = new Racks();
        racks.setStatus(false);

        RacksDto dto = new RacksDto();

        when(repository.findByIdentifierAndDeletedFalse("R4"))
                .thenReturn(racks);

        when(repository.save(racks))
                .thenReturn(racks);

        when(mapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        RacksDto result = service.toggleStatus("R4");

        assertNotNull(result);
        assertTrue(racks.getStatus());

        verify(repository).save(racks);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Racks> page =
                new PageImpl<>(
                        List.of(new Racks()),
                        pageable,
                        1
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new RacksDto()));

        WsDto<RacksDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());

        Specification<Racks> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<RacksDto> specResult =
                service.findAll(specification, pageable);

        assertEquals(1, specResult.getDtoList().size());
        assertEquals(1, specResult.getTotalRecords());

        verify(repository)
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findAllActiveTest() {

        Racks racks = new Racks();
        RacksDto dto = new RacksDto();

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(racks));

        when(mapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        List<RacksDto> result =
                service.findAllActive();

        assertEquals(1, result.size());

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(Collections.emptyList());

        result = service.findAllActive();

        assertTrue(result.isEmpty());
    }

    @Test
    void toggleStatusTest() {

        Racks activeRack = new Racks();
        activeRack.setStatus(true);

        RacksDto dto = new RacksDto();

        when(repository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(activeRack);

        when(repository.save(activeRack))
                .thenReturn(activeRack);

        when(mapper.map(activeRack, RacksDto.class))
                .thenReturn(dto);

        service.toggleStatus("R1");

        assertFalse(activeRack.getStatus());

        Racks nullStatusRack = new Racks();
        nullStatusRack.setStatus(null);

        when(repository.findByIdentifierAndDeletedFalse("R2"))
                .thenReturn(nullStatusRack);

        when(repository.save(nullStatusRack))
                .thenReturn(nullStatusRack);

        when(mapper.map(nullStatusRack, RacksDto.class))
                .thenReturn(dto);

        service.toggleStatus("R2");

        assertTrue(nullStatusRack.getStatus());

        when(repository.findByIdentifierAndDeletedFalse("R3"))
                .thenReturn(null);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.toggleStatus("R3")
                );

        assertEquals(
                "racks not found with identifier: R3",
                exception.getMessage()
        );
    }
}