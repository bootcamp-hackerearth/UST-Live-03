package com.ust.pos;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.model.Racks;
import com.ust.pos.model.RacksRepository;
import com.ust.pos.racks.service.impl.RacksServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RacksServiceTest {

    @Mock
    private RacksRepository racksRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RacksServiceImpl racksService;

    @Test
    void saveTestSuccess() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Rack");

        Racks racks = new Racks();

        Mockito.when(racksRepository.findByIdentifier("Rack")).thenReturn(null);
        Mockito.when(modelMapper.map(racksDto, Racks.class)).thenReturn(racks);

        RacksDto response = racksService.save(racksDto);

        Assertions.assertEquals("Rack", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Rack");

        Racks racks = new Racks();
        racks.setIdentifier("Rack");

        Mockito.when(racksRepository.findByIdentifier("Rack")).thenReturn(racks);

        RacksDto response = racksService.save(racksDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Racks racks = new Racks();
        racks.setIdentifier("Rack");

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Rack");

        Mockito.when(racksRepository.findByIdentifier("Rack")).thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class)).thenReturn(racksDto);

        RacksDto response = racksService.findByIdentifier("Rack");

        Assertions.assertEquals("Rack", response.getIdentifier());
    }

    @Test
    void updateTest() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Rack");

        Racks racks = new Racks();
        racks.setIdentifier("Rack");

        Mockito.when(racksRepository.findByIdentifier("Rack")).thenReturn(racks);
        Mockito.when(racksRepository.save(racks)).thenReturn(racks);

        RacksDto response = racksService.update(racksDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Rack");

        Mockito.when(racksRepository.findByIdentifier("Rack")).thenReturn(null);

        RacksDto response = racksService.update(racksDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(racksRepository).deleteById(1L);

        racksService.deleteById(1L);

        Mockito.verify(racksRepository, times(1)).deleteById(1L);
    }

    @Test
    void findAllTest() {
        Racks racks = new Racks();
        racks.setIdentifier("Rack");

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Rack");

        Page<Racks> page = new PageImpl<>(List.of(racks));

        Mockito.when(racksRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(racksDto));

        Pageable pageable = PageRequest.of(0, 50, Sort.unsorted());
        List<RacksDto> response = racksService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Rack", response.get(0).getIdentifier());
    }

    @Test
    void findByIdSuccessTest() {
        Racks racks = new Racks();
        racks.setIdentifier("Rack");

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Rack");

        Mockito.when(racksRepository.findById(1L))
                .thenReturn(java.util.Optional.of(racks));
        Mockito.when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(racksDto);

        RacksDto response = racksService.findById(1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Rack", response.getIdentifier());
    }

    @Test
    void findByIdFailureTest() {
        Mockito.when(racksRepository.findById(1L))
                .thenReturn(java.util.Optional.empty());

        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> racksService.findById(1L)
        );

        Assertions.assertEquals("Racks not found with id 1", exception.getMessage());
    }

    @Test
    void changeRacksStatusSuccessTest() {
        Racks racks = new Racks();
        racks.setIdentifier("Rack");
        racks.setStatus(false);

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("Rack");
        racksDto.setStatus(true);

        Mockito.when(racksRepository.findByIdentifier("Rack")).thenReturn(racks);
        Mockito.when(racksRepository.save(racks)).thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class)).thenReturn(racksDto);

        RacksDto response = racksService.changeRacksStatus("Rack", true);

        Assertions.assertEquals("Rack", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void changeRacksStatusFailureTest() {
        Mockito.when(racksRepository.findByIdentifier("Rack")).thenReturn(null);

        RacksDto response = racksService.changeRacksStatus("Rack", true);

        Assertions.assertNull(response);
        Mockito.verify(racksRepository, Mockito.never()).save(Mockito.any());
    }
}