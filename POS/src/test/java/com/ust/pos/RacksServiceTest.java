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
    void saveTest_Success() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        Racks entity = new Racks();
        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Racks.class))
                .thenReturn(entity);
        Mockito.when(racksRepository.save(entity))
                .thenReturn(entity);
        RacksDto response = racksService.save(dto);
        Assertions.assertEquals("R1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(racksRepository).save(entity);
    }

    @Test
    void saveTest_Failure_WhenAlreadyExists() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(new Racks());
        RacksDto response = racksService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        Racks existing = new Racks();
        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(existing);
        Mockito.doNothing()
                .when(modelMapper).map(dto, existing);
        Mockito.when(racksRepository.save(existing))
                .thenReturn(existing);
        RacksDto response = racksService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(racksRepository).save(existing);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(null);
        RacksDto response = racksService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
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
    void findAllTest() {
        List<Racks> entities = List.of(new Racks());
        List<RacksDto> dtos = List.of(new RacksDto());
        Type listType = new TypeToken<List<RacksDto>>() {}.getType();
        Mockito.when(racksRepository.findAll())
                .thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);
        List<RacksDto> response = racksService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest_WhenRackExists() {
        Racks racks = new Racks();
        racks.setStatus(false);
        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(racks);
        Mockito.when(racksRepository.save(racks))
                .thenReturn(racks);
        racksService.toggleStatus("R1");
        Assertions.assertTrue(racks.isStatus());
        Mockito.verify(racksRepository).save(racks);
    }

    @Test
    void toggleStatusTest_WhenRackNotFound() {
        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(null);
        racksService.toggleStatus("R1");
        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(racksRepository)
                .deleteByIdentifier("R1");
        racksService.delete("R1");
        Mockito.verify(racksRepository)
                .deleteByIdentifier("R1");
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
}