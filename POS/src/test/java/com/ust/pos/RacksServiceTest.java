package com.ust.pos;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Racks;
import com.ust.pos.model.RacksRepository;
import com.ust.pos.racks.service.impl.RacksServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RacksServiceTest {

    @Mock
    private RacksRepository racksRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private RacksServiceImpl racksService;

    private Racks racks;
    private RacksDto racksDto;

    @BeforeEach
    void setUp() {

        racks = new Racks();
        racks.setIdentifier("RACK1");
        racks.setStatus(true);
        racks.setDeleted(false);

        racksDto = new RacksDto();
        racksDto.setIdentifier("RACK1");
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Racks> page =
                new PageImpl<>(Collections.singletonList(racks));

        when(racksRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(racksDto));

        WsDto<RacksDto> result = racksService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Racks> specification =
                mock(Specification.class);

        Page<Racks> page =
                new PageImpl<>(Collections.singletonList(racks));

        when(racksRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(racksDto));

        WsDto<RacksDto> result =
                racksService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());

        verify(racksRepository)
                .findAll(specification, pageable);
    }

    @Test
    void testSave_NewRack() {

        when(racksRepository.findByIdentifier("RACK1"))
                .thenReturn(null);

        when(modelMapper.map(racksDto, Racks.class))
                .thenReturn(racks);

        RacksDto result = racksService.save(racksDto);

        assertNotNull(result);

        verify(racksRepository).save(racks);
    }

    @Test
    void testSave_AlreadyExists() {

        when(racksRepository.findByIdentifier("RACK1"))
                .thenReturn(racks);

        RacksDto result = racksService.save(racksDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_SoftDeleted() {

        racks.setDeleted(true);

        when(racksRepository.findByIdentifier("RACK1"))
                .thenReturn(racks);

        RacksDto result = racksService.save(racksDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    @Test
    void testDelete() {

        when(racksRepository.findByIdentifier("RACK1"))
                .thenReturn(racks);

        racksService.delete("RACK1");

        assertTrue(racks.isDeleted());
        assertFalse(racks.isStatus());

        verify(racksRepository).save(racks);
    }

    @Test
    void testFindByIdentifier() {

        when(racksRepository.findByIdentifier("RACK1"))
                .thenReturn(racks);

        when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(racksDto);

        RacksDto result =
                racksService.findByIdentifier("RACK1");

        assertNotNull(result);
        assertEquals("RACK1", result.getIdentifier());
    }

    @Test
    void testUpdate() {

        when(racksRepository.findByIdentifier("RACK1"))
                .thenReturn(racks);

        doNothing().when(modelMapper)
                .map(racksDto, racks);

        RacksDto result = racksService.update(racksDto);

        assertNotNull(result);

        verify(racksRepository).save(racks);
    }

    @Test
    void testChangeToggleStatus() {

        when(racksRepository.findByIdentifier("RACK1"))
                .thenReturn(racks);

        when(modelMapper.map(racks, RacksDto.class))
                .thenReturn(racksDto);

        RacksDto result =
                racksService.changeToggleStatus("RACK1", false);

        assertNotNull(result);
        assertFalse(racks.isStatus());

        verify(racksRepository).save(racks);
    }

    @Test
    void testChangeToggleStatus_RackNotFound() {

        when(racksRepository.findByIdentifier("RACK1"))
                .thenReturn(null);

        when(modelMapper.map(null, RacksDto.class))
                .thenReturn(null);

        RacksDto result =
                racksService.changeToggleStatus("RACK1", false);

        assertNull(result);
    }

    @Test
    void testFindActiveStatus() {

        Racks inactiveRack = new Racks();
        inactiveRack.setStatus(false);

        when(racksRepository.findAll())
                .thenReturn(List.of(racks, inactiveRack));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(racksDto));

        List<RacksDto> result =
                racksService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}