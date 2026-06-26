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

import java.lang.reflect.Type;
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
        dto.setIdentifier("R1");
        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(null);
        Rack rack = new Rack();
        Mockito.when(modelMapper.map(dto, Rack.class)).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        RackDto response = rackService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestAlreadyExists() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        Rack existing = new Rack();
        existing.setDeleted(false);
        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(existing);
        RackDto response = rackService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestDeletedExists() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        Rack existing = new Rack();
        existing.setDeleted(true);
        Mockito.when(rackRepository.findByIdentifier("R1")).thenReturn(existing);
        RackDto response = rackService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        Rack existing = new Rack();
        Mockito.when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(rackRepository.save(existing)).thenReturn(existing);
        RackDto response = rackService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RackDto dto = new RackDto();
        dto.setIdentifier("R1");
        Mockito.when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(null);
        RackDto response = rackService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Rack rack = new Rack();
        Mockito.when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        rackService.delete("R1");
        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void deleteNullTest() {
        Mockito.when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(null);
        rackService.delete("R1");
        Mockito.verify(rackRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllTest() {
        Rack rack = new Rack();
        RackDto dto = new RackDto();
        List<Rack> list = List.of(rack);
        List<RackDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Rack> page = new PageImpl<>(list);
        Mockito.when(rackRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        WsDto<RackDto> response = rackService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findByIdentifierTest() {
        Rack rack = new Rack();
        RackDto dto = new RackDto();
        Mockito.when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(rack);
        Mockito.when(modelMapper.map(rack, RackDto.class)).thenReturn(dto);
        RackDto response = rackService.findByIdentifier("R1");
        Assertions.assertNotNull(response);
    }

    @Test
    void updateStatusTest() {
        Rack rack = new Rack();
        Mockito.when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(rack);
        Mockito.when(rackRepository.save(rack)).thenReturn(rack);
        rackService.updateStatus("R1", true);
        Mockito.verify(rackRepository).save(rack);
    }

    @Test
    void updateStatusNullTest() {
        Mockito.when(rackRepository.findByIdentifierAndDeletedFalse("R1")).thenReturn(null);
        rackService.updateStatus("R1", true);
        Mockito.verify(rackRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllActiveTest() {
        Rack rack = new Rack();
        RackDto dto = new RackDto();
        List<Rack> list = List.of(rack);
        List<RackDto> dtoList = List.of(dto);
        Mockito.when(rackRepository.findByStatusAndDeletedFalse(true)).thenReturn(list);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        List<RackDto> response = rackService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }
}