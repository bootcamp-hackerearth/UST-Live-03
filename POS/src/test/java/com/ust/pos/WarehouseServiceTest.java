package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void save_success() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        Warehouse entity = new Warehouse();
        Warehouse saved = new Warehouse();
        saved.setIdentifier("W1");
        WarehouseDto responseDto = new WarehouseDto();
        responseDto.setIdentifier("W1");
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);
        when(modelMapper.map(dto, Warehouse.class)).thenReturn(entity);
        when(warehouseRepository.save(entity)).thenReturn(saved);
        when(modelMapper.map(saved, WarehouseDto.class)).thenReturn(responseDto);
        WarehouseDto response = warehouseService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Warehouse saved successfully", response.getMessage());
    }

    @Test
    void save_failure_exists() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(new Warehouse());
        WarehouseDto response = warehouseService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Warehouse already exists", response.getMessage());
    }

    @Test
    void save_failure_empty() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("");
        WarehouseDto response = warehouseService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Identifier required", response.getMessage());
    }

    @Test
    void update_success() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        Warehouse existing = new Warehouse();
        existing.setIdentifier("W1");
        Warehouse saved = new Warehouse();
        saved.setIdentifier("W1");
        WarehouseDto mappedDto = new WarehouseDto();
        mappedDto.setIdentifier("W1");
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(existing);
        doNothing().when(modelMapper).map(any(WarehouseDto.class), any(Warehouse.class));
        when(warehouseRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, WarehouseDto.class)).thenReturn(mappedDto);
        WarehouseDto response = warehouseService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Warehouse updated successfully", response.getMessage());
        verify(warehouseRepository).save(existing);
    }

    @Test
    void update_failure() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);
        WarehouseDto response = warehouseService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Warehouse not found", response.getMessage());
    }

    @Test
    void findByIdentifier_success() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);
        WarehouseDto response = warehouseService.findByIdentifier("W1");
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("W1", response.getIdentifier());
    }

    @Test
    void findByIdentifier_failure() {
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);
        WarehouseDto response = warehouseService.findByIdentifier("W1");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Warehouse not found", response.getMessage());
    }

    @Test
    void findAll_success() {
        Warehouse warehouse = new Warehouse();
        List<Warehouse> list = List.of(warehouse);
        Page<Warehouse> page = new PageImpl<>(list);
        List<WarehouseDto> mappedList = List.of(new WarehouseDto());
        when(warehouseRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(eq(list), ArgumentMatchers.<Type>any())).thenReturn(mappedList);
        List<WarehouseDto> result = warehouseService.findAll(Pageable.unpaged());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void delete_test() {
        warehouseService.delete("W1");
        verify(warehouseRepository).deleteByIdentifier("W1");
    }
}