package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @InjectMocks
    private UnitServiceImpl unitService;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    // ================= SAVE =================

    @Test
    void save_Success() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Unit entity = new Unit();

        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Unit.class))
                .thenReturn(entity);

        Mockito.when(unitRepository.save(entity))
                .thenReturn(entity);

        UnitDto response = unitService.save(dto);

        Assertions.assertEquals("U1", response.getIdentifier());
        Mockito.verify(unitRepository).save(entity);
    }

    @Test
    void save_Failure_WhenExists() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(new Unit());

        UnitDto response = unitService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    // ================= UPDATE =================

    @Test
    void update_Success() {

        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("U1");

        Unit existing = new Unit();
        existing.setId(1L);
        existing.setIdentifier("U1");

        Mockito.when(unitRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.doNothing().when(modelMapper).map(dto, existing);

        Mockito.when(unitRepository.save(existing))
                .thenReturn(existing);

        UnitDto response = unitService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(unitRepository).save(existing);
    }

    @Test
    void update_Failure_WhenNotFound() {
        UnitDto dto = new UnitDto();
        dto.setId(1L);

        Mockito.when(unitRepository.findById(1L))
                .thenReturn(Optional.empty());

        UnitDto response = unitService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void update_Failure_WhenIdentifierAlreadyExists() {
        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");

        Unit existing = new Unit();
        existing.setId(1L);
        existing.setIdentifier("OLD");

        Mockito.when(unitRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("NEW"))
                .thenReturn(new Unit());

        UnitDto response = unitService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("Already Exists"));

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void findByIdentifier_Success() {
        Unit entity = new Unit();
        UnitDto dto = new UnitDto();

        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(entity);

        Mockito.when(modelMapper.map(entity, UnitDto.class))
                .thenReturn(dto);

        UnitDto response = unitService.findByIdentifier("U1");

        Assertions.assertNotNull(response);
    }


    @Test
    void findAll_List() {
        List<Unit> entities = List.of(new Unit());
        List<UnitDto> dtos = List.of(new UnitDto());

        Type listType = new TypeToken<List<UnitDto>>() {}.getType();

        Mockito.when(unitRepository.findByDeletedFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<UnitDto> response = unitService.findAll();

        Assertions.assertEquals(1, response.size());
    }


    @Test
    void findAll_WithSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Unit unit = new Unit();
        UnitDto dto = new UnitDto();

        Page<Unit> page = new PageImpl<>(List.of(unit));

        Mockito.when(unitRepository.findAll(
                        Mockito.<Specification<Unit>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        Page<UnitDto> response =
                unitService.findAll(pageable, "U");

        Assertions.assertEquals(1, response.getContent().size());

        Mockito.verify(unitRepository)
                .findAll(
                        Mockito.<Specification<Unit>>any(),
                        Mockito.eq(pageable)
                );
    }

    @Test
    void findAll_WithoutSearch() {
        Pageable pageable = PageRequest.of(0, 10);

        Unit unit = new Unit();
        Page<Unit> page = new PageImpl<>(List.of(unit));

        UnitDto dto = new UnitDto();

        Mockito.when(unitRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        Page<UnitDto> response = unitService.findAll(pageable, null);

        Assertions.assertEquals(1, response.getContent().size());
    }


    @Test
    void delete_Success() {
        Unit unit = new Unit();
        unit.setDeleted(false);

        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(unit);

        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        unitService.delete("U1");

        Assertions.assertTrue(unit.isDeleted());
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void delete_WhenNotFound() {
        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(null);

        unitService.delete("U1");

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void toggleStatus_Success() {
        Unit unit = new Unit();
        unit.setStatus(true);

        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(unit);

        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        unitService.toggleStatus("U1");

        Assertions.assertFalse(unit.isStatus());
        Mockito.verify(unitRepository).save(unit);
    }
}