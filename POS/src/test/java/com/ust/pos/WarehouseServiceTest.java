package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Warehouse;
import com.ust.pos.modell.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Test
    void findByIdentifierSuccess() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);
        WarehouseDto result = warehouseService.findByIdentifier("WH1");
        assertNotNull(result);
        assertEquals("WH1", result.getIdentifier());
    }

    @Test
    void findByIdentifierNotFound() {
        when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(null);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> warehouseService.findByIdentifier("WH1")
        );

        assertEquals(
                "Warehouse with identifier 'WH1' not found",
                ex.getMessage()
        );
    }

    @Test
    void saveSuccess() {
        Warehouse warehouse = new Warehouse();
        warehouse.setStatus(null);
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        when(warehouseRepository.findByIdentifier("WH1")).thenReturn(null);
        when(modelMapper.map(dto, Warehouse.class)).thenReturn(warehouse);
        WarehouseDto result = warehouseService.save(dto);
        assertTrue(result.isSuccess());
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void saveDuplicate() {
        Warehouse warehouse = new Warehouse();
        warehouse.setDeleted(false);
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        when(warehouseRepository.findByIdentifier("WH1")).thenReturn(warehouse);
        WarehouseDto result = warehouseService.save(dto);
        assertFalse(result.isSuccess());
    }

    @Test
    void saveSoftDeleted() {
        Warehouse warehouse = new Warehouse();
        warehouse.setDeleted(true);
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        when(warehouseRepository.findByIdentifier("WH1")).thenReturn(warehouse);
        WarehouseDto result = warehouseService.save(dto);
        assertFalse(result.isSuccess());
    }

    @Test
    void updateSuccess() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");
        warehouse.setCreatedBy("admin");
        warehouse.setCreatedOn(LocalDateTime.now());
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1")).thenReturn(warehouse);
        WarehouseDto result = warehouseService.update(dto);
        assertTrue(result.isSuccess());
        verify(modelMapper).map(dto, warehouse);
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void updateNotFound() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");
        when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1")).thenReturn(null);
        WarehouseDto result = warehouseService.update(dto);
        assertFalse(result.isSuccess());
    }

    @Test
    void deleteTest() {
        Warehouse warehouse = new Warehouse();
        when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1")).thenReturn(warehouse).thenReturn(null);
        warehouseService.delete("WH1");
        verify(warehouseRepository).save(warehouse);
        warehouseService.delete("WH1");
    }

    @Test
    void saveSuccessWithExistingStatus() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse warehouse = new Warehouse();
        warehouse.setStatus(false);

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Warehouse.class))
                .thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(dto);

        assertTrue(result.isSuccess());
        assertFalse(warehouse.getStatus());

        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Warehouse> page = new PageImpl<>(List.of(new Warehouse()), pageable, 1);
        when(warehouseRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(new WarehouseDto()));
        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
    }

    @Test
    void toggleStatusSuccess() {
        Warehouse warehouse = new Warehouse();
        warehouse.setStatus(true);
        when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1")).thenReturn(warehouse);
        warehouseService.toggleStatus("WH1");
        assertFalse(warehouse.getStatus());
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void toggleStatusNotFound() {
        when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1")).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> warehouseService.toggleStatus("WH1"));
    }

    @Test
    void findAllActiveTest() {
        Warehouse warehouse = new Warehouse();
        when(warehouseRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(warehouse));
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(new WarehouseDto());
        List<WarehouseDto> result = warehouseService.findAllActive();
        assertEquals(1, result.size());
    }
}
