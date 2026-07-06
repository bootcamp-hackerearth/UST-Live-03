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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
    void testFindByIdentifier_Success() {
        String identifier = "WH-001";

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier(identifier);

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier(identifier);

        when(warehouseRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(warehouseDto);

        WarehouseDto result = warehouseService.findByIdentifier(identifier);

        assertEquals(identifier, result.getIdentifier());
    }

    @Test
    void testSave_Success() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH-001");

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH-001");

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(null);
        when(modelMapper.map(warehouseDto, Warehouse.class))
                .thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void testSave_AlreadyExists() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH-001");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("WH-001");
        existingWarehouse.setDeleted(false);

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(existingWarehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertFalse(result.isSuccess());
        assertEquals("Warehouse with identifier - WH-001 already exists", result.getMessage());

        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void testSave_DeletedWarehouseExists() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH-001");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("WH-001");
        existingWarehouse.setDeleted(true);

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(existingWarehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Warehouse with identifier - WH-001 was deleted and cannot be created again.",
                result.getMessage()
        );

        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void testUpdate_Success() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH-001");

        Warehouse existingWarehouse = new Warehouse();
        existingWarehouse.setIdentifier("WH-001");

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(existingWarehouse);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertNotNull(result);

        verify(modelMapper).map(warehouseDto, existingWarehouse);
        verify(warehouseRepository).save(existingWarehouse);
    }

    @Test
    void testUpdate_NotFound() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH-001");

        when(warehouseRepository.findByIdentifier("WH-001"))
                .thenReturn(null);

        WarehouseDto result = warehouseService.update(warehouseDto);

        assertFalse(result.isSuccess());
        assertEquals("Warehouse with identifier - WH-001 not found", result.getMessage());

        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "WH-001";

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier(identifier);
        warehouse.setDeleted(false);

        when(warehouseRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(warehouse);

        warehouseService.delete(identifier);

        assertTrue(warehouse.getDeleted());

        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH-001");

        List<Warehouse> warehouseList = List.of(warehouse);
        Page<Warehouse> warehousePage =
                new PageImpl<>(warehouseList, pageable, warehouseList.size());

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH-001");

        List<WarehouseDto> dtoList = List.of(warehouseDto);

        when(warehouseRepository.findAllByDeletedFalse(pageable))
                .thenReturn(warehousePage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

}