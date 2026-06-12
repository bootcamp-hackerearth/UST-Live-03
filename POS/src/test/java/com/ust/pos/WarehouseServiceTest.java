package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
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

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    void save_success() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse entity = new Warehouse();
        Warehouse saved = new Warehouse();

        WarehouseDto responseDto = new WarehouseDto();
        responseDto.setIdentifier("W1");
        responseDto.setSuccess(true);
        responseDto.setMessage("Warehouse saved successfully");

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(null);

        when(modelMapper.map(any(WarehouseDto.class), eq(Warehouse.class)))
                .thenReturn(entity);

        when(warehouseRepository.save(any(Warehouse.class)))
                .thenReturn(saved);

        when(modelMapper.map(any(Warehouse.class), eq(WarehouseDto.class)))
                .thenReturn(responseDto);

        WarehouseDto response = warehouseService.save(dto);

        assertTrue(response.isSuccess());
        assertEquals("Warehouse saved successfully", response.getMessage());
    }

    @Test
    void save_failure_blank_identifier() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("   ");

        WarehouseDto response = warehouseService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals("Identifier required", response.getMessage());
    }

    @Test
    void update_success() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse existing = new Warehouse();
        Warehouse saved = new Warehouse();

        WarehouseDto responseDto = new WarehouseDto();
        responseDto.setSuccess(true);
        responseDto.setMessage("Warehouse updated successfully");

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(existing);
        lenient().when(modelMapper.map(any(WarehouseDto.class), eq(Warehouse.class)))
                .thenReturn(existing);

        when(warehouseRepository.save(any(Warehouse.class)))
                .thenReturn(saved);

        lenient().when(modelMapper.map(any(Warehouse.class), eq(WarehouseDto.class)))
                .thenReturn(responseDto);

        WarehouseDto response = warehouseService.update(dto);

        assertTrue(response.isSuccess());
        assertEquals("Warehouse updated successfully", response.getMessage());
    }

    @Test
    void update_failure_not_found() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(null);

        WarehouseDto response = warehouseService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals("Warehouse not found", response.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Warehouse warehouse = new Warehouse();

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(warehouse);

        when(modelMapper.map(eq(warehouse), eq(WarehouseDto.class)))
                .thenReturn(dto);

        WarehouseDto response = warehouseService.findByIdentifier("W1");

        assertTrue(response.isSuccess());
        assertEquals("W1", response.getIdentifier());
    }

    @Test
    void findAll_success() {

        Warehouse warehouse = new Warehouse();
        List<Warehouse> list = List.of(warehouse);

        Page<Warehouse> page = new PageImpl<>(list);

        Pageable pageable = PageRequest.of(0, 5);

        when(warehouseRepository.findAll(pageable))
                .thenReturn(page);

        List<WarehouseDto> dtoList = List.of(new WarehouseDto());

        Type type = new TypeToken<List<WarehouseDto>>() {
        }.getType();

        lenient().when(modelMapper.map(anyList(), eq(type)))
                .thenReturn(dtoList);

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
    }

    @Test
    void delete_success() {

        warehouseService.delete("W1");

        verify(warehouseRepository).deleteByIdentifier("W1");
    }
}