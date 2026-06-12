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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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
        Pageable pageable = Mockito.mock(Pageable.class);

        Page<Racks> page = new PageImpl<>(List.of(racks));

        when(racksRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(
                        Mockito.eq(List.of(racks)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<RacksDto> result =
                racksService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());    }

    @Test
    void save_success() {

        RacksDto input = new RacksDto();
        input.setIdentifier("RACK01");

        when(racksRepository.findByIdentifier("RACK01"))
                .thenReturn(null);

        Racks entity = new Racks();

        when(modelMapper.map(input, Racks.class))
                .thenReturn(entity);

        when(racksRepository.save(entity))
                .thenReturn(entity);

        RacksDto result = racksService.save(input);

        assertEquals("RACK01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        RacksDto input = new RacksDto();
        input.setIdentifier("RACK01");

        when(racksRepository.findByIdentifier("RACK01"))
                .thenReturn(new Racks());

        RacksDto result = racksService.save(input);

        Assertions.assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Racks racks = new Racks();
        racks.setIdentifier("RACK01");

        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK01");

        when(racksRepository.findByIdentifier("RACK01"))
                .thenReturn(racks);

        when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        RacksDto result =
                racksService.findByIdentifier("RACK01");

        assertEquals("RACK01", result.getIdentifier());
    }

    @Test
    void update_success() {

        RacksDto input = new RacksDto();
        input.setIdentifier("RACK01");

        Racks existing = new Racks();

        when(racksRepository.findByIdentifier("RACK01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        when(racksRepository.save(existing))
                .thenReturn(existing);

        RacksDto result = racksService.update(input);

        assertEquals("RACK01", result.getIdentifier());
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

        when(racksRepository.findByIdentifier("RACK01"))
                .thenReturn(racks);

        when(racksRepository.save(racks))
                .thenReturn(racks);

        when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        RacksDto result =
                racksService.changeToggleStatus("RACK01", true);

        Assertions.assertTrue(racks.isStatus());
        assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Racks racks = new Racks();
        racks.setStatus(true);

        RacksDto dto = new RacksDto();

        when(racksRepository.findByIdentifier("RACK01"))
                .thenReturn(racks);

        when(racksRepository.save(racks))
                .thenReturn(racks);

        when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(dto);

        RacksDto result =
                racksService.changeToggleStatus("RACK01", false);

        Assertions.assertFalse(racks.isStatus());
        assertNotNull(result);
    }

    @Test
    void testFindActiveStatus() {
        Racks active = new Racks();
        active.setStatus(true);

        Racks inactive = new Racks();
        inactive.setStatus(false);

        when(racksRepository.findAll())
                .thenReturn(List.of(active, inactive));

        RacksDto dto = new RacksDto();
        List<RacksDto> expectedDtoList = List.of(dto);

        when(modelMapper.map(
                Mockito.eq(List.of(active)),
                Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(expectedDtoList);

        List<RacksDto> result = racksService.findActiveStatus();

        assertNotNull(result, "The result list should not be null");
        assertEquals(1, result.size(), "The result list should contain exactly 1 active rack");
    }
}