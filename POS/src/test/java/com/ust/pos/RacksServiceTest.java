package com.ust.pos;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

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
        verify(racksRepository).save(racks);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("RACK1");

        Racks existingRacks = new Racks();
        existingRacks.setDeleted(false);

        Mockito.when(racksRepository.findByIdentifier("RACK1")).thenReturn(existingRacks);

        RacksDto response = racksService.save(racksDto);

        Assertions.assertEquals("RACK1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Racks with identifier - RACK1 already exists", response.getMessage());
        Mockito.verify(racksRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureAlreadyDeletedTest() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("RACK1");

        Racks existingRacks = new Racks();
        existingRacks.setDeleted(true);

        Mockito.when(racksRepository.findByIdentifier("RACK1")).thenReturn(existingRacks);

        RacksDto response = racksService.save(racksDto);

        Assertions.assertEquals("RACK1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Racks with identifier - RACK1 was deleted , Please Contact the Administrator to add.", response.getMessage());
        Mockito.verify(racksRepository, Mockito.never()).save(Mockito.any());
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
        verify(modelMapper).map(racksDto, existingRacks);
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
        Assertions.assertEquals("Racks with identifier - RACK1 not found", response.getMessage());
        Mockito.verify(racksRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK1");

        Mockito.when(racksRepository.findByIdentifier("RACK1")).thenReturn(racks);

        racksService.delete("RACK1");

        verify(racksRepository).findByIdentifier("RACK1");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK1");

        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("RACK1");

        Mockito.when(racksRepository.findByIdentifierAndIsDeletedFalse("RACK1")).thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class)).thenReturn(racksDto);

        RacksDto response = racksService.findByIdentifier("RACK1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("RACK1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(racksRepository.findByIdentifierAndIsDeletedFalse("RACK1")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            racksService.findByIdentifier("RACK1");
        });
    }

    @Test
    void findAllSuccessTest() {
        Racks r1 = new Racks();
        r1.setIdentifier("RACK1");

        List<Racks> racksList = List.of(r1);

        RacksDto d1 = new RacksDto();
        d1.setIdentifier("RACK1");

        List<RacksDto> racksDtos = List.of(d1);

        Page<Racks> page = new PageImpl<>(racksList, PageRequest.of(0, 20), 1);
        Pageable pageable = PageRequest.of(0, 20);

        Mockito.when(racksRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(racksList), Mockito.any(Type.class))).thenReturn(racksDtos);

        WsDto<RacksDto> result = racksService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(20, result.getSizePerPage());
    }

    @Test
    void findAllActiveSuccessTest() {
        Racks r1 = new Racks();
        r1.setStatus(true);

        List<Racks> activeRacks = List.of(r1);

        RacksDto d1 = new RacksDto();
        d1.setStatus(true);

        List<RacksDto> racksDtos = List.of(d1);

        Mockito.when(racksRepository.findByStatus(true)).thenReturn(activeRacks);
        Mockito.when(modelMapper.map(Mockito.eq(activeRacks), Mockito.any(Type.class))).thenReturn(racksDtos);

        List<RacksDto> result = racksService.findAllActive();

        Assertions.assertEquals(1, result.size());
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

    @Test
    void findAllSpecificationSuccessTest() {
        Racks racks = new Racks();
        List<Racks> racksList = List.of(racks);

        RacksDto dto = new RacksDto();
        List<RacksDto> racksDtos = List.of(dto);

        Page<Racks> page = new PageImpl<>(racksList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Racks> specification = Mockito.mock(Specification.class);

        Mockito.when(racksRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(racksList), Mockito.any(Type.class))).thenReturn(racksDtos);

        WsDto<RacksDto> result = racksService.findAll(specification, pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }
}