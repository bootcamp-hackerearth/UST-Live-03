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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RacksServiceTest {

    @InjectMocks
    private RacksServiceImpl racksService;

    @Mock
    private RacksRepository racksRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("RACK1");

        Racks racks = new Racks();

        Mockito.when(racksRepository.findByIdentifier("RACK1")).thenReturn(null);
        Mockito.when(modelMapper.map(racksDto, Racks.class)).thenReturn(racks);

        RacksDto response = racksService.save(racksDto);

        Assertions.assertEquals("RACK1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(racksRepository).save(racks);
    }

    @Test
    void saveFailureTest() {

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("RACK1");

        Mockito.when(racksRepository.findByIdentifier("RACK1")).thenReturn(new Racks());

        RacksDto response = racksService.save(racksDto);

        Assertions.assertEquals("RACK1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateSuccessTest() {

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("RACK1");

        Racks existingRacks = new Racks();
        existingRacks.setIdentifier("RACK1");

        Mockito.when(racksRepository.findByIdentifier("RACK1")).thenReturn(existingRacks);

        RacksDto response = racksService.update(racksDto);

        Assertions.assertEquals("RACK1", response.getIdentifier());
        verify(racksRepository).save(existingRacks);
    }

    @Test
    void updateFailureTest() {

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("RACK1");

        Mockito.when(racksRepository.findByIdentifier("RACK1")).thenReturn(null);

        RacksDto response = racksService.update(racksDto);

        Assertions.assertEquals("RACK1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteSuccessTest() {

        racksService.delete("RACK1");

        verify(racksRepository).deleteByIdentifier("RACK1");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Racks racks = new Racks();
        racks.setIdentifier("RACK1");

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("RACK1");

        Mockito.when(racksRepository.findByIdentifier("RACK1")).thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class)).thenReturn(racksDto);

        RacksDto response = racksService.findByIdentifier("RACK1");

        Assertions.assertEquals("RACK1", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(racksRepository.findByIdentifier("RACK1")).thenReturn(null);

        RacksDto response = racksService.findByIdentifier("RACK1");

        Assertions.assertNull(response);
    }

    @Test
    void findAllSuccessTest() {

        Racks r1 = new Racks();
        r1.setIdentifier("RACK1");

        Racks r2 = new Racks();
        r2.setIdentifier("RACK2");

        List<Racks> racksList = List.of(r1, r2);

        RacksDto d1 = new RacksDto();
        d1.setIdentifier("RACK1");

        RacksDto d2 = new RacksDto();
        d2.setIdentifier("RACK2");

        List<RacksDto> racksDtos = List.of(d1, d2);

        Page<Racks> page = new PageImpl<>(racksList);
        Pageable pageable = PageRequest.of(0, 20);

        Mockito.when(racksRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(racksList), Mockito.any(Type.class))).thenReturn(racksDtos);

        List<RacksDto> result = racksService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void findAllActiveSuccessTest() {

        Racks r1 = new Racks();
        r1.setIdentifier("RACK1");
        r1.setStatus(true);

        Racks r2 = new Racks();
        r2.setIdentifier("RACK2");
        r2.setStatus(true);

        List<Racks> activeRacks = List.of(r1, r2);

        RacksDto d1 = new RacksDto();
        d1.setIdentifier("RACK1");

        RacksDto d2 = new RacksDto();
        d2.setIdentifier("RACK2");

        List<RacksDto> racksDtos = List.of(d1, d2);

        Mockito.when(racksRepository.findByStatus(true)).thenReturn(activeRacks);

        Mockito.when(modelMapper.map(Mockito.eq(activeRacks), Mockito.any(Type.class))).thenReturn(racksDtos);

        List<RacksDto> result = racksService.findAllActive();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void toggleStatusSuccessTest() {

        Racks racks = new Racks();
        racks.setStatus(true);

        Mockito.when(racksRepository.findByIdentifier("RACK1")).thenReturn(racks);

        racksService.toggleStatus("RACK1");

        Assertions.assertFalse(racks.isStatus());
        verify(racksRepository).save(racks);
    }

    @Test
    void toggleStatusRacksNotFoundTest() {

        Mockito.when(racksRepository.findByIdentifier("RACK1")).thenReturn(null);

        racksService.toggleStatus("RACK1");

        Mockito.verify(racksRepository, Mockito.never()).save(Mockito.any());
    }
}