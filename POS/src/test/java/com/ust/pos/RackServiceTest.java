package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.impl.RackServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        dto.setIdentifier("R1");
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
    }

    @Test
    void save_failure_duplicate() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        when(rackRepository.findByIdentifier("R1")).thenReturn(new Rack());
        RackDto response = rackService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Rack already exists", response.getMessage());
    }

    @Test
    void update_success() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        Rack existing = new Rack();
        Rack saved = new Rack();
        RackDto mapped = new RackDto();
        when(rackRepository.findByIdentifier("R1")).thenReturn(existing);
        when(rackRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, RackDto.class)).thenReturn(mapped);
        RackDto response = rackService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Rack updated successfully", response.getMessage());
    }

    @Test
    void update_failure_not_found() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        when(rackRepository.findByIdentifier("R1")).thenReturn(null);
        RackDto response = rackService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Rack not found", response.getMessage());
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
        RackDto response = rackService.findByIdentifier("R1");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Rack not found", response.getMessage());
    }

    @Test
    void find_all_pageable() {
        List<Rack> list = List.of(new Rack());
        Page<Rack> page = new PageImpl<>(list);
        List<RackDto> mappedList = List.of(new RackDto());
        when(rackRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(eq(list), ArgumentMatchers.<Type>any())).thenReturn(mappedList);
        List<RackDto> result = rackService.findAll(PageRequest.of(0, 5));
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void find_all_null_pageable() {
        List<Rack> list = List.of(new Rack());
        when(rackRepository.findAll(Mockito.nullable(Pageable.class))).
                thenReturn(new PageImpl<>(list));
        when(modelMapper.map(eq(list), ArgumentMatchers.<Type>any()))
                .thenReturn(List.of(new RackDto()));
        List<RackDto> result = rackService.findAll(null);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void active_racks() {
        List<Rack> list = List.of(new Rack(), new Rack());
        when(rackRepository.findAll(Mockito.nullable(Pageable.class)))
                .thenReturn(new PageImpl<>(list));
        RackDto active = new RackDto();
        active.setStatus(true);
        RackDto inactive = new RackDto();
        inactive.setStatus(false);
        when(modelMapper.map(eq(list), ArgumentMatchers.<Type>any()))
                .thenReturn(List.of(active, inactive));
        List<RackDto> result = rackService.getActiveRacks();
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void delete_test() {
        rackService.delete("R1");
        verify(rackRepository).deleteByIdentifier("R1");
    }

    @Test
    void toggle_success() {
        Rack rack = new Rack();
        rack.setStatus(true);
        Rack saved = new Rack();
        RackDto mapped = new RackDto();
        when(rackRepository.findByIdentifier("R1")).thenReturn(rack);
        when(rackRepository.save(rack)).thenReturn(saved);
        when(modelMapper.map(saved, RackDto.class)).thenReturn(mapped);
        RackDto response = rackService.toggleStatus("R1");
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
    }

    @Test
    void toggle_failure() {
        when(rackRepository.findByIdentifier("R1")).thenReturn(null);
        RackDto response = rackService.toggleStatus("R1");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Rack not found", response.getMessage());
    }
}