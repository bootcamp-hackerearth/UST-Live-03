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
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RacksServiceTest {
    @Mock
    private RacksRepository racksRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RacksServiceImpl racksService;

    @Test
    void saveTest() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Admin");

        Mockito.when(racksRepository.findByIdentifier("Admin")).thenReturn(null);
        Racks racks = new Racks();
        Mockito.when(modelMapper.map(racksDto, Racks.class)).thenReturn(racks);
        Mockito.when(racksRepository.save(racks)).thenReturn(racks);

        RacksDto response = racksService.save(racksDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Admin");

        Racks existingRacks = new Racks();
        existingRacks.setIdentifier("Admin");

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(existingRacks);

        RacksDto response = racksService.save(racksDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Racks racks = new Racks();
        racks.setIdentifier("Admin");

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Admin");

        Mockito.when(racksRepository.findByIdentifier("Admin")).thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class)).thenReturn(racksDto);

        RacksDto response = racksService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Admin");

        Racks existingRacks = new Racks();
        existingRacks.setIdentifier("Admin");

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(existingRacks);
        Mockito.when(racksRepository.save(existingRacks))
                .thenReturn(existingRacks);

        RacksDto response = racksService.update(racksDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Admin");

        Mockito.when(racksRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        RacksDto response = racksService.update(racksDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(racksRepository)
                .deleteByIdentifier("Admin");

        boolean response = racksService.delete("Admin");

        Assertions.assertEquals(true, response);
    }

    @Test
    void findAllTest() {
        Racks racks = new Racks();
        racks.setIdentifier("Admin");

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Admin");

        List<Racks> rackss = List.of(racks);
        List<RacksDto> racksDtos = List.of(racksDto);

        Page<Racks> racksPage = new PageImpl<>(rackss, PageRequest.of(0,
                2), rackss.size());

        Pageable pageable = PageRequest.of(0,
                50, Sort.by(new ArrayList<>()));

        Mockito.when(racksRepository.findAll(pageable)).thenReturn(racksPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(rackss),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(racksDtos);

        List<RacksDto> response = racksService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByStatusTest() {
        Racks racks = new Racks();
        racks.setIdentifier("Admin");
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Admin");

        List<Racks> rackss = List.of(racks);
        List<RacksDto> racksDtos = List.of(racksDto);

        Mockito.when(racksRepository.findByStatusIsTrue()).thenReturn(rackss);
        Mockito.when(modelMapper.map(
                Mockito.eq(rackss),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(racksDtos);

        List<RacksDto> response = racksService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleTestActive() {

        Racks racks = new Racks();
        racks.setStatus(false);
        RacksDto racksDto = new RacksDto();
        racksDto.setStatus(true);
        Mockito.when(racksRepository.findByIdentifier("Admin")).thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class)).thenReturn(racksDto);
        RacksDto response = racksService.toggleStatus("Admin");
        Assertions.assertTrue(response.isStatus());

    }

    @Test
    void toggleTestInactive() {

        Racks racks = new Racks();
        racks.setStatus(true);
        RacksDto racksDto = new RacksDto();
        racksDto.setStatus(false);
        Mockito.when(racksRepository.findByIdentifier("Admin")).thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class)).thenReturn(racksDto);
        RacksDto response = racksService.toggleStatus("Admin");
        Assertions.assertFalse(response.isStatus());

    }
}