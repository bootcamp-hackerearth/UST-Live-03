package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.models.Warehouse;
import com.ust.pos.models.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
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
class WarehouseServiceTest {

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        dto.setSuccess(true);
        Warehouse warehouse = new Warehouse();
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);
        when(modelMapper.map(dto, Warehouse.class)).thenReturn(warehouse);
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        WarehouseDto result = warehouseService.save(dto);
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("W1", result.getIdentifier());
        verify(warehouseRepository).save(warehouse);
        Warehouse duplicate = new Warehouse();
        duplicate.setDeleted(false);
        when(warehouseRepository.findByIdentifier("W2")).thenReturn(duplicate);
        WarehouseDto duplicateDto = new WarehouseDto();
        duplicateDto.setIdentifier("W2");
        result = warehouseService.save(duplicateDto);
        Assertions.assertFalse(result.isSuccess());
        Warehouse deleted = new Warehouse();
        deleted.setDeleted(true);
        when(warehouseRepository.findByIdentifier("W3")).thenReturn(deleted);
        WarehouseDto deletedDto = new WarehouseDto();
        deletedDto.setIdentifier("W3");
        result = warehouseService.save(deletedDto);
        Assertions.assertFalse(result.isSuccess());
    }

    @Test
    void findByIdentifierUpdateAndDeleteTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        when(warehouseRepository.findByIdentifierAndDeletedFalse("W1")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);
        WarehouseDto result = warehouseService.findByIdentifier("W1");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("W1", result.getIdentifier());
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);
        result = warehouseService.update(dto);
        Assertions.assertTrue(result.isSuccess());
        verify(modelMapper).map(dto, warehouse);
        verify(warehouseRepository).save(warehouse);
        warehouseService.delete("W1");
        Assertions.assertTrue(warehouse.getDeleted());
        verify(warehouseRepository, atLeastOnce()).save(warehouse);
        when(warehouseRepository.findByIdentifier("W2")).thenReturn(null);
        WarehouseDto updateFail = new WarehouseDto();
        updateFail.setIdentifier("W2");
        result = warehouseService.update(updateFail);
        Assertions.assertFalse(result.isSuccess());
        when(warehouseRepository.findByIdentifierAndDeletedFalse("W3")).thenReturn(null);
        ResourceNotFoundException ex = Assertions.assertThrows(ResourceNotFoundException.class, () -> warehouseService.findByIdentifier("W3"));
        Assertions.assertEquals("Warehouse with identifier 'W3' not found", ex.getMessage()
        );
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Warehouse warehouse = new Warehouse();
        Page<Warehouse> page = new PageImpl<>(List.of(warehouse), pageable, 1);
        when(warehouseRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new WarehouseDto()));
        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Warehouse> specification = mock(Specification.class);
        Page<Warehouse> page = new PageImpl<>(List.of(new Warehouse()), pageable, 1);
        when(warehouseRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new WarehouseDto()));
        WsDto<WarehouseDto> result = warehouseService.findAll(specification, pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        verify(warehouseRepository).findAll(specification, pageable);
    }

    @Test
    void findAllActiveTest() {
        Warehouse warehouse = new Warehouse();
        when(warehouseRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(warehouse));
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(new WarehouseDto());
        List<WarehouseDto> result = warehouseService.findAllActive();
        Assertions.assertEquals(1, result.size());
        when(warehouseRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of());
        Assertions.assertTrue(warehouseService.findAllActive().isEmpty()
        );
    }

    @Test
    void toggleStatusTest() {
        when(warehouseRepository.save(any(Warehouse.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Warehouse.class), eq(WarehouseDto.class))).thenReturn(new WarehouseDto());
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");
        warehouse.setStatus(true);
        when(warehouseRepository.findByIdentifierAndDeletedFalse("W1")).thenReturn(warehouse);
        warehouseService.toggleStatus("W1");
        Assertions.assertFalse(warehouse.getStatus());
        warehouse.setStatus(false);
        when(warehouseRepository.findByIdentifierAndDeletedFalse("W2")).thenReturn(warehouse);
        warehouseService.toggleStatus("W2");
        Assertions.assertTrue(warehouse.getStatus());
        warehouse.setStatus(null);
        when(warehouseRepository.findByIdentifierAndDeletedFalse("W3")).thenReturn(warehouse);
        warehouseService.toggleStatus("W3");
        Assertions.assertTrue(warehouse.getStatus());
        when(warehouseRepository.findByIdentifierAndDeletedFalse("W4")).thenReturn(null);
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> warehouseService.toggleStatus("W4"));
        Assertions.assertTrue(ex.getMessage().contains("Warehouse not found with identifier"));
        verify(warehouseRepository, atLeast(3)).save(any(Warehouse.class));
    }
}