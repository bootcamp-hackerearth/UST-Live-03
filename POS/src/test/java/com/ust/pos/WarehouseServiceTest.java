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
import org.springframework.data.jpa.domain.Specification;

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
        warehouse.setIdentifier("WH1");
        warehouse.setStatus(true);
        warehouse.setDeleted(false);

        warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH1");
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Warehouse> page =
                new PageImpl<>(Collections.singletonList(warehouse));

        when(warehouseRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(warehouseDto));

        WsDto<WarehouseDto> result =
                warehouseService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Warehouse> specification =
                mock(Specification.class);

        Page<Warehouse> page =
                new PageImpl<>(Collections.singletonList(warehouse));

        when(warehouseRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(warehouseDto));

        WsDto<WarehouseDto> result =
                warehouseService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());

        verify(warehouseRepository)
                .findAll(specification, pageable);
    }

    @Test
    void testSave_NewWarehouse() {

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(null);

        when(modelMapper.map(warehouseDto, Warehouse.class))
                .thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertNotNull(result);

        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void testSave_AlreadyExists() {

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));

        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void testDelete() {

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(warehouse);

        warehouseService.delete("WH1");

        assertTrue(warehouse.isDeleted());
        assertFalse(warehouse.isStatus());

        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void testFindByIdentifier() {

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(warehouse);

        when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(warehouseDto);

        WarehouseDto result =
                warehouseService.findByIdentifier("WH1");

        assertNotNull(result);
        assertEquals("WH1", result.getIdentifier());
    }

    @Test
    void testUpdate() {

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(warehouse);

        doNothing().when(modelMapper)
                .map(warehouseDto, warehouse);

        WarehouseDto result =
                warehouseService.update(warehouseDto);

        assertNotNull(result);

        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void testChangeToggleStatus() {

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(warehouse);

        when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(warehouseDto);

        WarehouseDto result =
                warehouseService.changeToggleStatus("WH1", false);

        assertNotNull(result);
        assertFalse(warehouse.isStatus());

        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void testChangeToggleStatus_WarehouseNotFound() {

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(null);

        when(modelMapper.map(null, WarehouseDto.class))
                .thenReturn(null);

        WarehouseDto result =
                warehouseService.changeToggleStatus("WH1", false);

        assertNull(result);
    }

    @Test
    void testFindActiveStatus() {

        Warehouse inactiveWarehouse = new Warehouse();
        inactiveWarehouse.setStatus(false);

        when(warehouseRepository.findAll())
                .thenReturn(List.of(warehouse, inactiveWarehouse));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(warehouseDto));

        List<WarehouseDto> result =
                warehouseService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}