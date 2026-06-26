package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
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

    // ================= SAVE SUCCESS =================
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

    // ================= SAVE FAILURE DUPLICATE =================
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

    // ================= SAVE FAILURE NAME MISSING =================
    @Test
    void save_failure_name_missing() {

        RackDto dto = new RackDto();
        dto.setName("   ");

        RackDto response = rackService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Rack name is required", response.getMessage());

        verifyNoInteractions(rackRepository);
    }

    // ================= UPDATE SUCCESS =================
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

    // ================= UPDATE FAILURE =================
    @Test
    void update_failure_not_found() {

        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        when(rackRepository.findByIdentifier("R1")).thenReturn(null);

        RackDto response = rackService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Rack not found", response.getMessage());
    }

    // ================= FIND BY ID SUCCESS =================
    @Test
    void find_success() {

        Rack rack = new Rack();
        RackDto dto = new RackDto();

        when(rackRepository.findByIdentifier("R1")).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);

        RackDto response = rackService.findByIdentifier("R1");

        Assertions.assertTrue(response.isSuccess());
    }

    // ================= FIND BY ID FAILURE =================
    @Test
    void find_failure() {

        when(rackRepository.findByIdentifier("R1")).thenReturn(null);

        RackDto response = rackService.findByIdentifier("R1");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Rack not found", response.getMessage());
    }

    // ================= FIND ALL (FIXED RETURN TYPE) =================
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

    // ================= ACTIVE RACKS =================
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

    // ================= DELETE =================
    @Test
    void delete_test() {

        Rack rack = new Rack();

        when(rackRepository.findByIdentifier("R1"))
                .thenReturn(rack);

        rackService.delete("R1");

        verify(rackRepository).save(rack);
    }

    // ================= TOGGLE SUCCESS =================
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

    // ================= TOGGLE FAILURE =================
    @Test
    void toggle_failure() {

        when(rackRepository.findByIdentifier("R1")).thenReturn(null);

        RackDto response = rackService.toggleStatus("R1");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Rack not found", response.getMessage());
    }
}