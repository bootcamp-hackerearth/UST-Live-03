package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.impl.RackServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RackServiceTest {

    @InjectMocks
    private RackServiceImpl rackService;

    @Mock
    private RackRepository rackRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void save_success() {

        RackDto dto = new RackDto();
        dto.setName("R1");

        Rack entity = new Rack();
        Rack saved = new Rack();
        RackDto mapped = new RackDto();

        when(rackRepository.findByIdentifier("R1")).thenReturn(null);
        when(modelMapper.map(dto, Rack.class)).thenReturn(entity);
        when(rackRepository.save(entity)).thenReturn(saved);
        when(modelMapper.map(saved, RackDto.class)).thenReturn(mapped);

        RackDto response = rackService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Rack saved successfully", response.getMessage());

        verify(rackRepository).save(entity);
    }

    @Test
    void save_failure_duplicate() {

        RackDto dto = new RackDto();
        dto.setName("R1");

        when(rackRepository.findByIdentifier("R1"))
                .thenReturn(new Rack());

        RackDto response = rackService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Rack already exists", response.getMessage());

        verify(rackRepository, never()).save(any());
    }

    @Test
    void save_failure_name_missing() {

        RackDto dto = new RackDto();
        dto.setName("   ");

        RackDto response = rackService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Rack name is required", response.getMessage());

        verifyNoInteractions(rackRepository);
    }

    @Test
    void update_success() {

        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        dto.setName("R2");

        Rack existing = new Rack();

        when(rackRepository.findByIdentifier("R1")).thenReturn(existing);
        when(rackRepository.save(existing)).thenReturn(existing);
        when(modelMapper.map(existing, RackDto.class)).thenReturn(new RackDto());

        RackDto response = rackService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Rack updated successfully", response.getMessage());

        verify(rackRepository).save(existing);
    }

    @Test
    void update_failure_identifier_missing() {

        RackDto dto = new RackDto();
        dto.setIdentifier("  ");

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> rackService.update(dto));

        Assertions.assertEquals(RackServiceImpl.RACK_NOT_FOUND, ex.getMessage());

        verifyNoInteractions(rackRepository);
    }

    @Test
    void update_failure_not_found() {

        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        when(rackRepository.findByIdentifier("R1")).thenReturn(null);

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> rackService.update(dto));

        verify(rackRepository, never()).save(any());
    }

    @Test
    void update_failure_deleted() {

        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        Rack deleted = new Rack();
        deleted.setDeleted(true);

        when(rackRepository.findByIdentifier("R1")).thenReturn(deleted);

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> rackService.update(dto));

        verify(rackRepository, never()).save(any());
    }

    @Test
    void find_success() {

        Rack rack = new Rack();
        RackDto dto = new RackDto();

        when(rackRepository.findByIdentifier("R1")).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);

        RackDto response = rackService.findByIdentifier("R1");

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void find_failure() {

        when(rackRepository.findByIdentifier("R1")).thenReturn(null);

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> rackService.findByIdentifier("R1"));
    }

    @Test
    void find_failure_deleted() {

        Rack deleted = new Rack();
        deleted.setDeleted(true);

        when(rackRepository.findByIdentifier("R1")).thenReturn(deleted);

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> rackService.findByIdentifier("R1"));
    }

    @Test
    void find_all_pageable() {

        List<Rack> list = List.of(new Rack());
        Page<Rack> page = new PageImpl<>(list);

        List<RackDto> mappedList = List.of(new RackDto());

        when(rackRepository.findByDeletedFalse(any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(eq(list), ArgumentMatchers.<Type>any()))
                .thenReturn(mappedList);

        WsDto<RackDto> result =
                rackService.findAll(PageRequest.of(0, 5));

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1L, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
    }

    @Test
    void active_racks() {

        Rack r1 = new Rack();
        r1.setStatus(true);

        Rack r2 = new Rack();
        r2.setStatus(false);

        when(rackRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(r1, r2));

        when(modelMapper.map(any(Rack.class), eq(RackDto.class)))
                .thenReturn(new RackDto());

        List<RackDto> result = rackService.getActiveRacks();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void delete_test() {

        Rack rack = new Rack();

        when(rackRepository.findByIdentifier("R1"))
                .thenReturn(rack);

        rackService.delete("R1");

        verify(rackRepository).save(rack);
        Assertions.assertTrue(rack.getDeleted());
    }

    @Test
    void delete_failure_not_found() {

        when(rackRepository.findByIdentifier("R1")).thenReturn(null);

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> rackService.delete("R1"));

        verify(rackRepository, never()).save(any());
    }

    @Test
    void delete_failure_already_deleted() {

        Rack deleted = new Rack();
        deleted.setDeleted(true);

        when(rackRepository.findByIdentifier("R1")).thenReturn(deleted);

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> rackService.delete("R1"));

        verify(rackRepository, never()).save(any());
    }

    @Test
    void toggle_success() {

        Rack rack = new Rack();
        rack.setStatus(true);

        when(rackRepository.findByIdentifier("R1")).thenReturn(rack);
        when(rackRepository.save(rack)).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(new RackDto());

        RackDto response = rackService.toggleStatus("R1");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
    }

    @Test
    void toggle_failure_not_found() {

        when(rackRepository.findByIdentifier("R1")).thenReturn(null);

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> rackService.toggleStatus("R1"));
    }

    @Test
    void toggle_failure_deleted() {

        Rack deleted = new Rack();
        deleted.setDeleted(true);

        when(rackRepository.findByIdentifier("R1")).thenReturn(deleted);

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> rackService.toggleStatus("R1"));
    }
}