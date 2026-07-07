package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.impl.RackServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RackServiceTest {

    @Mock
    private RackRepository rackRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RackServiceImpl rackService;

    private Rack rack;
    private RackDto rackDto;

    @BeforeEach
    void setUp() {
        rackDto = new RackDto();
        rackDto.setIdentifier("RACK-001");
        rackDto.setSuccess(true);

        rack = new Rack();
        rack.setIdentifier("RACK-001");
        rack.setStatus(true);
        rack.setDeleted(false);
    }

    @Test
    void testSave_RackAlreadyExists() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);

        RackDto result = rackService.save(rackDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Rack with identifier - RACK-001 already exists", result.getMessage());

        verify(rackRepository, times(1)).findByIdentifier("RACK-001");
        verify(rackRepository, never()).save(any());
    }

    @Test
    void testSave_RackAlreadyExists_ButIsSoftDeleted() {
        rack.setDeleted(true);
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);

        RackDto result = rackService.save(rackDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Rack identifier - RACK-001 not available", result.getMessage());

        verify(rackRepository, times(1)).findByIdentifier("RACK-001");
        verify(rackRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testSave_NewRack() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(null);
        when(modelMapper.map(rackDto, Rack.class)).thenReturn(rack);

        RackDto result = rackService.save(rackDto);

        assertNotNull(result);
        verify(rackRepository, times(1)).findByIdentifier("RACK-001");
        verify(modelMapper, times(1)).map(rackDto, Rack.class);
        verify(rackRepository, times(1)).save(rack);
    }

    @Test
    void testUpdate_RackNotFound() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(null);

        RackDto result = rackService.update(rackDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("rack with identifier - RACK-001 not found", result.getMessage());

        verify(rackRepository, times(1)).findByIdentifier("RACK-001");
        verify(rackRepository, never()).save(any());
    }

    @Test
    void testUpdate_RackFound() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);

        RackDto result = rackService.update(rackDto);

        assertNotNull(result);
        verify(rackRepository, times(1)).findByIdentifier("RACK-001");
        verify(modelMapper, times(1)).map(rackDto, rack);
        verify(rackRepository, times(1)).save(rack);
    }

    @Test
    void testDelete() {
        rack.setDeleted(false);

        when(rackRepository.findByIdentifierAndDeletedFalse("RACK-001")).thenReturn(rack);

        assertDoesNotThrow(() -> rackService.delete("RACK-001"));

        verify(rackRepository, times(1)).findByIdentifierAndDeletedFalse("RACK-001");
        assertTrue(rack.getDeleted());
    }

    @Test
    void testFindAll_WithData() {
        Pageable pageable = PageRequest.of(0, 50);
        List<Rack> rackList = Collections.singletonList(rack);
        List<RackDto> rackDtoList = Collections.singletonList(rackDto);
        Page<Rack> rackPage = new PageImpl<>(rackList, pageable, rackList.size());
        Type listType = new TypeToken<List<RackDto>>() {}.getType();

        when(rackRepository.findAllByDeletedFalse(pageable)).thenReturn(rackPage);
        when(modelMapper.map(rackPage.getContent(), listType)).thenReturn(rackDtoList);

        WsDto<RackDto> result = rackService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(50, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(rackRepository, times(1)).findAllByDeletedFalse(pageable);
        verify(modelMapper, times(1)).map(rackPage.getContent(), listType);
    }

    @Test
    void testFindAll_EmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rack> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Type listType = new TypeToken<List<RackDto>>() {}.getType();

        when(rackRepository.findAllByDeletedFalse(pageable)).thenReturn(emptyPage);
        when(modelMapper.map(emptyPage.getContent(), listType)).thenReturn(Collections.emptyList());

        WsDto<RackDto> result = rackService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getDtoList().isEmpty());
        assertEquals(0L, result.getTotalRecords());
        assertEquals(0, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(rackRepository, times(1)).findAllByDeletedFalse(pageable);
        verify(modelMapper, times(1)).map(emptyPage.getContent(), listType);
    }

    @Test
    void testFindByIdentifier_Found() {
        when(rackRepository.findByIdentifierAndDeletedFalse("RACK-001")).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(rackDto);

        RackDto result = rackService.findByIdentifier("RACK-001");

        assertNotNull(result);
        assertEquals("RACK-001", result.getIdentifier());

        verify(rackRepository, times(1)).findByIdentifierAndDeletedFalse("RACK-001");
        verify(modelMapper, times(1)).map(rack, RackDto.class);
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(rackRepository.findByIdentifierAndDeletedFalse("RACK-001")).thenReturn(null);
        when(modelMapper.map(null, RackDto.class)).thenReturn(null);

        RackDto result = rackService.findByIdentifier("RACK-001");

        assertNull(result);

        verify(rackRepository, times(1)).findByIdentifierAndDeletedFalse("RACK-001");
        verify(modelMapper, times(1)).map(null, RackDto.class);
    }

    @Test
    void testFindActiveRacks() {
        List<Rack> rackEntities = Collections.singletonList(rack);
        List<RackDto> rackDtos = Collections.singletonList(rackDto);

        when(rackRepository.findByStatusTrueAndDeletedFalse()).thenReturn(rackEntities);
        when(modelMapper.map(eq(rackEntities), any(Type.class))).thenReturn(rackDtos);

        List<RackDto> result = rackService.findActiveRacks();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(rackRepository, times(1)).findByStatusTrueAndDeletedFalse();
    }

    @Test
    void testToggleStatus_TrueToFalse() {
        rack.setStatus(true);
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(rackDto);

        rackService.toggleStatus("RACK-001");

        assertFalse(rack.isStatus());
        verify(rackRepository, times(1)).save(rack);
    }

    @Test
    void testToggleStatus_FalseToTrue() {
        rack.setStatus(false);
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(rackDto);

        rackService.toggleStatus("RACK-001");

        assertTrue(rack.isStatus());
        verify(rackRepository, times(1)).save(rack);
    }

    @Test
    void testToggleStatus_RackNotFound() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(null);

        RackDto result = rackService.toggleStatus("RACK-001");

        assertNull(result);
        verify(rackRepository, times(1)).findByIdentifier("RACK-001");
        verify(rackRepository, never()).save(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindAll_WithSpecification_Success() {
        Specification<Rack> mockSpec = mock(Specification.class);
        Pageable pageable = PageRequest.of(1, 15);
        List<Rack> rackList = Collections.singletonList(rack);
        List<RackDto> rackDtoList = Collections.singletonList(rackDto);
        Page<Rack> rackPage = new PageImpl<>(rackList, pageable, 45);
        Type listType = new TypeToken<List<RackDto>>() {}.getType();

        when(rackRepository.findAll(mockSpec, pageable)).thenReturn(rackPage);
        when(modelMapper.map(rackPage.getContent(), listType)).thenReturn(rackDtoList);

        WsDto<RackDto> result = rackService.findAll(mockSpec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(45L, result.getTotalRecords());
        assertEquals(3, result.getTotalPages());
        assertEquals(15, result.getSizePerPage());
        assertEquals(1, result.getPage());

        verify(rackRepository, times(1)).findAll(mockSpec, pageable);
        verify(modelMapper, times(1)).map(rackPage.getContent(), listType);
    }
}