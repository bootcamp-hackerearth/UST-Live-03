package com.ust.pos;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.model.CommonFields;
import com.ust.pos.model.Racks;
import com.ust.pos.model.RacksRepository;
import com.ust.pos.racks.service.impl.RacksServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
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
        dto.setIdentifier("Admin");
        Racks racks = new Racks();

        Mockito.when(racksRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Racks.class)).thenReturn(racks);
        RacksDto response = racksService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(racksRepository).save(racks);
    }

    @Test
    void saveTestFailure() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("Admin");

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(new Racks());
        RacksDto response = racksService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestSuccess() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("Admin");
        Racks racks = new Racks();

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(racks);
        RacksDto response = racksService.update(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(racksRepository).save(racks);
    }

    @Test
    void updateTestFailure() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("Admin");

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        RacksDto response = racksService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {

        racksService.delete("Admin");
        verify(racksRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Racks racks = new Racks();
        racks.setIdentifier("Admin");
        RacksDto dto = new RacksDto();
        dto.setIdentifier("Admin");

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        RacksDto response = racksService.findByIdentifier("Admin");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        RacksDto response = racksService.findByIdentifier("Admin");

        Assertions.assertNull(response);
    }

    @Test
    void toggleStatus_trueToFalse() {

        Racks racks = new Racks();
        racks.setIdentifier("Admin");
        racks.setStatus(true);

        when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(racks);
        racksService.toggleStatus("Admin");

        Assertions.assertFalse(racks.isStatus());
        verify(racksRepository).save(argThat(saved ->
                !saved.isStatus()
        ));
    }

    @Test
    void toggleStatus_falseToTrue() {

        Racks racks = new Racks();
        racks.setIdentifier("Admin");
        racks.setStatus(false);

        when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(racks);
        racksService.toggleStatus("Admin");

        Assertions.assertTrue(racks.isStatus());
        verify(racksRepository).save(argThat(CommonFields::isStatus
        ));
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);
        List<Racks> racks = List.of(new Racks());
        Page<Racks> page = new PageImpl<>(racks);
        List<RacksDto> dtos = List.of(new RacksDto());

        when(racksRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(
                eq(racks),
                any(Type.class)
        )).thenReturn(dtos);

        List<RacksDto> result = racksService.findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(racksRepository).findAll(pageable);
    }

    @Test
    void toggleStatusNotFoundTest() {

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        racksService.toggleStatus("Admin");

        verify(racksRepository, never()).save(any());
    }

    @Test
    void findActiveRacksTest() {

        List<Racks> racksList = List.of(new Racks());
        Mockito.when(racksRepository.findByStatus(true))
                .thenReturn(racksList);

        List<Racks> response = racksService.findActiveRacks();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
    }
}
