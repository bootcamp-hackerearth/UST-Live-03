package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
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

    private Rack rackEntity;
    private RackDto rackDto;

    @BeforeEach
    void setUp() {
        rackEntity = new Rack();
        rackEntity.setId(1L);
        rackEntity.setIdentifier("RCK-101");
        rackEntity.setStatus(true);
        rackEntity.setDeleted(false);

        rackDto = new RackDto();
        rackDto.setIdentifier("RCK-101");
    }

    @Test
    void testFindByIdentifier() {
        when(rackRepository.findByIdentifier("RCK-101")).thenReturn(rackEntity);

        RackDto result = rackService.findByIdentifier("RCK-101");

        assertNotNull(result);
        assertEquals("RCK-101", result.getIdentifier());
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
    void testSave_WhenRackExistsAndNotDeleted() {
        when(rackRepository.findByIdentifier("RCK-101")).thenReturn(rackEntity);

        RackDto result = rackService.save(rackDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenRackWasPreviouslyDeleted() {
        rackEntity.setDeleted(true);
        when(rackRepository.findByIdentifier("RCK-101")).thenReturn(rackEntity);

        RackDto result = rackService.save(rackDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(rackRepository.findByIdentifier("RCK-101")).thenReturn(null);
        when(rackRepository.save(any(Rack.class))).thenReturn(rackEntity);

        RackDto result = rackService.save(rackDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Rack created successfully", result.getMessage());
    }

    @Test
    void testUpdate_WhenRackNotFound() {
        when(rackRepository.findByIdentifier("RCK-101")).thenReturn(null);

        RackDto result = rackService.update(rackDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenRackgetDeleted() {
        rackEntity.setDeleted(true);
        when(rackRepository.findByIdentifier("RCK-101")).thenReturn(rackEntity);

        RackDto result = rackService.update(rackDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        when(rackRepository.findByIdentifier("RCK-101")).thenReturn(rackEntity);
        when(rackRepository.save(any(Rack.class))).thenReturn(rackEntity);

        RackDto result = rackService.update(rackDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Rack updated successfully", result.getMessage());
    }

    @Test
    void testDelete_WhenRackNotFound() {
        when(rackRepository.findByIdentifier("RCK-101")).thenReturn(null);

        rackService.delete("RCK-101");

        verify(rackRepository, never()).save(any(Rack.class));
    }

    @Test
    void testDelete_Success() {
        when(rackRepository.findByIdentifier("RCK-101")).thenReturn(rackEntity);
        when(rackRepository.save(any(Rack.class))).thenReturn(rackEntity);

        rackService.delete("RCK-101");

        verify(rackRepository, times(1)).save(rackEntity);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Rack> entityList = Collections.singletonList(rackEntity);
        Page<Rack> page = new PageImpl<>(entityList, pageable, 1);

        when(rackRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<RackDto> result = rackService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getPage());
    }

    @Test
    void testToggleStatus_WhenRackNotFound() {
        when(rackRepository.findByIdentifier("RCK-101")).thenReturn(null);

        RackDto result = rackService.toggleStatus("RCK-101");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus_Success() {
        rackEntity.setStatus(true);
        when(rackRepository.findByIdentifier("RCK-101")).thenReturn(rackEntity);
        when(rackRepository.save(any(Rack.class))).thenReturn(rackEntity);

        RackDto result = rackService.toggleStatus("RCK-101");

        assertNotNull(result);
        assertFalse(result.getStatus());
    }

    @Test
    void testFindIfTrue() {
        List<Rack> activeRacks = Collections.singletonList(rackEntity);
        when(rackRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(activeRacks);

        List<RackDto> result = rackService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}