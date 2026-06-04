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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    void findAll_success() {

        Racks racks = new Racks();
        RacksDto dto = new RacksDto();

        Page<Racks> page = new PageImpl<>(List.of(racks));

        Mockito.when(racksRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(racks)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<RacksDto> result =
                racksService.findAll(Mockito.mock(Pageable.class));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void save_success() {

        RacksDto input = new RacksDto();
        input.setIdentifier("RACK01");

        Mockito.when(racksRepository.findByIdentifier("RACK01"))
                .thenReturn(null);

        Racks entity = new Racks();

        Mockito.when(modelMapper.map(input, Racks.class))
                .thenReturn(entity);

        Mockito.when(racksRepository.save(entity))
                .thenReturn(entity);

        RacksDto result = racksService.save(input);

        Assertions.assertEquals("RACK01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        RacksDto input = new RacksDto();
        input.setIdentifier("RACK01");

        Mockito.when(racksRepository.findByIdentifier("RACK01"))
                .thenReturn(new Racks());

        RacksDto result = racksService.save(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Racks racks = new Racks();
        racks.setIdentifier("RACK01");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK01");

        Mockito.when(racksRepository.findByIdentifier("RACK01"))
                .thenReturn(racks);

        Mockito.when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        RacksDto result =
                racksService.findByIdentifier("RACK01");

        Assertions.assertEquals("RACK01", result.getIdentifier());
    }

    @Test
    void update_success() {

        RacksDto input = new RacksDto();
        input.setIdentifier("RACK01");

        Racks existing = new Racks();

        Mockito.when(racksRepository.findByIdentifier("RACK01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(racksRepository.save(existing))
                .thenReturn(existing);

        RacksDto result = racksService.update(input);

        Assertions.assertEquals("RACK01", result.getIdentifier());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(racksRepository).deleteByIdentifier("RACK01");

        racksService.delete("RACK01");

        Mockito.verify(racksRepository)
                .deleteByIdentifier("RACK01");
    }

    @Test
    void changeToggleStatus_enable() {

        Racks racks = new Racks();
        racks.setStatus(false);

        RacksDto dto = new RacksDto();

        Mockito.when(racksRepository.findByIdentifier("RACK01"))
                .thenReturn(racks);

        Mockito.when(racksRepository.save(racks))
                .thenReturn(racks);

        Mockito.when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        RacksDto result =
                racksService.changeToggleStatus("RACK01", true);

        Assertions.assertTrue(racks.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Racks racks = new Racks();
        racks.setStatus(true);

        RacksDto dto = new RacksDto();

        Mockito.when(racksRepository.findByIdentifier("RACK01"))
                .thenReturn(racks);

        Mockito.when(racksRepository.save(racks))
                .thenReturn(racks);

        Mockito.when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        RacksDto result =
                racksService.changeToggleStatus("RACK01", false);

        Assertions.assertFalse(racks.isStatus());
        Assertions.assertNotNull(result);
    }
}