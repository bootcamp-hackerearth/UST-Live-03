package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    void saveTest() {

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(null);
        Shelf shelf = new Shelf();
        Mockito.when(modelMapper.map(shelfDto, Shelf.class)).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        ShelfDto response = shelfService.save(shelfDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");
        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(shelf);
        ShelfDto response = shelfService.save(shelfDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());

    }

    @Test
    void findByIdentifierTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(shelfDto);

        ShelfDto response = shelfService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        Shelf existingShelf = new Shelf();
        existingShelf.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(existingShelf);
        Mockito.when(shelfRepository.save(existingShelf))
                .thenReturn(existingShelf);

        ShelfDto response = shelfService.update(shelfDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        ShelfDto response = shelfService.update(shelfDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(shelfRepository)
                .deleteByIdentifier("Admin");

        shelfService.delete("Admin");

        Mockito.verify(shelfRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        List<Shelf> shelfs = List.of(shelf);
        List<ShelfDto> shelfDtos = List.of(shelfDto);

        Page<Shelf> shelfPage = new PageImpl<>(shelfs);

        Mockito.when(shelfRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(shelfPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(shelfs),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(shelfDtos);

        PaginatedResponseDto<ShelfDto> response = shelfService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
    }

    @Test
    void findAllActiveTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");
        shelf.setStatus(true);

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        List<Shelf> shelfs = List.of(shelf);
        List<ShelfDto> shelfDtos = List.of(shelfDto);

        Mockito.when(shelfRepository.findByStatus(true)).thenReturn(shelfs);
        Mockito.when(modelMapper.map(
                Mockito.eq(shelfs),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(shelfDtos);

        List<ShelfDto> response = shelfService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");
        shelf.setStatus(false);

        Mockito.when(shelfRepository.findByIdentifier("Admin"))
                .thenReturn(shelf);

        Mockito.when(shelfRepository.save(shelf))
                .thenReturn(shelf);

        shelfService.changeStatus("Admin", true);

        Assertions.assertTrue(shelf.getStatus());

        Mockito.verify(shelfRepository).save(shelf);
    }
}