package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Rack;
import com.ust.pos.models.RackRepository;
import com.ust.pos.rack.service.impl.RackServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
    void saveTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        Rack rack = new Rack();
        when(rackRepository.findByIdentifier("R1")).thenReturn(null);
        when(modelMapper.map(dto, Rack.class)).thenReturn(rack);
        RackDto result = rackService.save(dto);
        verify(rackRepository).save(rack);
        Assertions.assertEquals("R1", result.getIdentifier());
        Rack activeRack = new Rack();
        activeRack.setDeleted(false);
        when(rackRepository.findByIdentifier("R2")).thenReturn(activeRack);
        RackDto duplicateDto = new RackDto();
        duplicateDto.setIdentifier("R2");
        result = rackService.save(duplicateDto);
        Assertions.assertFalse(result.isSuccess());
        Rack deletedRack = new Rack();
        deletedRack.setDeleted(true);
        when(rackRepository.findByIdentifier("R3")).thenReturn(deletedRack);
        RackDto deletedDto = new RackDto();
        deletedDto.setIdentifier("R3");
        result = rackService.save(deletedDto);
        Assertions.assertFalse(result.isSuccess());
    }

    @Test
    void findByIdentifierUpdateAndDeleteTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(rack);
        when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);
        RackDto result = rackService.findByIdentifier("R1");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("R1", result.getIdentifier());
        result = rackService.update(dto);
        verify(modelMapper).map(dto, rack);
        verify(rackRepository).save(rack);
        Assertions.assertEquals("R1", result.getIdentifier());
        rackService.delete("R1");
        Assertions.assertTrue(rack.getDeleted());
        verify(rackRepository, atLeastOnce()).save(rack);
        when(rackRepository.findByIdentifierAndDeletedFalse("R2")).thenReturn(null);
        RackDto notFound = rackService.update(new RackDto() {{setIdentifier("R2");}});
        Assertions.assertFalse(notFound.isSuccess());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        Page<Rack> page = new PageImpl<>(List.of(rack), pageable, 1);
        when(rackRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new RackDto()));
        WsDto<RackDto> result = rackService.findAll(pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Rack> specification = mock(Specification.class);
        Page<Rack> page = new PageImpl<>(List.of(new Rack()), pageable, 1);
        when(rackRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new RackDto()));
        WsDto<RackDto> result = rackService.findAll(specification, pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        verify(rackRepository).findAll(specification, pageable);
    }

    @Test
    void findAllActiveTest() {
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        when(rackRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(rack));
        when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);
        List<RackDto> result = rackService.findAllActive();
        Assertions.assertEquals(1, result.size());
        when(rackRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of());
        Assertions.assertTrue(rackService.findAllActive().isEmpty());
    }

    @Test
    void toggleStatusTest() {
        when(rackRepository.save(any(Rack.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Rack.class), eq(RackDto.class))).thenReturn(new RackDto());
        Rack rack = new Rack();
        rack.setIdentifier("R1");
        rack.setStatus(true);
        when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(rack);
        rackService.toggleStatus("R1");
        Assertions.assertFalse(rack.getStatus());
        rack.setStatus(false);
        when(rackRepository.findByIdentifierAndDeletedFalse("R2")).thenReturn(rack);
        rackService.toggleStatus("R2");
        Assertions.assertTrue(rack.getStatus());
        rack.setStatus(null);
        when(rackRepository.findByIdentifierAndDeletedFalse("R3")).thenReturn(rack);
        rackService.toggleStatus("R3");
        Assertions.assertTrue(rack.getStatus());
        when(rackRepository.findByIdentifierAndDeletedFalse("R4")).thenReturn(null);
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> rackService.toggleStatus("R4"));
        Assertions.assertEquals("Rack not found with identifier: R4", ex.getMessage());
    }
}