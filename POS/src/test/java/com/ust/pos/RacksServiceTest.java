package com.ust.pos;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
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

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RacksServiceTest {

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
    }

    @Test
    void saveSuccessTest() {

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

        assertEquals("R1", result.getIdentifier());
        assertTrue(racks.getStatus());
    }

    @Test
    void saveDuplicateTest() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Racks racks = new Racks();
        racks.setDeleted(false);

        when(repository.findByIdentifier("R1"))
                .thenReturn(racks);

        RacksDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Shelf with identifier - R1 already exists",
                result.getMessage()
        );
    }

    @Test
    void saveSoftDeletedTest() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Racks racks = new Racks();
        racks.setDeleted(true);

        when(repository.findByIdentifier("R1"))
                .thenReturn(racks);

        RacksDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Rack with Identifier R1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateSuccessTest() {

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
    }

    @Test
    void updateNotFoundTest() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        when(repository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(null);

        RacksDto result = service.update(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Shelf with identifier - R1 not found",
                result.getMessage()
        );
    }

    @Test
    void deleteTest() {

        Racks racks = new Racks();

        when(repository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(racks)
                .thenReturn(null);

        service.delete("R1");

        verify(repository).save(racks);

        service.delete("R1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Racks> page =
                new PageImpl<>(List.of(new Racks()), pageable, 1);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new RacksDto()));

        WsDto<RacksDto> result = service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Racks> page =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        WsDto<RacksDto> result = service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
    }

    @Test
    void findAllActiveTest() {

        Racks racks = new Racks();
        RacksDto dto = new RacksDto();

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(racks));

        when(mapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        assertEquals(1, service.findAllActive().size());

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(Collections.emptyList());

        assertTrue(service.findAllActive().isEmpty());
    }

    @Test
    void toggleStatusTest() {

        Racks racks = new Racks();
        racks.setStatus(true);

        RacksDto dto = new RacksDto();

        when(repository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(racks);

        when(repository.save(racks))
                .thenReturn(racks);

        when(mapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        service.toggleStatus("R1");

        assertFalse(racks.getStatus());

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