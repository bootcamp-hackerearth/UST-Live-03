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

    // SAVE

    @Test
    void save_Success() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Racks entity = new Racks();

        Mockito.when(racksRepository.findByIdentifierAndIsDeleteFalse("R1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Racks.class))
                .thenReturn(entity);

        Mockito.when(racksRepository.save(entity))
                .thenReturn(entity);

        RacksDto result = racksService.save(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(racksRepository).save(entity);
    }

    @Test
    void save_WhenExists_ShouldFail() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifierAndIsDeleteFalse("R1"))
                .thenReturn(new Racks());

        RacksDto result = racksService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    // UPDATE

    @Test
    void update_Success() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Racks existing = new Racks();
        existing.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifierAndIsDeleteFalse("R1"))
                .thenReturn(existing);

        Mockito.doNothing().when(modelMapper).map(dto, existing);

        Mockito.when(racksRepository.save(existing))
                .thenReturn(existing);

        RacksDto result = racksService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(racksRepository).save(existing);
    }

    @Test
    void update_WhenNotFound_ShouldFail() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifierAndIsDeleteFalse("R1"))
                .thenReturn(null);

        RacksDto result = racksService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Racks not found", result.getMessage());

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    // DELETE (SOFT DELETE)

    @Test
    void delete_Success() {

        Racks entity = new Racks();
        entity.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifierAndIsDeleteFalse("R1"))
                .thenReturn(entity);

        Mockito.when(racksRepository.save(entity)).thenReturn(entity);

        racksService.delete("R1");

        Assertions.assertTrue(entity.isDelete());

        Mockito.verify(racksRepository).save(entity);
    }

    @Test
    void delete_WhenNotFound_ShouldDoNothing() {

        Mockito.when(racksRepository.findByIdentifierAndIsDeleteFalse("R1"))
                .thenReturn(null);

        racksService.delete("R1");

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    // TOGGLE STATUS

    @Test
    void toggleStatus_Success() {

        Racks entity = new Racks();
        entity.setIdentifier("R1");
        entity.setStatus(true);

        Mockito.when(racksRepository.findByIdentifierAndIsDeleteFalse("R1"))
                .thenReturn(entity);

        Mockito.when(racksRepository.save(entity)).thenReturn(entity);

        racksService.toggleStatus("R1");

        Assertions.assertFalse(entity.getStatus());

        Mockito.verify(racksRepository).save(entity);
    }

    @Test
    void toggleStatus_WhenNotFound_ShouldDoNothing() {

        Mockito.when(racksRepository.findByIdentifierAndIsDeleteFalse("R1"))
                .thenReturn(null);

        racksService.toggleStatus("R1");

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    // FIND ALL (LIST)

    @Test
    void findAll_List_Success() {

        List<Racks> entities = List.of(new Racks());
        List<RacksDto> dtos = List.of(new RacksDto());

        Type type = new TypeToken<List<RacksDto>>() {
        }.getType();

        Mockito.when(racksRepository.findByIsDeleteFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, type))
                .thenReturn(dtos);

        List<RacksDto> result = racksService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // FIND BY IDENTIFIER

    @Test
    void findByIdentifier_Success() {

        Racks entity = new Racks();
        entity.setIdentifier("R1");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifierAndIsDeleteFalse("R1"))
                .thenReturn(entity);

        Mockito.when(modelMapper.map(entity, RacksDto.class))
                .thenReturn(dto);

        RacksDto result = racksService.findByIdentifier("R1");

        Assertions.assertEquals("R1", result.getIdentifier());
    }

    // PAGINATION (LIST VERSION)

    @Test
    void findAll_Pageable_ListVersion() {

        Pageable pageable = PageRequest.of(0, 10);

        Racks entity = new Racks();
        entity.setIdentifier("R1");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Page<Racks> page = new PageImpl<>(List.of(entity));

        Mockito.when(racksRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(page.getContent(), new TypeToken<List<RacksDto>>() {
                }.getType()))
                .thenReturn(List.of(dto));

        List<RacksDto> result = racksService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
    }

    // PAGINATION (SEARCH VERSION)

    @Test
    void findAll_WithSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Racks entity = new Racks();
        entity.setIdentifier("R1");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Page<Racks> page = new PageImpl<>(List.of(entity));

        Mockito.when(racksRepository
                        .findByIdentifierContainingIgnoreCaseAndIsDeleteFalse("R", pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entity, RacksDto.class))
                .thenReturn(dto);

        Page<RacksDto> result = racksService.findAll(pageable, "R");

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void findAll_WithBlankSearch_ShouldFallback() {

        Pageable pageable = PageRequest.of(0, 10);

        Racks entity = new Racks();
        entity.setIdentifier("R1");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Page<Racks> page = new PageImpl<>(List.of(entity));

        Mockito.when(racksRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entity, RacksDto.class))
                .thenReturn(dto);

        Page<RacksDto> result = racksService.findAll(pageable, " ");

        Assertions.assertEquals(1, result.getContent().size());
    }
}