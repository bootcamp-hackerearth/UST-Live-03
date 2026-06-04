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
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RacksServiceTest {

    @InjectMocks
    private RacksServiceImpl racksService;

    @Mock
    private RacksRepository racksRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTestSuccess() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("A1");

        Racks racks = new Racks();

        when(racksRepository.findByIdentifier("A1")).thenReturn(null);
        when(modelMapper.map(dto, Racks.class)).thenReturn(racks);

        RacksDto response = racksService.save(dto);

        Assertions.assertEquals("A1", response.getIdentifier());
        verify(racksRepository).save(racks);
    }

    @Test
    void saveTestFailure() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("A1");

        Racks racks = new Racks();
        racks.setIdentifier("A1");

        when(racksRepository.findByIdentifier("A1")).thenReturn(racks);

        RacksDto response = racksService.save(dto);

        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
        verify(racksRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("A1");

        Racks racks = new Racks();

        when(racksRepository.findByIdentifier("A1")).thenReturn(racks);

        RacksDto response = racksService.update(dto);

        Assertions.assertEquals("A1", response.getIdentifier());
        verify(modelMapper).map(dto, racks);
        verify(racksRepository).save(racks);
    }

    @Test
    void updateTestFailure() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("A1");

        when(racksRepository.findByIdentifier("A1")).thenReturn(null);

        RacksDto response = racksService.update(dto);

        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
        verify(racksRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        racksService.delete("A1");
        verify(racksRepository).deleteByIdentifier("A1");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Racks racks = new Racks();
        racks.setIdentifier("A1");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("A1");

        when(racksRepository.findByIdentifier("A1")).thenReturn(racks);
        when(modelMapper.map(racks, RacksDto.class)).thenReturn(dto);

        RacksDto result = racksService.findByIdentifier("A1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("A1", result.getIdentifier());
    }

    @Test
    void findByIdentifierTest() {
        when(racksRepository.findByIdentifier("A1")).thenReturn(null);

        RacksDto result = racksService.findByIdentifier("A1");

        Assertions.assertNull(result);
    }

    @Test
    void findAllTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Racks> page = mock(Page.class);

        List<Racks> racksList = List.of(new Racks(), new Racks());
        List<RacksDto> dtoList = List.of(new RacksDto(), new RacksDto());

        when(racksRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(racksList);
        when(modelMapper.map(eq(racksList), any(Type.class))).thenReturn(dtoList);

        List<RacksDto> result = racksService.findAll(pageable);

        Assertions.assertEquals(2, result.size());

        verify(racksRepository).findAll(pageable);
        verify(page).getContent();
        verify(modelMapper).map(eq(racksList), any(Type.class));
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Racks racks = new Racks();
        racks.setIdentifier("A1");
        racks.setStatus(true);

        when(racksRepository.findByIdentifier("A1")).thenReturn(racks);

        racksService.toggleStatus("A1");

        Assertions.assertFalse(racks.getStatus());
        verify(racksRepository).save(racks);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Racks racks = new Racks();
        racks.setIdentifier("A1");
        racks.setStatus(false);

        when(racksRepository.findByIdentifier("A1")).thenReturn(racks);

        racksService.toggleStatus("A1");

        Assertions.assertTrue(racks.getStatus());
        verify(racksRepository).save(racks);
    }

    @Test
    void toggleStatusNullTest() {
        when(racksRepository.findByIdentifier("A1")).thenReturn(null);

        racksService.toggleStatus("A1");

        verify(racksRepository, never()).save(any());
    }
}