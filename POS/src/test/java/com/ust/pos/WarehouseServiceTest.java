package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Warehouse;
import com.ust.pos.modell.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

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
    void findByIdentifier_success() {
        Warehouse entity = new Warehouse();
        entity.setIdentifier("W1");
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        dto.setSuccess(true);
        when(warehouseRepository.findByIdentifierAndDeletedFalse("W1")).thenReturn(entity);
        when(modelMapper.map(entity, WarehouseDto.class)).thenReturn(dto);
        WarehouseDto response = warehouseService.findByIdentifier("W1");
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("W1", response.getIdentifier());
    }

    @Test
    void findByIdentifier_notFound() {
        when(warehouseRepository.findByIdentifierAndDeletedFalse("W1")).thenReturn(null);
        ResourceNotFoundException ex = Assertions.assertThrows(ResourceNotFoundException.class, () -> {warehouseService.findByIdentifier("W1");});
        Assertions.assertEquals("Warehouse with identifier 'W1' not found", ex.getMessage());
    }

    @Test
    void save_success() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        dto.setSuccess(true);
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);
        Warehouse entity = new Warehouse();
        when(modelMapper.map(dto, Warehouse.class)).thenReturn(entity);
        when(warehouseRepository.save(entity)).thenReturn(entity);
        WarehouseDto response = warehouseService.save(dto);
        Assertions.assertEquals("W1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void save_failure_duplicate() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        Warehouse existing = new Warehouse();
        existing.setDeleted(false);
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(existing);
        WarehouseDto response = warehouseService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
    }

    @Test
    void save_failure_deleted() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        Warehouse existing = new Warehouse();
        existing.setDeleted(true);
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(existing);
        WarehouseDto response = warehouseService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("was deleted and cannot be created again"));
    }

    @Test
    void update_success() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        dto.setSuccess(true);
        Warehouse existing = new Warehouse();
        existing.setIdentifier("W1");
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(existing);
        when(warehouseRepository.save(existing)).thenReturn(existing);
        WarehouseDto response = warehouseService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void update_failure_notFound() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);
        WarehouseDto response = warehouseService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void delete_test() {
        Warehouse entity = new Warehouse();
        entity.setIdentifier("W1");
        when(warehouseRepository.findByIdentifierAndDeletedFalse("W1")).thenReturn(entity);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(entity);
        warehouseService.delete("W1");
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void findAllTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("A1");
        Pageable pageable = PageRequest.of(0, 50);
        Page<Warehouse> page = new PageImpl<>(List.of(warehouse), pageable, 1);
        when(warehouseRepository.findALlByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(java.lang.reflect.Type.class))).thenReturn(List.of(new WarehouseDto()));
        WsDto<WarehouseDto> response = warehouseService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findAllActiveTest() {
        Warehouse entity = new Warehouse();
        entity.setIdentifier("W1");
        when(warehouseRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(entity));
        when(modelMapper.map(entity, WarehouseDto.class)).thenReturn(new WarehouseDto());
        List<WarehouseDto> result = warehouseService.findAllActive();
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void toggleStatus_trueToFalse() {
        Warehouse entity = new Warehouse();
        entity.setIdentifier("W1");
        entity.setStatus(true);
        WarehouseDto dto = new WarehouseDto();
        when(warehouseRepository.findByIdentifierAndDeletedFalse("W1")).thenReturn(entity);
        when(warehouseRepository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, WarehouseDto.class)).thenReturn(dto);
        WarehouseDto result = warehouseService.toggleStatus("W1");
        Assertions.assertFalse(entity.getStatus());
        Assertions.assertNotNull(result);
        verify(warehouseRepository).save(entity);
    }

    @Test
    void toggleStatus_nullToTrue() {
        Warehouse entity = new Warehouse();
        entity.setIdentifier("W1");
        entity.setStatus(null);
        WarehouseDto dto = new WarehouseDto();
        when(warehouseRepository.findByIdentifierAndDeletedFalse("W1")).thenReturn(entity);
        when(warehouseRepository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, WarehouseDto.class)).thenReturn(dto);
        WarehouseDto result = warehouseService.toggleStatus("W1");
        Assertions.assertTrue(entity.getStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void toggleStatus_failure_notFound() {
        when(warehouseRepository.findByIdentifierAndDeletedFalse("W1")).thenReturn(null);
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {warehouseService.toggleStatus("W1");});
        Assertions.assertTrue(ex.getMessage().contains("Warehouse not found with identifier"));
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }
}