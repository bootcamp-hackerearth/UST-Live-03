package com.ust.pos;

import com.ust.pos.dto.PageDto;
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
        racksDto.setIdentifier("Rack1");

        Racks racks = new Racks();

        Mockito.when(racksRepository.findByIdentifier("Rack1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(racksDto, Racks.class))
                .thenReturn(racks);
        Mockito.when(racksRepository.save(racks))
                .thenReturn(racks);

        RacksDto response = racksService.save(racksDto);

        Assertions.assertEquals("Rack1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Rack1");

        Mockito.when(racksRepository.findByIdentifier("Rack1"))
                .thenReturn(new Racks());

        RacksDto response = racksService.save(racksDto);

        Assertions.assertEquals("Rack1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Racks racks = new Racks();
        racks.setIdentifier("Rack1");

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Rack1");

        Mockito.when(racksRepository.findByIdentifier("Rack1"))
                .thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(racksDto);

        RacksDto response = racksService.findByIdentifier("Rack1");

        Assertions.assertEquals("Rack1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Rack1");

        Racks existingRack = new Racks();
        existingRack.setIdentifier("Rack1");

        Mockito.when(racksRepository.findByIdentifier("Rack1"))
                .thenReturn(existingRack);
        Mockito.when(racksRepository.save(existingRack))
                .thenReturn(existingRack);

        RacksDto response = racksService.update(racksDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Rack1");

        Mockito.when(racksRepository.findByIdentifier("Rack1"))
                .thenReturn(null);

        RacksDto response = racksService.update(racksDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(racksRepository)
                .deleteByIdentifier("Rack1");

        racksService.delete("Rack1");

        Mockito.verify(racksRepository)
                .deleteByIdentifier("Rack1");
    }

    @Test
    void toggleStatusTest() {
        Racks racks = new Racks();
        racks.setIdentifier("Rack1");
        racks.setStatus(true);

        Mockito.when(racksRepository.findByIdentifier("Rack1"))
                .thenReturn(racks);
        Mockito.when(racksRepository.save(racks))
                .thenReturn(racks);

        racksService.toggleStatus("Rack1");

        Assertions.assertFalse(racks.getStatus());
        Mockito.verify(racksRepository).save(racks);
    }

    @Test
    void toggleStatusTestFailure() {
        Mockito.when(racksRepository.findByIdentifier("Rack1"))
                .thenReturn(null);

        racksService.toggleStatus("Rack1");

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {

        Racks racks = new Racks();
        racks.setIdentifier("Rack1");

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Rack1");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Racks> racksPage =
                new PageImpl<>(List.of(racks), pageable, 1);

        Mockito.when(racksRepository.findAll(pageable))
                .thenReturn(racksPage);

        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();

        Mockito.when(modelMapper.map(Mockito.eq(racksPage.getContent()), Mockito.eq(listType))).thenReturn(List.of(racksDto));

        PageDto<RacksDto> response = racksService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(1, response.getDtoList().size());

        Assertions.assertEquals("Rack1", response.getDtoList().get(0).getIdentifier());

        Assertions.assertEquals(1, response.getTotalRecords());

        Assertions.assertEquals(1, response.getTotalPages());

        Assertions.assertEquals(10, response.getSizePerPage());

        Assertions.assertEquals(0, response.getPage());
    }
}