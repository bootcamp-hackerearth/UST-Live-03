package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private Warehouse warehouse;
    private WarehouseDto warehouseDto;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setIdentifier("W1");
        warehouse.setStatus(true);
        warehouse.setDeleted(false);

        warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("W1");
    }

    // ✅ FIND ALL (Pagination)
    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Warehouse> page = new PageImpl<>(Collections.singletonList(warehouse));

        when(warehouseRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(warehouseDto));

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    // ✅ SAVE - NEW
    @Test
    void testSave_NewWarehouse() {
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);
        when(modelMapper.map(warehouseDto, Warehouse.class)).thenReturn(warehouse);

        doNothing().when(warehouseService).setAuditFields(warehouse, true);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);
        verify(warehouseRepository).save(warehouse);
    }

    // ✅ SAVE - ALREADY EXISTS
    @Test
    void testSave_AlreadyExists() {
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    // ✅ DELETE (Soft Delete)
    @Test
    void testDelete() {
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        doNothing().when(warehouseService).softDelete(warehouse);
        doNothing().when(warehouseService).setAuditFields(warehouse, false);

        warehouseService.delete("W1");

        verify(warehouseRepository).save(warehouse);
    }

    // ✅ FIND BY IDENTIFIER
    @Test
    void testFindByIdentifier() {
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);

        WarehouseDto result = warehouseService.findByIdentifier("W1");

        assertNotNull(result);
    }

    // ✅ UPDATE
    @Test
    void testUpdate() {
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        doNothing().when(modelMapper).map(warehouseDto, warehouse);
        doNothing().when(warehouseService).setAuditFields(warehouse, false);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertNotNull(result);
        verify(warehouseRepository).save(warehouse);
    }

    // ✅ CHANGE TOGGLE STATUS
    @Test
    void testChangeToggleStatus() {
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);

        WarehouseDto result = warehouseService.changeToggleStatus("W1", false);

        assertNotNull(result);
        assertFalse(warehouse.isStatus());
        verify(warehouseRepository).save(warehouse);
    }

    // ✅ FIND ACTIVE STATUS
    @Test
    void testFindActiveStatus() {
        warehouse.setStatus(true);

        Warehouse inactive = new Warehouse();
        inactive.setStatus(false);

        List<Warehouse> list = List.of(warehouse, inactive);

        when(warehouseRepository.findAll()).thenReturn(list);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(warehouseDto));

        List<WarehouseDto> result = warehouseService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}