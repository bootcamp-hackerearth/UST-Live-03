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
        racks.setIdentifier("R1");
        racks.setStatus(true);
        racks.setDeleted(false);

        racksDto = new RacksDto();
        racksDto.setIdentifier("R1");
    }

    // ✅ FIND ALL (Pagination)
    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Racks> page = new PageImpl<>(Collections.singletonList(racks));

        when(racksRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(racksDto));

        WsDto<RacksDto> result = racksService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    // ✅ SAVE - NEW RACK
    @Test
    void testSave_NewRacks() {
        when(racksRepository.findByIdentifier("R1")).thenReturn(null);
        when(modelMapper.map(racksDto, Racks.class)).thenReturn(racks);

        doNothing().when(racksService).setAuditFields(racks, true);

        RacksDto result = racksService.save(racksDto);

        assertNotNull(result);
        verify(racksRepository).save(racks);
    }

    // ✅ SAVE - ALREADY EXISTS
    @Test
    void testSave_AlreadyExists() {
        when(racksRepository.findByIdentifier("R1")).thenReturn(racks);

        RacksDto result = racksService.save(racksDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    // ✅ SAVE - SOFT DELETED
    @Test
    void testSave_SoftDeleted() {
        racks.setDeleted(true);

        when(racksRepository.findByIdentifier("R1")).thenReturn(racks);

        RacksDto result = racksService.save(racksDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    // ✅ DELETE
    @Test
    void testDelete() {
        when(racksRepository.findByIdentifier("R1")).thenReturn(racks);

        doNothing().when(racksService).softDelete(racks);
        doNothing().when(racksService).setAuditFields(racks, false);

        racksService.delete("R1");

        verify(racksRepository).save(racks);
    }

    // ✅ FIND BY IDENTIFIER
    @Test
    void testFindByIdentifier() {
        when(racksRepository.findByIdentifier("R1")).thenReturn(racks);
        when(modelMapper.map(racks, RacksDto.class)).thenReturn(racksDto);

        RacksDto result = racksService.findByIdentifier("R1");

        assertNotNull(result);
    }

    // ✅ UPDATE
    @Test
    void testUpdate() {
        when(racksRepository.findByIdentifier("R1")).thenReturn(racks);

        doNothing().when(modelMapper).map(racksDto, racks);
        doNothing().when(racksService).setAuditFields(racks, false);

        RacksDto result = racksService.update(racksDto);

        assertNotNull(result);
        verify(racksRepository).save(racks);
    }

    // ✅ CHANGE TOGGLE STATUS
    @Test
    void testChangeToggleStatus() {
        when(racksRepository.findByIdentifier("R1")).thenReturn(racks);
        when(modelMapper.map(racks, RacksDto.class)).thenReturn(racksDto);

        RacksDto result = racksService.changeToggleStatus("R1", false);

        assertNotNull(result);
        assertFalse(racks.isStatus());
        verify(racksRepository).save(racks);
    }

    // ✅ FIND ACTIVE STATUS
    @Test
    void testFindActiveStatus() {
        racks.setStatus(true);

        Racks inactive = new Racks();
        inactive.setStatus(false);

        List<Racks> racksList = List.of(racks, inactive);

        when(racksRepository.findAll()).thenReturn(racksList);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(racksDto));

        List<RacksDto> result = racksService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}