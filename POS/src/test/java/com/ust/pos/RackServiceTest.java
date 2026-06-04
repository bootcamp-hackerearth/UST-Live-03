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
class RackServiceTest {

    @InjectMocks
    private RacksServiceImpl racksService;

    @Mock
    private RacksRepository racksRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveRacksSuccess() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Racks racks = new Racks();

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Racks.class))
                .thenReturn(racks);

        RacksDto response = racksService.save(dto);

        Assertions.assertNull(response.getMessage());
        Mockito.verify(racksRepository).save(racks);
    }
    @Test
    void findAll_WithPagination_ShouldReturnRacksDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Racks> racks = List.of(new Racks());
        Page<Racks> page = new PageImpl<>(racks);

        List<RacksDto> racksDtos = List.of(new RacksDto());

        Type listType = new TypeToken<List<RacksDto>>() {}.getType();

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

    @Test
    void saveRacksAlreadyExists() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(new Racks());

        RacksDto response = racksService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Racks with identifier - R1 already exists",
                response.getMessage()
        );

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateRacksSuccess() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        dto.setDescription("Upper Rack");
        dto.setStatus(true);

        Racks racks = new Racks();

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(racks);

        RacksDto response = racksService.update(dto);

        Assertions.assertNull(response.getMessage());
        Assertions.assertEquals("Upper Rack", racks.getDescription());
        Assertions.assertTrue(racks.getStatus());

        Mockito.verify(modelMapper).map(dto, racks);
        Mockito.verify(racksRepository).save(racks);
    }

    @Test
    void updateRacksNotFound() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(null);

        RacksDto response = racksService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Racks not found", response.getMessage());

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteRacksTest() {
        racksService.delete("R1");

        Mockito.verify(racksRepository).deleteByIdentifier("R1");
        Mockito.verifyNoMoreInteractions(racksRepository);
    }

    @Test
    void findAllRacksTest() {
        List<Racks> racksList = List.of(new Racks(), new Racks());
        List<RacksDto> dtoList = List.of(new RacksDto(), new RacksDto());

        Mockito.when(racksRepository.findAll())
                .thenReturn(racksList);
        Mockito.when(modelMapper.map(
                Mockito.eq(racksList),
                Mockito.any(Type.class))
        ).thenReturn(dtoList);

        List<RacksDto> response = racksService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findRacksByIdentifierTest() {
        Racks racks = new Racks();
        racks.setIdentifier("R1");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        RacksDto response = racksService.findByIdentifier("R1");

        Assertions.assertEquals("R1", response.getIdentifier());
    }

    @Test
    void toggleRacksStatusSuccess() {
        Racks racks = new Racks();
        racks.setStatus(Boolean.TRUE);

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(racks);

        racksService.toggleStatus("R1");

        Assertions.assertFalse(racks.getStatus());
        Mockito.verify(racksRepository).save(racks);
    }

    @Test
    void toggleRacksStatusNotFound() {
        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(null);

        racksService.toggleStatus("R1");

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }
}
