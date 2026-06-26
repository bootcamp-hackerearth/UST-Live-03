package com.ust.pos;

import com.ust.pos.dto.PageDto;
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

@ExtendWith(MockitoExtension.class)
class RacksServiceTest {

    @InjectMocks
    private RacksServiceImpl racksService;

    @Mock
    private RacksRepository racksRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(null);

        Racks racks = new Racks();

        Mockito.when(modelMapper.map(dto, Racks.class))
                .thenReturn(racks);

        Mockito.when(racksRepository.save(racks))
                .thenReturn(racks);

        RacksDto response = racksService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("R1", response.getIdentifier());

        Mockito.verify(racksRepository).save(racks);
    }

    @Test
    void saveDuplicateTest() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Racks existing = new Racks();
        existing.setDeleted(false);

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(existing);

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
    void saveSoftDeletedRecordTest() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Racks existing = new Racks();
        existing.setDeleted(true);

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(existing);

        RacksDto response = racksService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Racks with identifier - R1 has been soft deleted. Restore it by changing status.",
                response.getMessage()
        );

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {

        Racks entity = new Racks();
        entity.setIdentifier("R1");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(entity);

        Mockito.when(modelMapper.map(entity, RacksDto.class))
                .thenReturn(dto);

        RacksDto response =
                racksService.findByIdentifier("R1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("R1", response.getIdentifier());
    }

    @Test
    void updateSuccessTest() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Racks entity = new Racks();
        entity.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(entity);

        Mockito.when(racksRepository.save(entity))
                .thenReturn(entity);

        RacksDto response =
                racksService.update(dto);

        Assertions.assertNotNull(response);

        Mockito.verify(modelMapper)
                .map(dto, entity);

        Mockito.verify(racksRepository)
                .save(entity);
    }

    @Test
    void updateFailureTest() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(null);

        RacksDto response =
                racksService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Racks with identifier - R1 not found",
                response.getMessage()
        );
    }

    @Test
    void deleteSoftDeleteTest() {

        Racks racks = new Racks();
        racks.setIdentifier("R1");
        racks.setDeleted(false);

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(racks);

        Mockito.when(racksRepository.save(racks))
                .thenReturn(racks);

        boolean result =
                racksService.delete("R1");

        Assertions.assertTrue(result);

        Assertions.assertTrue(racks.getDeleted());

        Mockito.verify(racksRepository)
                .save(racks);
    }

    @Test
    void deleteFailureTest() {

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(null);

        boolean result =
                racksService.delete("R1");

        Assertions.assertFalse(result);

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void toggleStatusTest() {

        Racks racks = new Racks();
        racks.setIdentifier("R1");
        racks.setStatus(true);

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(racks);

        Mockito.when(racksRepository.save(racks))
                .thenReturn(racks);

        racksService.toggleStatus("R1");

        Assertions.assertFalse(racks.getStatus());

        Mockito.verify(racksRepository)
                .save(racks);
    }

    @Test
    void toggleStatusFailureTest() {

        Mockito.when(racksRepository.findByIdentifier("R1"))
                .thenReturn(null);

        racksService.toggleStatus("R1");

        Mockito.verify(racksRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {

        Racks racks = new Racks();
        racks.setIdentifier("R1");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Racks> page =
                new PageImpl<>(List.of(racks), pageable, 1);

        Mockito.when(racksRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Type listType =
                new TypeToken<List<RacksDto>>() {
                }.getType();

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(page.getContent()),
                        Mockito.eq(listType)
                )
        ).thenReturn(List.of(dto));

        PageDto<RacksDto> response =
                racksService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("R1",
                response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findActiveRacksTest() {

        Racks racks = new Racks();
        racks.setIdentifier("R1");
        racks.setStatus(true);

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        List<Racks> racksList = List.of(racks);

        Type listType =
                new TypeToken<List<RacksDto>>() {
                }.getType();

        Mockito.when(racksRepository.findByStatusTrue())
                .thenReturn(racksList);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(racksList),
                        Mockito.eq(listType)
                )
        ).thenReturn(List.of(dto));

        List<RacksDto> response =
                racksService.findActiveRacks();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "R1",
                response.get(0).getIdentifier()
        );
    }
}