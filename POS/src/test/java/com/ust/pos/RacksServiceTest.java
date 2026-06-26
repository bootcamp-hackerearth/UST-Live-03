package com.ust.pos;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    void findByIdentifierTest() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK001");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK001");

        when(racksRepository.findByIdentifier("RACK001"))
                .thenReturn(racks);

        when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        RacksDto result = racksService.findByIdentifier("RACK001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("RACK001", result.getIdentifier());
    }

    @Test
    void saveSuccessTest() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK001");

        Racks racks = new Racks();

        when(racksRepository.findByIdentifier("RACK001"))
                .thenReturn(null);

        when(modelMapper.map(dto, Racks.class))
                .thenReturn(racks);

        RacksDto result = racksService.save(dto);

        Assertions.assertEquals("RACK001", result.getIdentifier());

        verify(racksRepository).save(racks);
    }

    @Test
    void saveAlreadyExistsTest() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK001");

        Racks existingRacks = new Racks();
        existingRacks.setDeleted(false);

        when(racksRepository.findByIdentifier("RACK001"))
                .thenReturn(existingRacks);

        RacksDto result = racksService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Racks with identifier - RACK001 already exists",
                result.getMessage()
        );

        verify(racksRepository, never()).save(any());
    }

    @Test
    void saveDeletedRacksTest() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK001");

        Racks existingRacks = new Racks();
        existingRacks.setDeleted(true);

        when(racksRepository.findByIdentifier("RACK001"))
                .thenReturn(existingRacks);

        RacksDto result = racksService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Racks with identifier - RACK001 was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(racksRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK001");

        Racks existingRacks = new Racks();
        existingRacks.setIdentifier("RACK001");

        when(racksRepository.findByIdentifier("RACK001"))
                .thenReturn(existingRacks);

        RacksDto result = racksService.update(dto);

        Assertions.assertEquals("RACK001", result.getIdentifier());

        verify(modelMapper).map(dto, existingRacks);
        verify(racksRepository).save(existingRacks);
    }

    @Test
    void updateFailureTest() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK001");

        when(racksRepository.findByIdentifier("RACK001"))
                .thenReturn(null);

        RacksDto result = racksService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Racks with identifier - RACK001 not found",
                result.getMessage()
        );

        verify(racksRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Racks racks = new Racks();

        when(racksRepository.findByIdentifier("RACK001"))
                .thenReturn(racks);

        racksService.delete("RACK001");

        verify(racksRepository).findByIdentifier("RACK001");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Racks racks1 = new Racks();
        Racks racks2 = new Racks();

        List<Racks> racksList = List.of(
                racks1,
                racks2
        );

        Page<Racks> page = new PageImpl<>(
                racksList,
                pageable,
                2
        );

        List<RacksDto> dtoList = List.of(
                new RacksDto(),
                new RacksDto()
        );

        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();

        when(racksRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(racksList, listType))
                .thenReturn(dtoList);

        WsDto<RacksDto> result = racksService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Racks racks = new Racks();
        racks.setStatus(true);

        when(racksRepository.findByIdentifier("RACK001"))
                .thenReturn(racks);

        racksService.toggleStatus("RACK001");

        Assertions.assertFalse(racks.getStatus());

        verify(racksRepository).save(racks);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Racks racks = new Racks();
        racks.setStatus(false);

        when(racksRepository.findByIdentifier("RACK001"))
                .thenReturn(racks);

        racksService.toggleStatus("RACK001");

        Assertions.assertTrue(racks.getStatus());

        verify(racksRepository).save(racks);
    }

    @Test
    void toggleStatusRacksNotFoundTest() {
        when(racksRepository.findByIdentifier("RACK001"))
                .thenReturn(null);

        racksService.toggleStatus("RACK001");

        verify(racksRepository, never()).save(any());
    }
}