package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.impl.RackServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RackServiceTest {

    @InjectMocks
    private RackServiceImpl rackService;
    @Mock
    private RackRepository rackRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void testFindByIdentifier_Success() {
        String identifier = "RACK-001";

        Rack rack = new Rack();
        rack.setIdentifier(identifier);

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier(identifier);

        when(rackRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class))
                .thenReturn(rackDto);

        RackDto result = rackService.findByIdentifier(identifier);

        assertEquals(identifier, result.getIdentifier());
    }

    @Test
    void testSave_Success() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK-001");

        Rack rack = new Rack();
        rack.setIdentifier("RACK-001");

        when(rackRepository.findByIdentifier("RACK-001"))
                .thenReturn(null);
        when(modelMapper.map(rackDto, Rack.class))
                .thenReturn(rack);

        RackDto result = rackService.save(rackDto);

        assertNotNull(result);
        verify(rackRepository).save(rack);
    }

    @Test
    void testSave_AlreadyExists() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK-001");

        Rack existingRack = new Rack();
        existingRack.setIdentifier("RACK-001");
        existingRack.setDeleted(false);

        when(rackRepository.findByIdentifier("RACK-001"))
                .thenReturn(existingRack);

        RackDto result = rackService.save(rackDto);

        assertFalse(result.isSuccess());
        assertEquals("Rack with identifier - RACK-001 already exists", result.getMessage());

        verify(rackRepository, never()).save(any(Rack.class));
    }

    @Test
    void testUpdate_Success() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK-001");

        Rack existingRack = new Rack();
        existingRack.setIdentifier("RACK-001");

        when(rackRepository.findByIdentifier("RACK-001"))
                .thenReturn(existingRack);

        RackDto result = rackService.update(rackDto);

        assertNotNull(result);
        verify(modelMapper).map(rackDto, existingRack);
        verify(rackRepository).save(existingRack);
    }

    @Test
    void testUpdate_NotFound() {
        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK-001");

        when(rackRepository.findByIdentifier("RACK-001"))
                .thenReturn(null);

        RackDto result = rackService.update(rackDto);

        assertFalse(result.isSuccess());
        assertEquals("Rack with identifier - RACK-001 not found", result.getMessage());

        verify(rackRepository, never()).save(any(Rack.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "RACK-001";

        Rack rack = new Rack();
        rack.setIdentifier(identifier);
        rack.setDeleted(false);

        when(rackRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(rack);

        rackService.delete(identifier);

        assertTrue(rack.getDeleted());

        verify(rackRepository).save(rack);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Rack rack = new Rack();
        rack.setIdentifier("RACK-001");

        List<Rack> rackList = List.of(rack);
        Page<Rack> rackPage = new PageImpl<>(rackList, pageable, rackList.size());

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK-001");

        List<RackDto> dtoList = List.of(rackDto);

        when(rackRepository.findAllByDeletedFalse(pageable))
                .thenReturn(rackPage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<RackDto> result = rackService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void testToggleStatus_Success() {
        String identifier = "RACK-001";

        Rack rack = new Rack();
        rack.setIdentifier(identifier);
        rack.setStatus(true);

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier(identifier);

        when(rackRepository.findByIdentifier(identifier))
                .thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class))
                .thenReturn(rackDto);

        RackDto result = rackService.toggleStatus(identifier);

        assertFalse(rack.isStatus());
        assertNotNull(result);

        verify(rackRepository).save(rack);
    }

    @Test
    void testFindActiveRacks_Success() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK-001");

        RackDto rackDto = new RackDto();
        rackDto.setIdentifier("RACK-001");

        List<Rack> rackList = List.of(rack);
        List<RackDto> dtoList = List.of(rackDto);

        when(rackRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(rackList);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(dtoList);

        List<RackDto> result = rackService.findActiveRacks();

        assertEquals(1, result.size());
        assertEquals("RACK-001", result.get(0).getIdentifier());
    }

}