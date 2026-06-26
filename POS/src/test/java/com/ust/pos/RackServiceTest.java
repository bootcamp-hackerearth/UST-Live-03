package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.impl.RackServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RackServiceTest {

    @Mock
    private RackRepository rackRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RackServiceImpl rackService;

    private RackDto rackDto;
    private Rack rack;

    @BeforeEach
    void setUp() {
        rackDto = new RackDto();
        rackDto.setIdentifier("RACK-001");

        rack = new Rack();
        rack.setIdentifier("RACK-001");
        rack.setStatus(true);
        rack.setDeleted(false);
    }

    @Test
    @DisplayName("Save Rack - Success")
    void save_Success() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(null);
        when(modelMapper.map(rackDto, Rack.class)).thenReturn(rack);

        RackDto result = rackService.save(rackDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Rack created successfully", result.getMessage());
        verify(rackRepository).save(rack);
    }

    @Test
    @DisplayName("Save Rack - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        rack.setDeleted(false);
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);

        RackDto result = rackService.save(rackDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(rackRepository, never()).save(any(Rack.class));
    }

    @Test
    @DisplayName("Save Rack - Failure: Previously Soft-Deleted")
    void save_Failure_PreviouslyDeleted() {
        rack.setDeleted(true);
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);

        RackDto result = rackService.save(rackDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(rackRepository, never()).save(any(Rack.class));
    }

    @Test
    @DisplayName("Find All Racks - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rack> rackPage = new PageImpl<>(List.of(rack));

        when(rackRepository.findByDeletedFalse(pageable)).thenReturn(rackPage);
        when(modelMapper.map(eq(rackPage.getContent()), any(Type.class))).thenReturn(List.of(rackDto));

        WsDto<RackDto> result = rackService.findAll(pageable);

        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertFalse(result.getDtoList().isEmpty());
    }

    @Test
    @DisplayName("Find All Active Racks - Success")
    void findAllActive_Success() {
        List<Rack> activeRacks = List.of(rack);
        when(rackRepository.findAllByStatusAndDeletedFalse(true)).thenReturn(activeRacks);
        when(modelMapper.map(eq(activeRacks), any(Type.class))).thenReturn(List.of(rackDto));

        List<RackDto> result = rackService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(rackDto);

        RackDto result = rackService.findByIdentifier("RACK-001");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Update Rack - Success")
    void update_Success() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);

        RackDto result = rackService.update(rackDto);

        Assertions.assertNotNull(result);
        verify(rackRepository).save(rack);
    }

    @Test
    @DisplayName("Update Rack - Failure: Not Found")
    void update_Failure_NotFound() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(null);

        RackDto result = rackService.update(rackDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(rackRepository, never()).save(any(Rack.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(rackDto);

        RackDto result = rackService.toggleStatus("RACK-001");

        Assertions.assertFalse(rack.isStatus());
        verify(rackRepository).save(rack);
    }

    @Test
    @DisplayName("Delete Rack - Success")
    void delete_Success() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(rack);

        boolean result = rackService.delete("RACK-001");

        Assertions.assertTrue(result);
        verify(rackRepository).save(rack);
    }

    @Test
    @DisplayName("Delete Rack - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(rackRepository.findByIdentifier("RACK-001")).thenReturn(null);

        boolean result = rackService.delete("RACK-001");

        Assertions.assertFalse(result);
        verify(rackRepository, never()).save(any(Rack.class));
    }
}