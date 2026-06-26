package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
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

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RacksServiceTest {

    @InjectMocks
    private RackServiceImpl service;

    @Mock
    private RackRepository rackRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Rack> page = new PageImpl<>(List.of(new Rack()), pageable, 1);

        when(rackRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new RackDto()));

        WsDto<RackDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findByIdentifierTest() {
        Rack rack = new Rack();
        RackDto dto = new RackDto();

        when(rackRepository.findByIdentifier("R1")).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);

        RackDto result = service.findByIdentifier("R1");

        assertNotNull(result);
    }

    @Test
    void saveSuccessTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        when(rackRepository.findByIdentifier("R1")).thenReturn(null);
        when(modelMapper.map(dto, Rack.class)).thenReturn(new Rack());

        RackDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(rackRepository).save(any());
    }

    @Test
    void saveDuplicateActiveTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        Rack existing = new Rack();
        existing.setDeleted(false);

        when(rackRepository.findByIdentifier("R1")).thenReturn(existing);

        RackDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(rackRepository, never()).save(any());
    }

    @Test
    void saveDuplicateDeletedTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        Rack existing = new Rack();
        existing.setDeleted(true);

        when(rackRepository.findByIdentifier("R1")).thenReturn(existing);

        RackDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void updateSuccessTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        Rack existing = new Rack();

        when(rackRepository.findByIdentifier("R1")).thenReturn(existing);

        RackDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(rackRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        when(rackRepository.findByIdentifier("R1")).thenReturn(null);

        RackDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(rackRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Rack rack = new Rack();
        rack.setDeleted(false);

        when(rackRepository.findByIdentifier("R1")).thenReturn(rack);

        service.delete("R1");

        assertTrue(rack.isDeleted());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Rack rack = new Rack();
        rack.setStatus(true);

        when(rackRepository.findByIdentifier("R1")).thenReturn(rack);

        service.toggleStatus("R1");

        assertFalse(rack.isStatus());
        verify(rackRepository).save(rack);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Rack rack = new Rack();
        rack.setStatus(false);

        when(rackRepository.findByIdentifier("R1")).thenReturn(rack);

        service.toggleStatus("R1");

        assertTrue(rack.isStatus());
        verify(rackRepository).save(rack);
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(rackRepository.findByIdentifier("R1")).thenReturn(null);

        service.toggleStatus("R1");

        verify(rackRepository, never()).save(any());
    }

    @Test
    void findActiveStatusTest() {
        when(rackRepository.findByStatusTrue()).thenReturn(List.of(new Rack()));
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new RackDto()));

        List<RackDto> result = service.findActiveStatus();

        assertEquals(1, result.size());
    }

    @Test
    void findActiveRackTest() {
        Rack rack = new Rack();

        when(rackRepository.findByStatusTrue()).thenReturn(List.of(rack));
        when(modelMapper.map(rack, RackDto.class)).thenReturn(new RackDto());

        List<RackDto> result = service.findActiveRack();

        assertEquals(1, result.size());
    }
}