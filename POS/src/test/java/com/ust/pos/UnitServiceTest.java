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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // SAVE

    @Test
    void save_Success() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Unit entity = new Unit();

        Mockito.when(unitRepository.findByIdentifierAndIsDeleteFalse("U1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Unit.class))
                .thenReturn(entity);

        Mockito.when(unitRepository.save(entity))
                .thenReturn(entity);

        UnitDto result = unitService.save(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(unitRepository).save(entity);
    }

    @Test
    void save_WhenExists_ShouldFail() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Mockito.when(unitRepository.findByIdentifierAndIsDeleteFalse("U1"))
                .thenReturn(new Unit());

        UnitDto result = unitService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Model - U1 already exists", result.getMessage());

        Mockito.verify(unitRepository, Mockito.never())
                .save(Mockito.any());
    }

    // UPDATE (OPTIONAL FLOW)

    @Test
    void update_WhenNotFound_ShouldFail() {

        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("U1");

        Mockito.when(unitRepository.findById(1L))
                .thenReturn(Optional.empty());

        UnitDto result = unitService.update(dto);

        Assertions.assertFalse(result.isSuccess());
    }

    @Test
    void update_WhenDuplicateIdentifier_ShouldFail() {

        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("U2");

        Unit existing = new Unit();
        existing.setIdentifier("U1");

        Mockito.when(unitRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(unitRepository.findByIdentifierAndIsDeleteFalse("U2"))
                .thenReturn(new Unit());

        UnitDto result = unitService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Model Already Exists", result.getMessage());

        Mockito.verify(unitRepository, Mockito.never())
                .save(Mockito.any());
    }

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

        Mockito.when(unitRepository.save(existing))
                .thenReturn(existing);

        UnitDto result = unitService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(unitRepository).save(existing);
    }

    // DELETE (SOFT DELETE)

    @Test
    void delete_Success() {

        Unit entity = new Unit();
        entity.setIdentifier("U1");

        Mockito.when(unitRepository.findByIdentifierAndIsDeleteFalse("U1"))
                .thenReturn(entity);

        Mockito.when(unitRepository.save(entity)).thenReturn(entity);

        unitService.delete("U1");

        Assertions.assertTrue(entity.isDelete());

        Mockito.verify(unitRepository).save(entity);
    }

    @Test
    void delete_WhenNotFound_ShouldDoNothing() {

        Mockito.when(unitRepository.findByIdentifierAndIsDeleteFalse("U1"))
                .thenReturn(null);

        unitService.delete("U1");

        Mockito.verify(unitRepository, Mockito.never())
                .save(Mockito.any());
    }

    // STATUS UPDATE

    @Test
    void updateStatusOnly_Success() {

        Unit entity = new Unit();
        entity.setIdentifier("U1");
        entity.setStatus(false);

        Mockito.when(unitRepository.findByIdentifierAndIsDeleteFalse("U1"))
                .thenReturn(entity);

        Mockito.when(unitRepository.save(entity)).thenReturn(entity);

        unitService.updateStatusOnly("U1", true);

        Assertions.assertTrue(entity.getStatus());

        Mockito.verify(unitRepository).save(entity);
    }

    // FIND BY IDENTIFIER

    @Test
    void findByIdentifier_Success() {

        Unit entity = new Unit();
        entity.setIdentifier("U1");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Mockito.when(unitRepository.findByIdentifierAndIsDeleteFalse("U1"))
                .thenReturn(entity);

        Mockito.when(modelMapper.map(entity, UnitDto.class))
                .thenReturn(dto);

        UnitDto result = unitService.findByIdentifier("U1");

        Assertions.assertEquals("U1", result.getIdentifier());
    }

    // FIND ALL

    @Test
    void findAll_List_Success() {

        List<Unit> entities = List.of(new Unit());
        List<UnitDto> dtos = List.of(new UnitDto());

        Type type = new TypeToken<List<UnitDto>>() {
        }.getType();

        Mockito.when(unitRepository.findByIsDeleteFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, type))
                .thenReturn(dtos);

        List<UnitDto> result = unitService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // PAGINATION

    @Test
    void findAll_Pageable_NoSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Unit entity = new Unit();
        entity.setIdentifier("U1");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Page<Unit> page = new PageImpl<>(List.of(entity));

        Mockito.when(unitRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entity, UnitDto.class))
                .thenReturn(dto);

        Page<UnitDto> result = unitService.findAll(pageable, null);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void findAllPageableWithSearchTest() {
        Pageable pageable =
                PageRequest.of(0, 10);
        Unit unit = new Unit();
        Page<Unit> page =
                new PageImpl<>(List.of(unit));
        Mockito.when(unitRepository.findAll(
                        Mockito.<Specification<Unit>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);
        Page<UnitDto> result =
                unitService.findAll(pageable, "Admin");
        Assertions.assertEquals(
                1,
                result.getContent().size()
        );
        Mockito.verify(unitRepository)
                .findAll(
                        Mockito.<Specification<Unit>>any(),
                        Mockito.eq(pageable)
                );
    }

    @Test
    void findAll_WithBlankSearch_ShouldFallback() {

        Pageable pageable = PageRequest.of(0, 10);

        Unit entity = new Unit();
        entity.setIdentifier("U1");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Page<Unit> page = new PageImpl<>(List.of(entity));

        Mockito.when(unitRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entity, UnitDto.class))
                .thenReturn(dto);

        Page<UnitDto> result = unitService.findAll(pageable, " ");

        Assertions.assertEquals(1, result.getContent().size());
    }
}