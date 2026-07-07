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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    public static final String INVALID = "INVALID";
    @InjectMocks
    private WarehouseServiceImpl service;

    @Mock
    private WarehouseRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifierTest() {

        Warehouse warehouse = new Warehouse();
        WarehouseDto dto = new WarehouseDto();

        when(repository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(warehouse);

        when(mapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        assertNotNull(service.findByIdentifier("WH1"));

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByIdentifier(INVALID)
                );

        assertEquals(
                "Warehouse with identifier 'INVALID' not found",
                exception.getMessage()
        );
    }

    @Test
    void saveTest() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse warehouse = new Warehouse();
        warehouse.setStatus(null);

        when(repository.findByIdentifier("WH1"))
                .thenReturn(null);

        when(mapper.map(dto, Warehouse.class))
                .thenReturn(warehouse);

        WarehouseDto result = service.save(dto);

        verify(repository).save(warehouse);

        assertTrue(result.isSuccess());
        assertTrue(warehouse.getStatus());

        Warehouse warehouseWithStatus = new Warehouse();
        warehouseWithStatus.setStatus(false);

        when(repository.findByIdentifier("WH2"))
                .thenReturn(null);

        WarehouseDto dto2 = new WarehouseDto();
        dto2.setIdentifier("WH2");

        when(mapper.map(dto2, Warehouse.class))
                .thenReturn(warehouseWithStatus);

        result = service.save(dto2);

        assertFalse(warehouseWithStatus.getStatus());

        Warehouse duplicate = new Warehouse();
        duplicate.setDeleted(false);

        when(repository.findByIdentifier("WH1"))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Warehouse with identifier - WH1 already exists",
                result.getMessage()
        );

        duplicate.setDeleted(true);

        when(repository.findByIdentifier("WH1"))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Warehouse with Identifier WH1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateAndDeleteTest() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");
        warehouse.setCreatedBy("admin");
        warehouse.setCreatedOn(LocalDateTime.now());

        when(repository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(warehouse);

        WarehouseDto result = service.update(dto);

        assertTrue(result.isSuccess());

        verify(mapper).map(dto, warehouse);
        verify(repository).save(warehouse);

        WarehouseDto invalidDto = new WarehouseDto();
        invalidDto.setIdentifier(INVALID);

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        result = service.update(invalidDto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Warehouse with identifier - INVALID not found",
                result.getMessage()
        );

        service.delete("WH1");

        verify(repository, atLeast(2))
                .save(any(Warehouse.class));

        when(repository.findByIdentifierAndDeletedFalse("NOTFOUND"))
                .thenReturn(null);

        service.delete("NOTFOUND");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Warehouse> page =
                new PageImpl<>(
                        List.of(new Warehouse()),
                        pageable,
                        1
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new WarehouseDto()));

        WsDto<WarehouseDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());

        Specification<Warehouse> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<WarehouseDto> specResult =
                service.findAll(specification, pageable);

        assertEquals(1, specResult.getDtoList().size());
        assertEquals(1, specResult.getTotalRecords());

        verify(repository)
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void toggleStatusTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setStatus(true);

        when(repository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(warehouse);

        service.toggleStatus("WH1");

        assertFalse(warehouse.getStatus());

        verify(repository).save(warehouse);

        Warehouse nullStatusWarehouse = new Warehouse();
        nullStatusWarehouse.setStatus(null);

        when(repository.findByIdentifierAndDeletedFalse("WH2"))
                .thenReturn(nullStatusWarehouse);

        service.toggleStatus("WH2");

        assertTrue(nullStatusWarehouse.getStatus());

        when(repository.findByIdentifierAndDeletedFalse("WH3"))
                .thenReturn(null);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.toggleStatus("WH3")
                );

        assertEquals(
                "Warehouse not found: WH3",
                exception.getMessage()
        );
    }

    @Test
    void findAllActiveTest() {

        Warehouse warehouse = new Warehouse();
        WarehouseDto dto = new WarehouseDto();

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(warehouse));

        when(mapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        List<WarehouseDto> result =
                service.findAllActive();

        assertEquals(1, result.size());

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(Collections.emptyList());

        result = service.findAllActive();

        assertTrue(result.isEmpty());
    }
}