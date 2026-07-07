package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.impl.RackServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

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
        dto.setIdentifier("RACK_01");
        Rack rack = new Rack();
        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Rack.class)).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);

        RackDto response = rackService.save(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("RACK_01", response.getIdentifier());
        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void saveFailure_existingActive() {
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK_01");
        Rack existing = new Rack();
        existing.setDeleted(false);

        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(existing);
        RackDto response = rackService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveFailure_softDeleted() {
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK_01");
        Rack existing = new Rack();
        existing.setDeleted(true);

        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(existing);
        RackDto response = rackService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK_01");
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK_01");
        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);

        RackDto response = rackService.findByIdentifier("RACK_01");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("RACK_01", response.getIdentifier());
    }

    @Test
    void updateTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK_01");

        Rack existing = new Rack();
        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(rackRepository.save(existing)).thenReturn(existing);
        RackDto response = rackService.update(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("RACK_01", response.getIdentifier());
        Mockito.verify(rackRepository).save(existing);
    }

    @Test
    void updateFailure() {
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK_01");
        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(null);
        RackDto response = rackService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Rack rack = new Rack();
        rack.setDeleted(false);
        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        rackService.delete("RACK_01");
        Mockito.verify(rackRepository).findByIdentifier("RACK_01");
        Mockito.verify(rackRepository).save(rack);
        Assertions.assertTrue(rack.isDeleted());
    }

    @Test
    void findAllTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK_01");
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK_01");

        List<Rack> racks = List.of(rack);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rack> page = new PageImpl<>(racks, pageable, 1);
        Mockito.when(rackRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of(dto));

        WsDto<RackDto> response = rackService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("RACK_01", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findAllWithSpecificationTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK_01");
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK_01");

        List<Rack> racks = List.of(rack);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rack> page = new PageImpl<>(racks, pageable, 1);
        @SuppressWarnings("unchecked")
        Specification<Rack> specification = Mockito.mock(Specification.class);
        Mockito.when(rackRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of(dto));

        WsDto<RackDto> response = rackService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("RACK_01", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findActiveRacksTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK_01");
        rack.setStatus(true);

        RackDto dto = new RackDto();
        dto.setIdentifier("RACK_01");
        dto.setStatus(true);
        List<Rack> racks = List.of(rack);
        Mockito.when(rackRepository.findByStatusTrue()).thenReturn(racks);
        Mockito.when(modelMapper.map(Mockito.eq(racks), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of(dto));
        List<RackDto> response = rackService.findActiveRacks();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("RACK_01", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
        Mockito.verify(rackRepository).findByStatusTrue();
    }

    @Test
    void toggleStatusSuccessTest() {
        Rack rack = new Rack();
        rack.setIdentifier("RACK_01");
        rack.setStatus(false);
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK_01");
        dto.setStatus(true);

        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);
        RackDto response = rackService.toggleStatus("RACK_01", true);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("RACK_01", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(rackRepository.findByIdentifier("RACK_01")).thenReturn(null);
        Mockito.when(modelMapper.map(Mockito.isNull(), Mockito.eq(RackDto.class))).thenReturn(null);
        RackDto response = rackService.toggleStatus("RACK_01", true);
        Assertions.assertNull(response);
        Mockito.verify(rackRepository, Mockito.never()).save(Mockito.any());
    }
}