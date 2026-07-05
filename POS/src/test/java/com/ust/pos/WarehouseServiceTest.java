package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private WarehouseDto warehouseDto;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH-001");

        warehouse = new Warehouse();
        warehouse.setIdentifier("WH-001");
        warehouse.setStatus(true);
        warehouse.setDeleted(false);
    }

    @Test
    @DisplayName("Save Warehouse - Success")
    void save_Success() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);
        when(modelMapper.map(warehouseDto, Warehouse.class)).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("successfully"));
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    @DisplayName("Save Warehouse - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        warehouse.setDeleted(false);
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("Save Warehouse - Failure: Previously Soft-Deleted")
    void save_Failure_PreviouslyDeleted() {
        warehouse.setDeleted(true);
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.save(warehouseDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("previously deleted"));
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("Find All Warehouses - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Warehouse> warehousePage = new PageImpl<>(List.of(warehouse), pageable, 1);

        when(warehouseRepository.findByDeletedFalse(pageable)).thenReturn(warehousePage);
        when(modelMapper.map(eq(warehousePage.getContent()), any(Type.class))).thenReturn(List.of(warehouseDto));

        WsDto<WarehouseDto> result = warehouseService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    @DisplayName("Find All Warehouses with Specification - Success")
    void findAll_WithSpecification_Success() {
        Specification<Warehouse> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Warehouse> warehousePage = new PageImpl<>(List.of(warehouse), pageable, 1);

        when(warehouseRepository.findAll(spec, pageable)).thenReturn(warehousePage);
        when(modelMapper.map(eq(warehousePage.getContent()), any(Type.class))).thenReturn(List.of(warehouseDto));

        WsDto<WarehouseDto> result = warehouseService.findAll(spec, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
    }

    @Test
    @DisplayName("Find All Active Warehouses - Success")
    void findAllActive_Success() {
        List<Warehouse> activeWarehouses = List.of(warehouse);
        when(warehouseRepository.findAllByStatusAndDeletedFalse(true)).thenReturn(activeWarehouses);
        when(modelMapper.map(eq(activeWarehouses), any(Type.class))).thenReturn(List.of(warehouseDto));

        List<WarehouseDto> result = warehouseService.findAllActive();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);

        WarehouseDto result = warehouseService.findByIdentifier("WH-001");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Find By Identifier - Failure: Not Found Exception")
    void findByIdentifier_Failure_NotFound() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> warehouseService.findByIdentifier("WH-001"));
    }

    @Test
    @DisplayName("Update Warehouse - Success")
    void update_Success() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);

        WarehouseDto result = warehouseService.update(warehouseDto);

        Assertions.assertNotNull(result);
        verify(warehouseRepository).save(warehouse);
        verify(modelMapper).map(warehouseDto, warehouse);
    }

    @Test
    @DisplayName("Update Warehouse - Failure: Not Found")
    void update_Failure_NotFound() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        WarehouseDto result = warehouseService.update(warehouseDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);
        when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);

        WarehouseDto result = warehouseService.toggleStatus("WH-001");

        Assertions.assertFalse(warehouse.isStatus());
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    @DisplayName("Delete Warehouse - Success")
    void delete_Success() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(warehouse);

        boolean result = warehouseService.delete("WH-001");

        Assertions.assertTrue(result);
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    @DisplayName("Delete Warehouse - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(warehouseRepository.findByIdentifier("WH-001")).thenReturn(null);

        boolean result = warehouseService.delete("WH-001");

        Assertions.assertFalse(result);
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }
}