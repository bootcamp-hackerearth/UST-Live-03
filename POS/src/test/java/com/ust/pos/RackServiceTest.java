package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.impl.RackServiceImpl;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RackServiceTest {

    private static final Long ID = 1L;
    private static final String IDENTIFIER = "RACK001";
    @Mock
    private RackRepository rackRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private RackServiceImpl rackService;

    @Test
    void createRackSuccess() {

        RackDto dto = new RackDto();
        dto.setIdentifier(IDENTIFIER);

        Rack rack = new Rack();

        when(rackRepository.existsByIdentifier(IDENTIFIER)).thenReturn(false);

        when(modelMapper.map(dto, Rack.class)).thenReturn(rack);

        RackDto result = rackService.createRack(dto);

        assertNotNull(result);

        verify(rackRepository).save(rack);

    }

    @Test
    void createRackAlreadyExists() {

        RackDto dto = new RackDto();
        dto.setIdentifier(IDENTIFIER);

        when(rackRepository.existsByIdentifier(IDENTIFIER)).thenReturn(true);

        RackDto result = rackService.createRack(dto);

        assertFalse(result.isSuccess());

        assertEquals("Rack already exists", result.getMessage());

        verify(rackRepository, never()).save(any());

    }

    @Test
    void updateRackSuccess() {

        RackDto dto = new RackDto();

        Rack rack = new Rack();

        when(modelMapper.map(dto, Rack.class)).thenReturn(rack);

        RackDto result = rackService.updateRack(dto);

        assertNotNull(result);

        verify(rackRepository).save(rack);

    }

    @Test
    void getRackSuccess() {

        Rack rack = new Rack();

        rack.setId(ID);

        RackDto dto = new RackDto();

        when(rackRepository.findById(ID)).thenReturn(java.util.Optional.of(rack));

        when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);

        RackDto result = rackService.getRack(ID);

        assertTrue(result.isSuccess());

    }

    @Test
    void getRackNotFound() {

        when(rackRepository.findById(ID)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rackService.getRack(ID));

    }

    @Test
    void findAllSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Rack rack = new Rack();

        Page<Rack> page = new PageImpl<>(List.of(rack), pageable, 1);

        List<RackDto> dtoList = List.of(new RackDto());

        when(rackRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtoList);

        WsDto<RackDto> result = rackService.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

        assertEquals(1, result.getTotalRecords());

    }

    @Test
    void findAllEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Rack> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(rackRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        WsDto<RackDto> result = rackService.findAll(pageable);

        assertEquals(0, result.getDtoList().size());

        assertEquals(0, result.getTotalRecords());

    }

    @Test
    void findAllSpecificationSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Rack> spec = mock(Specification.class);

        Rack rack = new Rack();

        Page<Rack> page = new PageImpl<>(List.of(rack));

        List<RackDto> dtoList = List.of(new RackDto());

        when(rackRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtoList);

        WsDto<RackDto> result = rackService.findAll(spec, pageable, "rack");

        assertEquals("rack", result.getKeyword());

        assertEquals(1, result.getDtoList().size());

    }

    @Test
    void findAllSpecificationEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Rack> spec = mock(Specification.class);

        Page<Rack> page = new PageImpl<>(Collections.emptyList());

        when(rackRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        WsDto<RackDto> result = rackService.findAll(spec, pageable, "abc");

        assertEquals("abc", result.getKeyword());

        assertEquals(0, result.getDtoList().size());

    }

    @Test
    void deleteRackSuccess() {

        Rack rack = new Rack();

        when(rackRepository.findById(ID)).thenReturn(java.util.Optional.of(rack));

        boolean result = rackService.deleteRack(ID);

        assertTrue(result);

        verify(rackRepository).save(rack);

    }

    @Test
    void deleteRackNotFound() {

        when(rackRepository.findById(ID)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rackService.deleteRack(ID));

    }

}