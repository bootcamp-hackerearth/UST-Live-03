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

@ExtendWith(MockitoExtension.class)
class RacksServiceTest {

    @Mock
    private RacksRepository racksRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RacksServiceImpl racksService;

    @Test
    void findByIdentifierTestSuccess() {
        Racks racks = new Racks();
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("RCK01");

        Mockito.when(racksRepository.findByIdentifier("RCK01")).thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class)).thenReturn(racksDto);

        RacksDto response = racksService.findByIdentifier("RCK01");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("RCK01", response.getIdentifier());
    }

    @Test
    void findByIdentifierTestNotFoundException() {
        Mockito.when(racksRepository.findByIdentifier("RCK01")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            racksService.findByIdentifier("RCK01");
        });
    }

    @Test
    void toggleStatusTest() {
        Racks racks = new Racks();
        racks.setStatus(false);
        RacksDto racksDto = new RacksDto();
        racksDto.setStatus(true);

        Mockito.when(racksRepository.findByIdentifier("RCK01")).thenReturn(racks);
        Mockito.when(racksRepository.save(racks)).thenReturn(racks);
        Mockito.when(modelMapper.map(racks, RacksDto.class)).thenReturn(racksDto);

        RacksDto response = racksService.toggleStatus("RCK01");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void saveTestSuccess() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier(" RCK01 "); // Verifying trim block logic

        Mockito.when(racksRepository.findByIdentifier("RCK01")).thenReturn(null);
        Racks racks = new Racks();
        Mockito.when(modelMapper.map(racksDto, Racks.class)).thenReturn(racks);
        Mockito.when(racksRepository.save(racks)).thenReturn(racks);

        RacksDto response = racksService.save(racksDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("RCK01", response.getIdentifier());
    }

    @Test
    void saveTestFailureAlreadyExists() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("RCK01");

        Racks existingRacks = new Racks();
        existingRacks.setIdentifier("RCK01");
        existingRacks.setDeleted(false);

        Mockito.when(racksRepository.findByIdentifier("RCK01")).thenReturn(existingRacks);

        RacksDto response = racksService.save(racksDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Racks with identifier - RCK01 already exists", response.getMessage());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("RCK01");

        Racks existingRacks = new Racks();
        existingRacks.setIdentifier("RCK01");
        existingRacks.setDeleted(true);

        Mockito.when(racksRepository.findByIdentifier("RCK01")).thenReturn(existingRacks);

        RacksDto response = racksService.save(racksDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Racks with identifier RCK01 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void updateTestSuccess() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("RCK01");

        Racks existingRacks = new Racks();
        existingRacks.setIdentifier("RCK01");

        Mockito.when(racksRepository.findByIdentifier("RCK01")).thenReturn(existingRacks);
        Mockito.when(racksRepository.save(existingRacks)).thenReturn(existingRacks);

        RacksDto response = racksService.update(racksDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("RCK01", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {
        RacksDto racksDto = new RacksDto();
        racksDto.setIdentifier("RCK01");

        Mockito.when(racksRepository.findByIdentifier("RCK01")).thenReturn(null);

        RacksDto response = racksService.update(racksDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Racks with identifier - RCK01 not found", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        Racks racks = new Racks();

        Mockito.when(racksRepository.findByIdentifier("RCK01")).thenReturn(racks);
        Mockito.when(racksRepository.save(racks)).thenReturn(racks);

        boolean response = racksService.delete("RCK01");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(racksRepository.findByIdentifier("RCK01")).thenReturn(null);

        boolean response = racksService.delete("RCK01");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Racks racks = new Racks();
        List<Racks> racksList = List.of(racks);
        Page<Racks> racksPage = new PageImpl<>(racksList, pageable, racksList.size());

        RacksDto racksDto = new RacksDto();
        List<RacksDto> racksDtos = List.of(racksDto);

        Mockito.when(racksRepository.findByDeletedFalse(pageable)).thenReturn(racksPage);
        Mockito.when(modelMapper.map(Mockito.eq(racksList), Mockito.any(Type.class))).thenReturn(racksDtos);

        WsDto<RacksDto> response = racksService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findIfTrueTest() {
        Racks racks = new Racks();
        List<Racks> racksList = List.of(racks);
        RacksDto racksDto = new RacksDto();
        List<RacksDto> racksDtos = List.of(racksDto);

        Mockito.when(racksRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(racksList);
        Mockito.when(modelMapper.map(Mockito.eq(racksList), Mockito.any(Type.class))).thenReturn(racksDtos);

        List<RacksDto> response = racksService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Racks> specification = Mockito.mock(Specification.class);
        Racks racks = new Racks();
        List<Racks> racksList = List.of(racks);
        Page<Racks> page = new PageImpl<>(racksList, pageable, racksList.size());

        RacksDto racksDto = new RacksDto();
        List<RacksDto> racksDtos = List.of(racksDto);

        Mockito.when(racksRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(racksList), Mockito.any(Type.class))).thenReturn(racksDtos);

        WsDto<RacksDto> response = racksService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}