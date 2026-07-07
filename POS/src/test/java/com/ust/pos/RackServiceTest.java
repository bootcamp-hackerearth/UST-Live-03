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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

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

        Mockito.when(
                racksRepository.findByIdentifierAndDeletedFalse("R1")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(dto, Racks.class)
        ).thenReturn(racks);

        RacksDto response = racksService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(racksRepository).save(racks);

        Assertions.assertFalse(racks.getDeleted());
    }

    @Test
    void saveRacksAlreadyExists() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(
                racksRepository.findByIdentifierAndDeletedFalse("R1")
        ).thenReturn(new Racks());

        RacksDto response = racksService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Racks with identifier - R1 already exists",
                response.getMessage()
        );

        Mockito.verify(
                racksRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void updateRacksSuccess() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");
        dto.setDescription("Upper Rack");
        dto.setStatus(true);

        Racks racks = new Racks();

        Mockito.when(
                racksRepository.findByIdentifierAndDeletedFalse("R1")
        ).thenReturn(racks);

        RacksDto response = racksService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(modelMapper)
                .map(dto, racks);

        Mockito.verify(racksRepository)
                .save(racks);
    }

    @Test
    void updateRacksNotFound() {

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(
                racksRepository.findByIdentifierAndDeletedFalse("R1")
        ).thenReturn(null);

        RacksDto response = racksService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Racks not found",
                response.getMessage()
        );

        Mockito.verify(
                racksRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void findAllRacksTest() {

        List<Racks> racksList =
                List.of(new Racks(), new Racks());

        List<RacksDto> dtoList =
                List.of(new RacksDto(), new RacksDto());

        Mockito.when(
                racksRepository.findByDeletedFalse()
        ).thenReturn(racksList);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(racksList),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<RacksDto> response =
                racksService.findAll();

        Assertions.assertEquals(
                2,
                response.size()
        );
    }

    @Test
    void findRacksByIdentifierTest() {

        Racks racks = new Racks();
        racks.setIdentifier("R1");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("R1");

        Mockito.when(
                racksRepository.findByIdentifierAndDeletedFalse("R1")
        ).thenReturn(racks);

        Mockito.when(
                modelMapper.map(racks, RacksDto.class)
        ).thenReturn(dto);

        RacksDto response =
                racksService.findByIdentifier("R1");

        Assertions.assertEquals(
                "R1",
                response.getIdentifier()
        );
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Racks> racks =
                List.of(new Racks());

        Page<Racks> page =
                new PageImpl<>(racks, pageable, 1);

        List<RacksDto> dtoList =
                List.of(new RacksDto());

        Type listType =
                new TypeToken<List<RacksDto>>() {}.getType();

        Mockito.when(
                racksRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(racks, listType)
        ).thenReturn(dtoList);

        WsDto<RacksDto> response =
                racksService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void findAllSearchTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Racks racks = new Racks();
        racks.setIdentifier("R1");

        Example<Racks> example = Example.of(
                racks,
                ExampleMatcher.matching()
                        .withMatcher(
                                "identifier",
                                ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase()
                        )
        );

        Page<Racks> page =
                new PageImpl<>(List.of(racks));

        Mockito.when(
                racksRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        racks,
                        RacksDto.class
                )
        ).thenReturn(new RacksDto());

        Page<RacksDto> response =
                racksService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void findAllWithoutSearchTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Racks racks = new Racks();

        Example<Racks> example = Example.of(new Racks());

        Page<Racks> page =
                new PageImpl<>(List.of(racks));

        Mockito.when(
                racksRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        racks,
                        RacksDto.class
                )
        ).thenReturn(new RacksDto());

        Page<RacksDto> response =
                racksService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void deleteRacksTest() {

        Racks racks = new Racks();
        racks.setDeleted(false);

        Mockito.when(
                racksRepository.findByIdentifierAndDeletedFalse("R1")
        ).thenReturn(racks);

        racksService.delete("R1");

        Assertions.assertTrue(
                racks.getDeleted()
        );

        Mockito.verify(racksRepository)
                .save(racks);
    }

    @Test
    void deleteRacksNotFoundTest() {

        Mockito.when(
                racksRepository.findByIdentifierAndDeletedFalse("R1")
        ).thenReturn(null);

        racksService.delete("R1");

        Mockito.verify(
                racksRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void toggleRacksStatusSuccess() {

        Racks racks = new Racks();
        racks.setStatus(true);

        Mockito.when(
                racksRepository.findByIdentifierAndDeletedFalse("R1")
        ).thenReturn(racks);

        racksService.toggleStatus("R1");

        Assertions.assertFalse(
                racks.getStatus()
        );

        Mockito.verify(racksRepository)
                .save(racks);
    }

    @Test
    void toggleRacksStatusNotFound() {

        Mockito.when(
                racksRepository.findByIdentifierAndDeletedFalse("R1")
        ).thenReturn(null);

        racksService.toggleStatus("R1");

        Mockito.verify(
                racksRepository,
                Mockito.never()
        ).save(Mockito.any());
    }
}