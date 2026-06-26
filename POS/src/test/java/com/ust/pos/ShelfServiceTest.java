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
    void saveTest() {

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(shelfDto, Shelf.class)).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);

        ShelfDto response = shelfService.save(shelfDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertFalse(shelf.getIsDeleted());
    }

    @Test
    void saveTestFailureAlreadyExists() {

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        Shelf existingShelf = new Shelf();
        existingShelf.setIsDeleted(false);

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(existingShelf);

        ShelfDto response = shelfService.save(shelfDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf with identifier - Admin already exists", response.getMessage());
    }

    @Test
    void saveTestFailureDeletedShelf() {

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        Shelf existingShelf = new Shelf();
        existingShelf.setIsDeleted(true);

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(existingShelf);

        ShelfDto response = shelfService.save(shelfDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("was deleted"));
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

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(existingShelf);
        Mockito.when(shelfRepository.save(existingShelf)).thenReturn(existingShelf);

        ShelfDto response = shelfService.update(shelfDto);

        Assertions.assertEquals("Admin", response.getIdentifier());

        Mockito.verify(modelMapper).map(shelfDto, existingShelf);
        Mockito.verify(shelfRepository).save(existingShelf);
    }

    @Test
    void updateTestFailure() {

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(null);

        ShelfDto response = shelfService.update(shelfDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf with identifier - Admin not found", response.getMessage());
    }

    @Test
    void deleteTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");
        shelf.setStatus(true);
        shelf.setIsDeleted(false);

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);

        ShelfDto response = shelfService.delete("Admin");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Shelf deleted successfully", response.getMessage());
        Assertions.assertTrue(shelf.getIsDeleted());
        Assertions.assertFalse(shelf.getStatus());
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(null);

        ShelfDto response = shelfService.delete("Admin");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf with identifier - Admin not found", response.getMessage());
    }

    @Test
    void findAllTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");

        List<Shelf> shelfs = List.of(shelf);
        List<ShelfDto> shelfDtos = List.of(shelfDto);

        Page<Shelf> page = new PageImpl<>(shelfs);

        Mockito.when(shelfRepository.findByIsDeleted(
                Mockito.eq(false),
                Mockito.any(Pageable.class)
        )).thenReturn(page);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(shelfDtos);

        PaginatedResponseDto<ShelfDto> response = shelfService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
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

        Mockito.when(shelfRepository.findByStatusAndIsDeleted(true, false)).thenReturn(shelfs);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(shelfDtos);

        List<ShelfDto> response = shelfService.findAllActive();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Admin", response.get(0).getIdentifier());
    }

    @Test
    void changeStatusTrueTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");
        shelf.setStatus(false);

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);

        shelfService.changeStatus("Admin", true);

        Assertions.assertTrue(shelf.getStatus());
        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void changeStatusFalseTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");
        shelf.setStatus(true);

        Mockito.when(shelfRepository.findByIdentifier("Admin")).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);

        shelfService.changeStatus("Admin", false);

        Assertions.assertFalse(shelf.getStatus());
        Mockito.verify(shelfRepository).save(shelf);
    }
}