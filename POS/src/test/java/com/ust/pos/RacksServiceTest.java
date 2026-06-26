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
    void findByIdentifier_Found() {

        Racks racks = new Racks();
        racks.setIdentifier("R001");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R001");

        when(racksRepository.findByIdentifier("R001"))
                .thenReturn(racks);

        when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        RacksDto result = racksService.findByIdentifier("R001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("R001", result.getIdentifier());
    }

    @Test
    void save_NewRacks() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R001");

        Racks racks = new Racks();

        when(racksRepository.findByIdentifier("R001"))
                .thenReturn(null);

        when(modelMapper.map(dto, Racks.class))
                .thenReturn(racks);

        when(racksRepository.save(racks))
                .thenReturn(racks);

        RacksDto result = racksService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("R001", result.getIdentifier());
        verify(racksRepository).save(racks);
    }

    @Test
    void save_RacksAlreadyExists() {

        Racks existing = new Racks();

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R001");

        when(racksRepository.findByIdentifier("R001"))
                .thenReturn(existing);

        RacksDto result = racksService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(racksRepository, never()).save(any());
    }

    @Test
    void update_RacksExists() {

        Racks existing = new Racks();

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R001");

        when(racksRepository.findByIdentifier("R001"))
                .thenReturn(existing);

        when(racksRepository.save(existing))
                .thenReturn(existing);

        RacksDto result = racksService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("R001", result.getIdentifier());
        verify(modelMapper).map(dto, existing);
        verify(racksRepository).save(existing);
    }

    @Test
    void update_RacksNotFound() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R001");

        when(racksRepository.findByIdentifier("R001"))
                .thenReturn(null);

        RacksDto result = racksService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(racksRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        Racks racks = new Racks();

        when(racksRepository.findByIdentifier("R001"))
                .thenReturn(racks);

        racksService.delete("R001");

        verify(racksRepository).findByIdentifier("R001");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Racks racks1 = new Racks();
        Racks racks2 = new Racks();

        Page<Racks> page = new PageImpl<>(
                List.of(racks1, racks2),
                pageable,
                2
        );

        List<RacksDto> dtoList = List.of(new RacksDto(), new RacksDto());

        when(racksRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<RacksDto> result = racksService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(2, result.getTotalRecords());

        verify(racksRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Racks> page = new PageImpl<>(List.of(), pageable, 0);

        when(racksRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<RacksDto> result = racksService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalRecords());

        verify(racksRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void toggleStatus_Test() {

        Racks racks = new Racks();
        racks.setStatus(true);

        when(racksRepository.findByIdentifier("R001"))
                .thenReturn(racks);

        racksService.toggleStatus("R001");

        Assertions.assertFalse(racks.isStatus());
        verify(racksRepository).save(racks);
    }

    @Test
    void toggleStatus_NotFound() {

        when(racksRepository.findByIdentifier("R001"))
                .thenReturn(null);

        racksService.toggleStatus("R001");

        verify(racksRepository, never()).save(any());
    }
}
