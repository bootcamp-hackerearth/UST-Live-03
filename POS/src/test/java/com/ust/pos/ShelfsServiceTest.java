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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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

    @InjectMocks
    private ShelfsServiceImpl shelfsService;

    private Shelfs shelfs;
    private ShelfsDto shelfsDto;

    @BeforeEach
    void setUp() {

        shelfs = new Shelfs();
        shelfs.setIdentifier("SH1");
        shelfs.setStatus(true);
        shelfs.setDeleted(false);

        shelfsDto = new ShelfsDto();
        shelfsDto.setIdentifier("SH1");
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Shelfs> page =
                new PageImpl<>(Collections.singletonList(shelfs));

        when(shelfsRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(shelfsDto));

        WsDto<ShelfsDto> result = shelfsService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Shelfs> specification =
                mock(Specification.class);

        Page<Shelfs> page =
                new PageImpl<>(Collections.singletonList(shelfs));

        when(shelfsRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(shelfsDto));

        WsDto<ShelfsDto> result =
                shelfsService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());

        verify(shelfsRepository)
                .findAll(specification, pageable);
    }

    @Test
    void testFindActiveStatus() {

        Shelfs inactiveShelf = new Shelfs();
        inactiveShelf.setStatus(false);

        when(shelfsRepository.findAll())
                .thenReturn(List.of(shelfs, inactiveShelf));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(shelfsDto));

        List<ShelfsDto> result =
                shelfsService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testChangeToggleStatus() {

        when(shelfsRepository.findByIdentifier("SH1"))
                .thenReturn(shelfs);

        when(modelMapper.map(shelfs, ShelfsDto.class))
                .thenReturn(shelfsDto);

        ShelfsDto result =
                shelfsService.changeToggleStatus("SH1", false);

        assertNotNull(result);
        assertFalse(shelfs.isStatus());

        verify(shelfsRepository).save(shelfs);
    }

    @Test
    void testChangeToggleStatus_ShelfNotFound() {

        when(shelfsRepository.findByIdentifier("SH1"))
                .thenReturn(null);

        when(modelMapper.map(null, ShelfsDto.class))
                .thenReturn(null);

        ShelfsDto result =
                shelfsService.changeToggleStatus("SH1", false);

        assertNull(result);
    }

    @Test
    void testSave_NewShelf() {

        when(shelfsRepository.findByIdentifier("SH1"))
                .thenReturn(null);

        when(modelMapper.map(shelfsDto, Shelfs.class))
                .thenReturn(shelfs);

        ShelfsDto result = shelfsService.save(shelfsDto);

        assertNotNull(result);
        assertEquals("SH1", result.getIdentifier());

        verify(shelfsRepository).save(shelfs);
    }

    @Test
    void testSave_AlreadyExists() {

        when(shelfsRepository.findByIdentifier("SH1"))
                .thenReturn(shelfs);

        ShelfsDto result = shelfsService.save(shelfsDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_SoftDeleted() {

        shelfs.setDeleted(true);

        when(shelfsRepository.findByIdentifier("SH1"))
                .thenReturn(shelfs);

        ShelfsDto result = shelfsService.save(shelfsDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    @Test
    void testDelete() {

        when(shelfsRepository.findByIdentifier("SH1"))
                .thenReturn(shelfs);

        shelfsService.delete("SH1");

        assertTrue(shelfs.isDeleted());
        assertFalse(shelfs.isStatus());

        verify(shelfsRepository).save(shelfs);
    }

    @Test
    void testFindByIdentifier() {

        when(shelfsRepository.findByIdentifier("SH1"))
                .thenReturn(shelfs);

        when(modelMapper.map(shelfs, ShelfsDto.class))
                .thenReturn(shelfsDto);

        ShelfsDto result =
                shelfsService.findByIdentifier("SH1");

        assertNotNull(result);
        assertEquals("SH1", result.getIdentifier());
    }

    @Test
    void testUpdate() {

        when(shelfsRepository.findByIdentifier("SH1"))
                .thenReturn(shelfs);

        doNothing().when(modelMapper)
                .map(shelfsDto, shelfs);

        ShelfsDto result = shelfsService.update(shelfsDto);

        assertNotNull(result);

        verify(shelfsRepository).save(shelfs);
    }
}