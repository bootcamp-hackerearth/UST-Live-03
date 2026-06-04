package com.ust.pos;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.model.Racks;
import com.ust.pos.model.RacksRepository;
import com.ust.pos.racks.service.impl.RacksServiceImpl;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RacksServiceTest {

    @InjectMocks
    private RacksServiceImpl racksService;

    @Mock
    private RacksRepository racksRepository;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK01");

        Racks racks = new Racks();
        racks.setIdentifier("RACK01");

        when(racksRepository.findByIdentifier("RACK01")).thenReturn(null);
        when(modelMapper.map(dto, Racks.class)).thenReturn(racks);

        RacksDto response = racksService.save(dto);

        assertEquals("RACK01", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        verify(racksRepository).save(racks);
    }

    @Test
    void saveFailureTest() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK01");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK01");

        when(racksRepository.findByIdentifier("RACK01")).thenReturn(racks);

        RacksDto response = racksService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(racksRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK01");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK01");

        when(racksRepository.findByIdentifier("RACK01")).thenReturn(racks);

        RacksDto response = racksService.update(dto);

        assertEquals("RACK01", response.getIdentifier());
        verify(modelMapper).map(dto, racks);
        verify(racksRepository).save(racks);
    }

    @Test
    void updateFailureTest() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK01");

        when(racksRepository.findByIdentifier("RACK01")).thenReturn(null);

        RacksDto response = racksService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(racksRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        racksService.delete("RACK01");
        verify(racksRepository).deleteByIdentifier("RACK01");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK01");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK01");

        when(racksRepository.findByIdentifier("RACK01")).thenReturn(racks);
        when(modelMapper.map(racks, RacksDto.class)).thenReturn(dto);

        RacksDto response = racksService.findByIdentifier("RACK01");

        assertNotNull(response);
        assertEquals("RACK01", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(racksRepository.findByIdentifier("RACK01")).thenReturn(null);

        RacksDto response = racksService.findByIdentifier("RACK01");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Racks> racksList = List.of(new Racks(), new Racks());
        Page<Racks> page = new PageImpl<>(racksList);

        List<RacksDto> dtoList = List.of(new RacksDto(), new RacksDto());

        when(racksRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(racksList),
                any(Type.class)
        )).thenReturn(dtoList);

        List<RacksDto> result = racksService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(racksRepository).findAll(pageable);
    }

    @Test
    void toggleStatusSuccessTest() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK01");
        racks.setStatus(true);

        when(racksRepository.findByIdentifier("RACK01")).thenReturn(racks);

        racksService.toggleStatus("RACK01");

        Assertions.assertFalse(racks.isStatus());
        verify(racksRepository).save(racks);
    }

    @Test
    void toggleStatusFailureTest() {
        when(racksRepository.findByIdentifier("RACK01")).thenReturn(null);

        racksService.toggleStatus("RACK01");

        verify(racksRepository, never()).save(any());
    }
}