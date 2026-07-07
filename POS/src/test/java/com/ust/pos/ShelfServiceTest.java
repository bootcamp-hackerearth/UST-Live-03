package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
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
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl shelfService;

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    // ---------------- SAVE ----------------

    @Test
    void save_Success() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf entity = new Shelf();

        Mockito.when(shelfRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Shelf.class))
                .thenReturn(entity);

        Mockito.when(shelfRepository.save(entity))
                .thenReturn(entity);

        ShelfDto result = shelfService.save(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(shelfRepository).save(entity);
    }

    @Test
    void save_WhenExists_ShouldFail() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(new Shelf());

        ShelfDto result = shelfService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Shelf already exists", result.getMessage());

        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
    }

    // UPDATE (OPTIONAL LOGIC)

    @Test
    void update_WhenNotFound_ShouldFail() {

        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.findById(1L))
                .thenReturn(Optional.empty());

        ShelfDto result = shelfService.update(dto);

        Assertions.assertFalse(result.isSuccess());
    }

    @Test
    void update_WhenDuplicateIdentifier_ShouldFail() {

        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("S2");

        Shelf existing = new Shelf();
        existing.setIdentifier("S1");

        Mockito.when(shelfRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(shelfRepository.findByIdentifier("S2"))
                .thenReturn(new Shelf());

        ShelfDto result = shelfService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Shelf already exists", result.getMessage());

        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void update_Success() {

        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("S1");

        Shelf existing = new Shelf();
        existing.setIdentifier("S1");

        Mockito.when(shelfRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.doNothing().when(modelMapper).map(dto, existing);

        Mockito.when(shelfRepository.save(existing))
                .thenReturn(existing);

        ShelfDto result = shelfService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(shelfRepository).save(existing);
    }

    //  DELETE (SOFT DELETE)

    @Test
    void delete_Success() {

        Shelf entity = new Shelf();
        entity.setIdentifier("S1");

        Mockito.when(shelfRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(entity);

        Mockito.when(shelfRepository.save(entity)).thenReturn(entity);

        shelfService.delete("S1");

        Assertions.assertTrue(entity.isDelete());

        Mockito.verify(shelfRepository).save(entity);
    }

    @Test
    void delete_WhenNotFound_ShouldDoNothing() {

        Mockito.when(shelfRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(null);

        shelfService.delete("S1");

        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
    }

    // STATUS UPDATE

    @Test
    void updateStatusOnly_Success() {

        Shelf entity = new Shelf();
        entity.setIdentifier("S1");
        entity.setStatus(false);

        Mockito.when(shelfRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(entity);

        Mockito.when(shelfRepository.save(entity)).thenReturn(entity);

        shelfService.updateStatusOnly("S1", true);

        Assertions.assertTrue(entity.getStatus());

        Mockito.verify(shelfRepository).save(entity);
    }

    // FIND BY IDENTIFIER

    @Test
    void findByIdentifier_Success() {

        Shelf entity = new Shelf();
        entity.setIdentifier("S1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(entity);

        Mockito.when(modelMapper.map(entity, ShelfDto.class))
                .thenReturn(dto);

        ShelfDto result = shelfService.findByIdentifier("S1");

        Assertions.assertEquals("S1", result.getIdentifier());
    }

    // FIND ALL

    @Test
    void findAll_Success() {

        List<Shelf> entities = List.of(new Shelf());
        List<ShelfDto> dtos = List.of(new ShelfDto());

        Type type = new TypeToken<List<ShelfDto>>() {
        }.getType();

        Mockito.when(shelfRepository.findByIsDeleteFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, type))
                .thenReturn(dtos);

        List<ShelfDto> result = shelfService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // FIND ALL BY STATUS

    @Test
    void findAllByStatus_Success() {

        Shelf activeShelf = new Shelf();
        activeShelf.setStatus(true);

        Shelf inactiveShelf = new Shelf();
        inactiveShelf.setStatus(false);

        Type type = new TypeToken<List<ShelfDto>>() {
        }.getType();

        ShelfDto activeDto = new ShelfDto();
        activeDto.setStatus(true);

        ShelfDto inactiveDto = new ShelfDto();
        inactiveDto.setStatus(false);

        Mockito.when(shelfRepository.findByIsDeleteFalse())
                .thenReturn(List.of(activeShelf, inactiveShelf));

        Mockito.when(modelMapper.map(List.of(activeShelf, inactiveShelf), type))
                .thenReturn(List.of(activeDto, inactiveDto));

        List<ShelfDto> result = shelfService.findAllByStatus();

        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.get(0).getStatus());
    }

    // PAGINATION

    @Test
    void findAll_Pageable_Success() {

        Pageable pageable = PageRequest.of(0, 10);

        Shelf entity = new Shelf();
        entity.setIdentifier("S1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Page<Shelf> page = new PageImpl<>(List.of(entity));

        Mockito.when(shelfRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(page.getContent(),
                        new TypeToken<List<ShelfDto>>() {
                        }.getType()))
                .thenReturn(List.of(dto));

        List<ShelfDto> result = shelfService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
    }

    // PAGINATION SEARCH

    @Test
    void findAllPageableWithSearchTest() {
        Pageable pageable =
                PageRequest.of(0, 10);
        Shelf shelf = new Shelf();
        Page<Shelf> page =
                new PageImpl<>(List.of(shelf));
        Mockito.when(shelfRepository.findAll(
                        Mockito.<Specification<Shelf>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);
        Page<ShelfDto> result =
                shelfService.findAll(pageable, "Admin");
        Assertions.assertEquals(
                1,
                result.getContent().size()
        );
        Mockito.verify(shelfRepository)
                .findAll(
                        Mockito.<Specification<Shelf>>any(),
                        Mockito.eq(pageable)
                );
    }

    @Test
    void findAll_WithBlankSearch_ShouldFallback() {

        Pageable pageable = PageRequest.of(0, 10);

        Shelf entity = new Shelf();
        entity.setIdentifier("S1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Page<Shelf> page = new PageImpl<>(List.of(entity));

        Mockito.when(shelfRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entity, ShelfDto.class))
                .thenReturn(dto);

        Page<ShelfDto> result = shelfService.findAll(pageable, " ");

        Assertions.assertEquals(1, result.getContent().size());
    }
}