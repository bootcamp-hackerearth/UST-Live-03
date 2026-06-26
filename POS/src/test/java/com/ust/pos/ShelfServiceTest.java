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
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(null);
        Shelf shelf = new Shelf();
        Mockito.when(modelMapper.map(dto, Shelf.class)).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        ShelfDto response = shelfService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestAlreadyExists() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Shelf existing = new Shelf();
        existing.setDeleted(false);
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(existing);
        ShelfDto response = shelfService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestDeletedExists() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Shelf existing = new Shelf();
        existing.setDeleted(true);
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(existing);
        ShelfDto response = shelfService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Shelf existing = new Shelf();
        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(shelfRepository.save(existing)).thenReturn(existing);
        ShelfDto response = shelfService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        ShelfDto response = shelfService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Shelf shelf = new Shelf();
        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        shelfService.delete("S1");
        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void deleteNullTest() {
        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        shelfService.delete("S1");
        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllTest() {
        Shelf shelf = new Shelf();
        ShelfDto dto = new ShelfDto();
        List<Shelf> list = List.of(shelf);
        List<ShelfDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Shelf> page = new PageImpl<>(list);
        Mockito.when(shelfRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        WsDto<ShelfDto> response = shelfService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findByIdentifierTest() {
        Shelf shelf = new Shelf();
        ShelfDto dto = new ShelfDto();
        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);
        ShelfDto response = shelfService.findByIdentifier("S1");
        Assertions.assertNotNull(response);
    }

    @Test
    void updateStatusTest() {
        Shelf shelf = new Shelf();
        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);
        shelfService.updateStatus("S1", true);
        Mockito.verify(shelfRepository).save(shelf);
    }

    @Test
    void updateStatusNullTest() {
        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        shelfService.updateStatus("S1", true);
        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllActiveTest() {
        Shelf shelf = new Shelf();
        ShelfDto dto = new ShelfDto();
        List<Shelf> list = List.of(shelf);
        List<ShelfDto> dtoList = List.of(dto);
        Mockito.when(shelfRepository.findByStatusAndDeletedFalse(true)).thenReturn(list);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        List<ShelfDto> response = shelfService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }
}