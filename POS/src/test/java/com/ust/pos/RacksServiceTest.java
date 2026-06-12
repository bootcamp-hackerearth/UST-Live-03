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

    @Mock
    private RackRepository rackRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RackServiceImpl rackService;

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Rack> racks = List.of(new Rack());
        Page<Rack> page = new PageImpl<>(racks);
        List<RackDto> dtoList = List.of(new RackDto());

        when(rackRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<RackDto> result = rackService.findAll(pageable);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());

        verify(rackRepository).findAll(pageable);
    }

    @Test
    void findByIdentifierTest() {

        Rack rack = new Rack();
        rack.setIdentifier("R1");

        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        when(rackRepository.findByIdentifier("R1")).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);

        RackDto result = rackService.findByIdentifier("R1");

        assertNotNull(result);
        assertEquals("R1", result.getIdentifier());
    }

    @Test
    void saveSuccessTest() {

        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        when(rackRepository.findByIdentifier("R1")).thenReturn(null);
        when(modelMapper.map(dto, Rack.class)).thenReturn(new Rack());

        RackDto result = rackService.save(dto);

        assertNotNull(result);
        verify(rackRepository).save(any(Rack.class));
    }

    @Test
    void saveDuplicateTest() {

        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        when(rackRepository.findByIdentifier("R1")).thenReturn(new Rack());

        RackDto result = rackService.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void updateSuccessTest() {

        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        Rack existing = new Rack();

        when(rackRepository.findByIdentifier("R1")).thenReturn(existing);

        doNothing().when(modelMapper).map(eq(dto), eq(existing));

        RackDto result = rackService.update(dto);

        assertNotNull(result);
        verify(rackRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {

        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        when(rackRepository.findByIdentifier("R1")).thenReturn(null);

        RackDto result = rackService.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void deleteTest() {

        rackService.delete("R1");

        verify(rackRepository).deleteByIdentifier("R1");
    }

    @Test
    void toggleStatusTrueToFalseTest() {

        Rack rack = new Rack();
        rack.setStatus(true);

        when(rackRepository.findByIdentifier("R1")).thenReturn(rack);

        rackService.toggleStatus("R1");

        assertFalse(rack.isStatus());
        verify(rackRepository).save(rack);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Rack rack = new Rack();
        rack.setStatus(false);

        when(rackRepository.findByIdentifier("R1")).thenReturn(rack);

        rackService.toggleStatus("R1");

        assertTrue(rack.isStatus());
        verify(rackRepository).save(rack);
    }

    @Test
    void toggleStatusNotFoundTest() {

        when(rackRepository.findByIdentifier("R1")).thenReturn(null);

        rackService.toggleStatus("R1");

        verify(rackRepository, never()).save(any());
    }

    @Test
    void findActiveStatusTest() {

        List<Rack> racks = List.of(new Rack());
        List<RackDto> dtoList = List.of(new RackDto());

        when(rackRepository.findByStatusTrue()).thenReturn(racks);

        when(modelMapper.map(eq(racks), any(Type.class)))
                .thenReturn(dtoList);

        List<RackDto> result = rackService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findActiveRackTest() {

        Rack rack = new Rack();
        rack.setIdentifier("R1");

        List<Rack> racks = List.of(rack);

        RackDto dto = new RackDto();
        dto.setIdentifier("R1");

        when(rackRepository.findByStatusTrue()).thenReturn(racks);

        when(modelMapper.map(
                any(Rack.class),
                eq(RackDto.class)
        )).thenReturn(dto);

        List<RackDto> result = rackService.findActiveRack();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("R1", rack.getIdentifier());
    }
}