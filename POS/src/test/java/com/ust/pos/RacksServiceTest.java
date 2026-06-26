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
import org.springframework.data.domain.*;

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
        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
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
        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(new Racks());
        RacksDto response = racksService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(racksRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        dto.setStatus(true);
        Racks existing = new Racks();
        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
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
        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(null);
        RacksDto response = racksService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Racks not found", response.getMessage());
        Mockito.verify(racksRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Racks racks = new Racks();
        racks.setIdentifier("R1");
        racks.setDeleted(false);
        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(racks);
        racksService.delete("R1");
        Assertions.assertTrue(racks.isDeleted());
        Mockito.verify(racksRepository).save(racks);
    }

    @Test
    void deleteTest_WhenNotFound() {
        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(null);
        racksService.delete("R1");
        Mockito.verify(racksRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllTest() {

        List<Racks> entities = List.of(new Racks());
        List<RacksDto> dtos = List.of(new RacksDto());

        java.lang.reflect.Type listType =
                new org.modelmapper.TypeToken<List<RacksDto>>() {}.getType();

        Mockito.when(racksRepository.findByDeletedFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<RacksDto> response = racksService.findAll();

        Assertions.assertEquals(1, response.size());

        Mockito.verify(racksRepository).findByDeletedFalse();
    }

    // ================= FIND BY ID =================
    @Test
    void findByIdentifierTest() {
        Racks entity = new Racks();
        entity.setIdentifier("R1");
        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(entity);
        Mockito.when(modelMapper.map(entity, RacksDto.class))
                .thenReturn(dto);
        RacksDto response = racksService.findByIdentifier("R1");
        Assertions.assertEquals("R1", response.getIdentifier());
    }

    @Test
    void toggleStatusTest_WhenExists() {
        Racks racks = new Racks();
        racks.setStatus(false);

        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(racks);

        Mockito.when(racksRepository.save(racks))
                .thenReturn(racks);

        racksService.toggleStatus("R1");

        Assertions.assertTrue(racks.isStatus());

        Mockito.verify(racksRepository).save(racks);
    }

    @Test
    void toggleStatusTest_WhenNotFound() {
        Mockito.when(racksRepository.findByIdentifierAndDeletedFalse("R1"))
                .thenReturn(null);

        racksService.toggleStatus("R1");

        Mockito.verify(racksRepository, Mockito.never()).save(Mockito.any());
    }

    // ================= PAGINATION + SEARCH =================
    @Test
    void findAll_WithPagination_NoSearch() {
        Pageable pageable = PageRequest.of(0, 10);

        Racks racks = new Racks();
        RacksDto dto = new RacksDto();

        Page<Racks> page = new PageImpl<>(List.of(racks));

        Mockito.when(racksRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        Page<RacksDto> response = racksService.findAll(pageable, null);

        Assertions.assertEquals(1, response.getContent().size());

        Mockito.verify(racksRepository).findByDeletedFalse(pageable);
    }

    @Test
    void findAll_WithPagination_WithSearch() {
        Pageable pageable = PageRequest.of(0, 10);

        Racks racks = new Racks();
        RacksDto dto = new RacksDto();

        Page<Racks> page = new PageImpl<>(List.of(racks));

        Mockito.when(racksRepository
                        .findByIdentifierContainingIgnoreCaseAndDeletedFalse("R1", pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        Page<RacksDto> response = racksService.findAll(pageable, "R1");

        Assertions.assertEquals(1, response.getContent().size());

        Mockito.verify(racksRepository)
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse("R1", pageable);
    }
}