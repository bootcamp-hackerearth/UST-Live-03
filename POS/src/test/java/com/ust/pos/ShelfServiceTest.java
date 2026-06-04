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

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl shelfService;

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveShelfSuccess() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("S1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Shelf.class))
                .thenReturn(shelf);

        ShelfDto response = shelfService.save(dto);

        Assertions.assertNull(response.getMessage());
        Mockito.verify(shelfRepository).save(shelf);
    }
    @Test
    void findAll_WithPagination_ShouldReturnShelfDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Shelf> shelves = List.of(new Shelf());
        Page<Shelf> page = new PageImpl<>(shelves);

        List<ShelfDto> shelfDtos = List.of(new ShelfDto());

        Type listType = new TypeToken<List<ShelfDto>>() {}.getType();

        Mockito.when(shelfRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(shelves, listType))
                .thenReturn(shelfDtos);

        List<ShelfDto> response = shelfService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(shelfRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(shelves, listType);
    }


    @Test
    void saveShelfAlreadyExists() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.findByIdentifier("S1"))
                .thenReturn(new Shelf());

        ShelfDto response = shelfService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Shelf with identifier - S1 already exists",
                response.getMessage()
        );

        Mockito.verify(shelfRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateShelfSuccess() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        dto.setDescription("Top Shelf");
        dto.setStatus(true);

        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("S1"))
                .thenReturn(shelf);

        ShelfDto response = shelfService.update(dto);

        Assertions.assertNull(response.getMessage());
        Assertions.assertEquals("Top Shelf", shelf.getDescription());
        Assertions.assertTrue(shelf.getStatus());

        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void updateShelfNotFound() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.findByIdentifier("S1"))
                .thenReturn(null);

        ShelfDto response = shelfService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf not found", response.getMessage());

        Mockito.verify(shelfRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteShelfTest() {
        shelfService.delete("S1");

        Mockito.verify(shelfRepository)
                .deleteByIdentifier("S1");
    }

    @Test
    void findAllShelvesTest() {
        List<Shelf> shelves = List.of(new Shelf(), new Shelf());
        List<ShelfDto> dtoList = List.of(new ShelfDto(), new ShelfDto());

        Mockito.when(shelfRepository.findAll()).thenReturn(shelves);
        Mockito.when(modelMapper.map(
                Mockito.eq(shelves),
                Mockito.any(Type.class))
        ).thenReturn(dtoList);

        List<ShelfDto> response = shelfService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findShelfByIdentifierTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(shelfRepository.findByIdentifier("S1"))
                .thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        ShelfDto response = shelfService.findByIdentifier("S1");

        Assertions.assertEquals("S1", response.getIdentifier());
    }

    @Test
    void toggleShelfStatusSuccess() {
        Shelf shelf = new Shelf();
        shelf.setStatus(Boolean.TRUE);

        Mockito.when(shelfRepository.findByIdentifier("S1"))
                .thenReturn(shelf);

        shelfService.toggleStatus("S1");

        Assertions.assertFalse(shelf.getStatus());
        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleShelfStatusNotFound() {
        Mockito.when(shelfRepository.findByIdentifier("S1"))
                .thenReturn(null);

        shelfService.toggleStatus("S1");

        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
    }

}
