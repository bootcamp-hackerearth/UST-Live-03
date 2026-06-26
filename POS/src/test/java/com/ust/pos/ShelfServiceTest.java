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
import org.springframework.data.domain.*;

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


    @Test
    void saveTest_Success() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf entity = new Shelf();

        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Shelf.class))
                .thenReturn(entity);

        Mockito.when(shelfRepository.save(entity))
                .thenReturn(entity);

        ShelfDto response = shelfService.save(dto);

        Assertions.assertEquals("S1", response.getIdentifier());

        Mockito.verify(shelfRepository).save(entity);
    }


    @Test
    void saveTest_Failure_WhenAlreadyExists() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(new Shelf());

        ShelfDto response = shelfService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void updateTest_Success() {

        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("S1");

        Shelf existing = new Shelf();
        existing.setId(1L);
        existing.setIdentifier("S1");

        Mockito.when(shelfRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.doNothing().when(modelMapper).map(dto, existing);

        Mockito.when(shelfRepository.save(existing))
                .thenReturn(existing);

        ShelfDto response = shelfService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(shelfRepository).save(existing);
    }


    @Test
    void updateTest_Failure_WhenIdNotFound() {

        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.findById(1L))
                .thenReturn(Optional.empty());

        ShelfDto response = shelfService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void updateTest_Failure_WhenIdentifierExists() {

        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");

        Shelf existing = new Shelf();
        existing.setId(1L);
        existing.setIdentifier("OLD");

        Mockito.when(shelfRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(shelfRepository.findByIdentifier("NEW"))
                .thenReturn(new Shelf());

        ShelfDto response = shelfService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf already exists", response.getMessage());
    }


    @Test
    void findByIdentifierTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(shelf);

        Mockito.when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        ShelfDto response = shelfService.findByIdentifier("S1");

        Assertions.assertEquals("S1", response.getIdentifier());
    }


    @Test
    void findAllTest() {

        List<Shelf> entities = List.of(new Shelf());
        List<ShelfDto> dtos = List.of(new ShelfDto());

        Type listType = new TypeToken<List<ShelfDto>>() {}.getType();

        Mockito.when(shelfRepository.findByDeletedFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<ShelfDto> response = shelfService.findAll();

        Assertions.assertEquals(1, response.size());
    }


    @Test
    void toggleStatusTest() {

        Shelf shelf = new Shelf();
        shelf.setStatus(false);

        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(shelf);

        Mockito.when(shelfRepository.save(shelf))
                .thenReturn(shelf);

        shelfService.toggleStatus("S1");

        Assertions.assertTrue(shelf.isStatus());

        Mockito.verify(shelfRepository).save(shelf);
    }


    @Test
    void findAllByStatusTest() {

        Shelf active = new Shelf();
        active.setStatus(true);

        Shelf inactive = new Shelf();
        inactive.setStatus(false);

        List<Shelf> entities = List.of(active, inactive);
        List<ShelfDto> dtos = List.of(
                new ShelfDto() {{ setStatus(true); }},
                new ShelfDto() {{ setStatus(false); }}
        );

        Type listType = new TypeToken<List<ShelfDto>>() {}.getType();

        Mockito.when(shelfRepository.findByDeletedFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<ShelfDto> result = shelfService.findAllByStatus();

        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.get(0).isStatus());
    }


    @Test
    void deleteTest() {

        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(shelf);

        shelfService.delete("S1");

        Assertions.assertTrue(shelf.isDeleted());

        Mockito.verify(shelfRepository).save(shelf);
    }


    @Test
    void findAll_WithPagination_ShouldReturnShelfDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Page<Shelf> page = new PageImpl<>(List.of(shelf));

        Mockito.when(shelfRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        Page<ShelfDto> response =
                shelfService.findAll(pageable, null);

        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals("S1",
                response.getContent().get(0).getIdentifier());

        Mockito.verify(shelfRepository).findByDeletedFalse(pageable);
    }


    @Test
    void findAll_WithSearch_ShouldReturnShelfDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        Shelf shelf = new Shelf();

        Page<Shelf> page = new PageImpl<>(List.of(shelf));

        Mockito.when(shelfRepository
                        .findByIdentifierContainingIgnoreCaseAndDeletedFalse("S1", pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(new ShelfDto());

        Page<ShelfDto> response =
                shelfService.findAll(pageable, "S1");

        Assertions.assertEquals(1, response.getContent().size());

        Mockito.verify(shelfRepository)
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse("S1", pageable);
    }
}