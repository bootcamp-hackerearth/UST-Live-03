package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.impl.RackServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RackServiceTest {

    @Mock
    private RackRepository rackRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private RackServiceImpl rackService;

    private Rack rack;
    private RackDto rackDto;

    @BeforeEach
    void setUp() {
        rack = new Rack();
        rack.setId(1L);
        rack.setIdentifier("RACK-001");
        rack.setStatus(true);
        rack.setDeleted(false);

        rackDto = new RackDto();
        rackDto.setIdentifier("RACK-001");
    }

    @Test
    void testFindByIdentifier_Success() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);

        RackDto result = rackService.findByIdentifier("RACK-001");

        assertNotNull(result);
        assertEquals("RACK-001", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_ThrowsResourceNotFoundException() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> rackService.findByIdentifier("RACK-001"));
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> rackService.save(null));
    }

    @Test
    void testSave_WhenIdentifierIsNull() {
        rackDto.setIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> rackService.save(rackDto));
    }

    @Test
    void testSave_WhenRackAlreadyExistsAndNotDeleted() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);

        RackDto result = rackService.save(rackDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenRackAlreadyExistsButDeleted() {
        rack.setDeleted(true);
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);

        RackDto result = rackService.save(rackDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(null);
        when(rackRepository.save(any(Rack.class))).thenReturn(rack);

        RackDto result = rackService.save(rackDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Rack created successfully", result.getMessage());
    }

    @Test
    void testUpdate_WhenRackNotFound() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(null);

        RackDto result = rackService.update(rackDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenRackDeleted() {
        rack.setDeleted(true);
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);

        RackDto result = rackService.update(rackDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);
        when(rackRepository.save(any(Rack.class))).thenReturn(rack);

        RackDto result = rackService.update(rackDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Rack updated successfully", result.getMessage());
    }

    @Test
    void testDelete_WhenRackNotFound() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(null);

        rackService.delete("RACK-001");

        verify(rackRepository, never()).save(any(Rack.class));
    }

    @Test
    void testDelete_Success() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);
        when(rackRepository.save(any(Rack.class))).thenReturn(rack);

        rackService.delete("RACK-001");

        verify(rackRepository, times(1)).save(any(Rack.class));
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rack> page = new PageImpl<>(Collections.singletonList(rack), pageable, 1);
        when(rackRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<RackDto> result = rackService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertFalse(result.getDtoList().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rack> page = new PageImpl<>(Collections.singletonList(rack), pageable, 1);
        Specification<Rack> spec = mock(Specification.class);
        when(rackRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        WsDto<RackDto> result = rackService.findAll(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testToggleStatus_WhenRackNotFound() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(null);

        RackDto result = rackService.toggleStatus("RACK-001");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus_Success() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);
        when(rackRepository.save(any(Rack.class))).thenReturn(rack);

        RackDto result = rackService.toggleStatus("RACK-001");

        assertNotNull(result);
        assertFalse(result.isStatus());
    }

    @Test
    void testFindIfTrue() {
        when(rackRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(Collections.singletonList(rack));

        List<RackDto> result = rackService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}