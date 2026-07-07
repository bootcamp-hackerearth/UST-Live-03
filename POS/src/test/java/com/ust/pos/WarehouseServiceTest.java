package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        dto.setIdentifier("WH1");

        Warehouse warehouse = new Warehouse();

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Warehouse.class))
                .thenReturn(warehouse);

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertEquals("WH1", response.getIdentifier());
        assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void saveTestFailure() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(new Warehouse());

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestSuccess() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse existing = new Warehouse();

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(existing);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertEquals("WH1", response.getIdentifier());
        assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(modelMapper).map(dto, existing);
        verify(warehouseRepository).save(existing);
    }

    @Test
    void updateTestFailure() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(null);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {

        Warehouse warehouse = new Warehouse();

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(warehouse);

        warehouseService.delete("WH1");

        assertTrue(warehouse.isDeleted());

        verify(warehouseRepository)
                .findByIdentifier("WH1");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Warehouse warehouse = new Warehouse();
        WarehouseDto dto = new WarehouseDto();

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(warehouse);

        when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        WarehouseDto response = warehouseService.findByIdentifier("WH1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(dto, response);
    }

    @Test
    void findByIdentifierFailureTest() {

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(null);

        ResourseNotFoundException exception =
                Assertions.assertThrows(
                        ResourseNotFoundException.class,
                        () -> warehouseService.findByIdentifier("WH1")
                );

        Assertions.assertEquals(
                "Data Cannot found WH1",
                exception.getMessage()
        );
    }

    @Test
    void findActiveWarehouseTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        when(warehouseRepository.findByStatus(true))
                .thenReturn(List.of(warehouse));

        when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        List<WarehouseDto> result =
                warehouseService.findActiveWarehouse();

        assertEquals(1, result.size());
        assertEquals("WH1",
                result.getFirst().getIdentifier());

        verify(warehouseRepository)
                .findByStatus(true);
    }

    @Test
    void toggleStatusTrueToFalseTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");
        warehouse.setStatus(true);

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(warehouse);

        warehouseService.toggleStatus("WH1");

        assertFalse(warehouse.isStatus());

        verify(warehouseRepository)
                .save(warehouse);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");
        warehouse.setStatus(false);

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(warehouse);

        warehouseService.toggleStatus("WH1");

        assertTrue(warehouse.isStatus());

        verify(warehouseRepository)
                .save(warehouse);
    }

    @Test
    void toggleStatusWarehouseNotFoundTest() {

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(null);

        warehouseService.toggleStatus("WH1");

        verify(warehouseRepository)
                .findByIdentifier("WH1");

        verify(warehouseRepository, never())
                .save(any());
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Page<Warehouse> page =
                new PageImpl<>(List.of(warehouse), pageable, 1);

        when(warehouseRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        var result = warehouseService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("WH1",
                result.getContent().getFirst().getIdentifier());

        assertEquals(0, result.getPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalRecords());

        verify(warehouseRepository)
                .findByIsDeletedFalse(pageable);

        verify(modelMapper)
                .map(warehouse, WarehouseDto.class);
    }

    @Test
    void saveTestFailure_deletedWarehouse() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse existing = new Warehouse();
        existing.setDeleted(true);

        when(warehouseRepository.findByIdentifier("WH1"))
                .thenReturn(existing);

        WarehouseDto response = warehouseService.save(dto);

        assertFalse(response.isSuccess());

        assertTrue(
                response.getMessage()
                        .contains("already exists but was deleted")
        );
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Warehouse> specification = mock(Specification.class);

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH001");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH001");

        List<Warehouse> warehouseList = List.of(warehouse);

        Page<Warehouse> page =
                new PageImpl<>(warehouseList, pageable, 1);

        when(warehouseRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(warehouseList), any(Type.class)))
                .thenReturn(List.of(warehouseDto));

        WsDto<WarehouseDto> result =
                warehouseService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("WH001",
                result.getContent().get(0).getIdentifier());
        assertEquals(1L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(warehouseRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(warehouseList), any(Type.class));
    }

    @Test
    void findAllWithSpecificationEmptyResultTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Warehouse> specification = mock(Specification.class);

        Page<Warehouse> page =
                new PageImpl<>(List.of(), pageable, 0);

        when(warehouseRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(List.of()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<WarehouseDto> result =
                warehouseService.findAll(specification, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalRecords());
        assertEquals(0, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(warehouseRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(List.of()), any(Type.class));
    }

}
