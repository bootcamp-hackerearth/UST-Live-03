package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

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
    void saveTestSuccess() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse warehouse = new Warehouse();

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);
        when(modelMapper.map(dto, Warehouse.class)).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertEquals("W1", result.getIdentifier());
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void saveTestFailure() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse warehouse = new Warehouse();

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertEquals("W1", result.getIdentifier());
        verify(modelMapper).map(dto, warehouse);
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void updateTestFailure() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        warehouseService.delete("W1");
        verify(warehouseRepository).deleteByIdentifier("W1");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);

        WarehouseDto result = warehouseService.findByIdentifier("W1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("W1", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);

        WarehouseDto result = warehouseService.findByIdentifier("W1");

        Assertions.assertNull(result);
    }

    @Test
    void findAllTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Warehouse> page = mock(Page.class);

        List<Warehouse> warehouses = List.of(new Warehouse(), new Warehouse());
        List<WarehouseDto> dtoList = List.of(new WarehouseDto(), new WarehouseDto());

        when(warehouseRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(warehouses);
        when(modelMapper.map(eq(warehouses), any(Type.class))).thenReturn(dtoList);

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        Assertions.assertEquals(2, result.getDtoList().size());

        verify(warehouseRepository).findAll(pageable);
        verify(page).getContent();
        verify(modelMapper).map(eq(warehouses), any(Type.class));
    }
}