package com.ust.pos;

import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.ShelfsRepository;
import com.ust.pos.shelfs.sevice.impl.ShelfsServiceImpl;
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
class ShelfsServiceTest {

    @Mock
    private ShelfsRepository shelfsRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private ShelfsServiceImpl shelfsService;

    private Shelfs shelfs;
    private ShelfsDto shelfsDto;

    @BeforeEach
    void setUp() {
        shelfs = new Shelfs();
        shelfs.setIdentifier("S1");
        shelfs.setStatus(true);
        shelfs.setDeleted(false);

        shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("S1");
    }

    // ✅ FIND ALL (Pagination)
    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shelfs> page = new PageImpl<>(Collections.singletonList(shelfs));

        when(shelfsRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(shelfsDto));

        WsDto<ShelfsDto> result = shelfsService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    // ✅ FIND ACTIVE STATUS
    @Test
    void testFindActiveStatus() {
        shelfs.setStatus(true);

        Shelfs inactive = new Shelfs();
        inactive.setStatus(false);

        List<Shelfs> list = List.of(shelfs, inactive);

        when(shelfsRepository.findAll()).thenReturn(list);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(shelfsDto));

        List<ShelfsDto> result = shelfsService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ✅ CHANGE TOGGLE STATUS
    @Test
    void testChangeToggleStatus() {
        when(shelfsRepository.findByIdentifier("S1")).thenReturn(shelfs);
        when(modelMapper.map(shelfs, ShelfsDto.class)).thenReturn(shelfsDto);

        ShelfsDto result = shelfsService.changeToggleStatus("S1", false);

        assertNotNull(result);
        assertFalse(shelfs.isStatus());
        verify(shelfsRepository).save(shelfs);
    }

    // ✅ SAVE - NEW
    @Test
    void testSave_New() {
        when(shelfsRepository.findByIdentifier("S1")).thenReturn(null);
        when(modelMapper.map(shelfsDto, Shelfs.class)).thenReturn(shelfs);

        doNothing().when(shelfsService).setAuditFields(shelfs, true);

        ShelfsDto result = shelfsService.save(shelfsDto);

        assertNotNull(result);
        verify(shelfsRepository).save(shelfs);
    }

    // ✅ SAVE - ALREADY EXISTS
    @Test
    void testSave_AlreadyExists() {
        when(shelfsRepository.findByIdentifier("S1")).thenReturn(shelfs);

        ShelfsDto result = shelfsService.save(shelfsDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    // ✅ SAVE - SOFT DELETED
    @Test
    void testSave_SoftDeleted() {
        shelfs.setDeleted(true);

        when(shelfsRepository.findByIdentifier("S1")).thenReturn(shelfs);

        ShelfsDto result = shelfsService.save(shelfsDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    // ✅ DELETE
    @Test
    void testDelete() {
        when(shelfsRepository.findByIdentifier("S1")).thenReturn(shelfs);

        doNothing().when(shelfsService).softDelete(shelfs);
        doNothing().when(shelfsService).setAuditFields(shelfs, false);

        shelfsService.delete("S1");

        verify(shelfsRepository).save(shelfs);
    }

    // ✅ FIND BY IDENTIFIER
    @Test
    void testFindByIdentifier() {
        when(shelfsRepository.findByIdentifier("S1")).thenReturn(shelfs);
        when(modelMapper.map(shelfs, ShelfsDto.class)).thenReturn(shelfsDto);

        ShelfsDto result = shelfsService.findByIdentifier("S1");

        assertNotNull(result);
    }

    // ✅ UPDATE
    @Test
    void testUpdate() {
        when(shelfsRepository.findByIdentifier("S1")).thenReturn(shelfs);

        doNothing().when(modelMapper).map(shelfsDto, shelfs);
        doNothing().when(shelfsService).setAuditFields(shelfs, false);

        ShelfsDto result = shelfsService.update(shelfsDto);

        assertNotNull(result);
        verify(shelfsRepository).save(shelfs);
    }
}