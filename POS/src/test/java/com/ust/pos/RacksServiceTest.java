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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RacksServiceTest {

    @InjectMocks
    private RacksServiceImpl racksService;

    @Mock
    private RacksRepository racksRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifier("R1")).thenReturn(null);
        Racks racks = new Racks();
        Mockito.when(modelMapper.map(racksDto, Racks.class)).thenReturn(racks);
        Mockito.when(racksRepository.save(racks)).thenReturn(racks);
        RacksDto response = racksService.save(racksDto);

        Assertions.assertEquals("R1", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("R1");
        Racks racks = new Racks();

        Mockito.when(racksRepository.findByIdentifier("R1")).thenReturn(racks);
        RacksDto response = racksService.save(racksDto);

        Assertions.assertEquals("R1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Racks racks = new Racks();
        racks.setIdentifier("R1");

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifier("R1")).thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class)).thenReturn(racksDto);

        RacksDto response = racksService.findByIdentifier("R1");

        Assertions.assertEquals("R1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("R1");

        Racks existingRacks = new Racks();
        existingRacks.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(existingRacks);
        Mockito.when(racksRepository.save(existingRacks))
                .thenReturn(existingRacks);

        RacksDto response = racksService.update(racksDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(null);

        RacksDto response = racksService.update(racksDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(racksRepository)
                .deleteByIdentifier("R1");

        racksService.delete("R1");

        Mockito.verify(racksRepository).deleteByIdentifier("R1");
    }

    @Test
    void findAllTest() {
        Racks racks = new Racks();
        racks.setIdentifier("R1");

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("R1");

        List<Racks> rackss = List.of(racks);
        List<RacksDto> racksDtos = List.of(racksDto);

        Mockito.when(racksRepository.findAll()).thenReturn(rackss);
        Mockito.when(modelMapper.map(
                Mockito.eq(rackss),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(racksDtos);

        List<RacksDto> response = racksService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        Racks racks = new Racks();
        racks.setIdentifier("R1");
        racks.setStatus(false);

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(racks);

        racksService.toggleStatus("R1");

        Assertions.assertTrue(racks.isStatus());

        Mockito.verify(racksRepository).save(racks);
    }

    @Test
    void toggleStatusNotFoundTest() {
        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(null);

        racksService.toggleStatus("R1");

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllWithPaginationShouldReturnRacksDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Racks> racks = List.of(new Racks());
        Page<Racks> page = new PageImpl<>(racks);

        List<RacksDto> racksDtos = List.of(new RacksDto());

        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();

        Mockito.when(racksRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(racks, listType))
                .thenReturn(racksDtos);

        List<RacksDto> response = racksService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(racksRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(racks, listType);
    }
}