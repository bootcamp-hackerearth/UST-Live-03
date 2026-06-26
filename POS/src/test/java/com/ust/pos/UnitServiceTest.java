package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
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

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UnitServiceImpl unitService;

    private UnitDto unitDto;
    private Unit unit;

    @BeforeEach
    void setUp() {
        unitDto = new UnitDto();
        unitDto.setIdentifier("UNT-001");

        unit = new Unit();
        unit.setIdentifier("UNT-001");
        unit.setStatus(true);
        unit.setDeleted(false);
    }

    @Test
    @DisplayName("Save Unit - Success")
    void save_Success() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(null);
        when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("added Successfully"));
        verify(unitRepository).save(unit);
    }

    @Test
    @DisplayName("Save Unit - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        unit.setDeleted(false);
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    @DisplayName("Save Unit - Failure: Previously Soft-Deleted")
    void save_Failure_PreviouslyDeleted() {
        unit.setDeleted(true);
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    @DisplayName("Find All Units - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Unit> unitPage = new PageImpl<>(List.of(unit));

        when(unitRepository.findByDeletedFalse(pageable)).thenReturn(unitPage);
        when(modelMapper.map(eq(unitPage.getContent()), any(Type.class))).thenReturn(List.of(unitDto));

        List<UnitDto> result = unitService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find All Active Units - Success")
    void findAllActive_Success() {
        List<Unit> activeUnits = List.of(unit);
        when(unitRepository.findAllByStatusAndDeletedFalse(true)).thenReturn(activeUnits);
        when(modelMapper.map(eq(activeUnits), any(Type.class))).thenReturn(List.of(unitDto));

        List<UnitDto> result = unitService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.findByIdentifier("UNT-001");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Update Unit - Success")
    void update_Success() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(unit);

        UnitDto result = unitService.update(unitDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("Updated"));
        verify(unitRepository).save(unit);
    }

    @Test
    @DisplayName("Update Unit - Failure: Not Found")
    void update_Failure_NotFound() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(null);

        UnitDto result = unitService.update(unitDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.toggleStatus("UNT-001");

        Assertions.assertFalse(unit.isStatus());
        verify(unitRepository).save(unit);
    }

    @Test
    @DisplayName("Delete Unit - Success")
    void delete_Success() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(unit);

        boolean result = unitService.delete("UNT-001");

        Assertions.assertTrue(result);
        verify(unitRepository).save(unit);
    }

    @Test
    @DisplayName("Delete Unit - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(unitRepository.findByIdentifier("UNT-001")).thenReturn(null);

        boolean result = unitService.delete("UNT-001");

        Assertions.assertFalse(result);
        verify(unitRepository, never()).save(any(Unit.class));
    }
}