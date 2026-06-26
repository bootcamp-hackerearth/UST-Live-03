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
    void saveTestSuccess() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Racks racks = new Racks();

        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Racks.class))
                .thenReturn(racks);
        Mockito.when(racksRepository.save(racks))
                .thenReturn(racks);

        RacksDto response = racksService.save(dto);

        Assertions.assertEquals("R1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(racksRepository).save(racks);
    }

    @Test
    void saveTestFailureWhenExists() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(new Racks());

        RacksDto response = racksService.save(dto);

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

        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        RacksDto response = racksService.findByIdentifier("R1");

        Assertions.assertEquals("R1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNullTest() {
        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(null, RacksDto.class))
                .thenReturn(null);

        RacksDto response = racksService.findByIdentifier("R1");

        Assertions.assertNull(response);
    }

    @Test
    void updateTestSuccess() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Racks existing = new Racks();

        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(existing);

        RacksDto response = racksService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper).map(dto, existing);
        Mockito.verify(racksRepository).save(existing);
    }

    @Test
    void updateTestFailureWhenNotFound() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(null);

        RacksDto response = racksService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Racks racks = new Racks();
        racks.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(racks);

        racksService.delete("R1");

        Assertions.assertTrue(racks.isDeleted());

        Mockito.verify(racksRepository).save(racks);
    }

    @Test
    void deleteTestWhenNotFound() {
        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(null);

        racksService.delete("R1");

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllTest() {
        List<Racks> racks = List.of(new Racks());
        List<RacksDto> dtos = List.of(new RacksDto());

        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();

        Mockito.when(racksRepository.findByDeletedFalse())
                .thenReturn(racks);
        Mockito.when(modelMapper.map(racks, listType))
                .thenReturn(dtos);

        List<RacksDto> response = racksService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        Racks racks = new Racks();
        racks.setStatus(false);

        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(racks);

        racksService.toggleStatus("R1");

        Assertions.assertTrue(racks.isStatus());

        Mockito.verify(racksRepository).save(racks);
    }

    @Test
    void toggleStatusNotFoundTest() {
        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(null);

        racksService.toggleStatus("R1");

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllWithPaginationShouldReturnRacksDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Racks racks = new Racks();
        racks.setIdentifier("R1");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Page<Racks> page = new PageImpl<>(List.of(racks));

        Mockito.when(racksRepository.findByDeletedFalse(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        Page<RacksDto> response = racksService.findAll(pageable, null);

        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals("R1", response.getContent().get(0).getIdentifier());

        Mockito.verify(racksRepository).findByDeletedFalse(pageable);
    }

    @Test
    void findAllWithSearchShouldReturnRacksDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Racks racks = new Racks();
        racks.setIdentifier("R1");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Page<Racks> page = new PageImpl<>(List.of(racks));

        Mockito.when(racksRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse("R1", pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        Page<RacksDto> response = racksService.findAll(pageable, "R1");

        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals("R1", response.getContent().get(0).getIdentifier());

        Mockito.verify(racksRepository)
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse("R1", pageable);
    }
}