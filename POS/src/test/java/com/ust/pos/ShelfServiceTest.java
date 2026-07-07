package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.jpa.domain.Specification;

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
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf_001");
        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Shelf.class)).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);

        ShelfDto response = shelfService.save(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Shelf_001", response.getIdentifier());
        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void saveFailure_existingActive() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf_001");
        Shelf existing = new Shelf();
        existing.setDeleted(false);
        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(existing);
        ShelfDto response = shelfService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveFailure_softDeleted() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf_001");
        Shelf existing = new Shelf();
        existing.setDeleted(true);

        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(existing);
        ShelfDto response = shelfService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf_001");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf_001");

        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        ShelfDto response = shelfService.findByIdentifier("Shelf_001");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Shelf_001", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf_001");
        Shelf existing = new Shelf();
        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(shelfRepository.save(existing)).thenReturn(existing);

        ShelfDto response = shelfService.update(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Shelf_001", response.getIdentifier());
        Mockito.verify(shelfRepository).save(existing);
    }

    @Test
    void updateFailure() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf_001");
        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(null);
        ShelfDto response = shelfService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Shelf shelf = new Shelf();
        shelf.setDeleted(false);

        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        shelfService.delete("Shelf_001");
        Mockito.verify(shelfRepository).findByIdentifier("Shelf_001");
        Mockito.verify(shelfRepository).save(shelf);
        Assertions.assertTrue(shelf.isDeleted());
    }

    @Test
    void findAllTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf_001");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf_001");

        List<Shelf> shelves = List.of(shelf);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shelf> page = new PageImpl<>(shelves, pageable, 1);
        Mockito.when(shelfRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(shelves), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of(dto));
        WsDto<ShelfDto> response = shelfService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Shelf_001", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findAllWithSpecificationTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf_001");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf_001");

        List<Shelf> shelves = List.of(shelf);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shelf> page = new PageImpl<>(shelves, pageable, 1);
        @SuppressWarnings("unchecked")
        Specification<Shelf> specification = Mockito.mock(Specification.class);
        Mockito.when(shelfRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(shelves), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of(dto));

        WsDto<ShelfDto> response = shelfService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Shelf_001", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void toggleStatusSuccessTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf_001");
        shelf.setStatus(false);

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf_001");
        dto.setStatus(true);
        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        ShelfDto response = shelfService.toggleStatus("Shelf_001", true);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Shelf_001", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(shelfRepository.findByIdentifier("Shelf_001")).thenReturn(null);
        Mockito.when(modelMapper.map(Mockito.isNull(), Mockito.eq(ShelfDto.class))).thenReturn(null);
        ShelfDto response = shelfService.toggleStatus("Shelf_001", true);
        Assertions.assertNull(response);
        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveShelvesTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF_01");
        shelf.setStatus(true);
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF_01");
        dto.setStatus(true);

        List<Shelf> shelves = List.of(shelf);
        Mockito.when(shelfRepository.findByStatusTrue()).thenReturn(shelves);
        Mockito.when(modelMapper.map(Mockito.eq(shelves), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of(dto));

        List<ShelfDto> response = shelfService.findActiveShelves();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("SHELF_01", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
        Mockito.verify(shelfRepository).findByStatusTrue();
    }
}