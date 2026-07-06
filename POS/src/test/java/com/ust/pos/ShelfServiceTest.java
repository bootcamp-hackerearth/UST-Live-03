package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl shelfService;
    @Mock
    private ShelfRepository shelfRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAll_ShouldReturnWsDtoWithShelfDtos() {
        Specification<Shelf> specification = (root, query, criteriaBuilder) -> null;
        Pageable pageable = PageRequest.of(0, 10);

        Shelf shelf = new Shelf();
        shelf.setId(1L);
        shelf.setIdentifier("SHELF001");

        List<Shelf> shelfList = List.of(shelf);
        Page<Shelf> shelfPage =
                new PageImpl<>(shelfList, pageable, shelfList.size());

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setId(1L);
        shelfDto.setIdentifier("SHELF001");

        List<ShelfDto> shelfDtoList = List.of(shelfDto);

        when(shelfRepository.findAll(specification, pageable))
                .thenReturn(shelfPage);

        when(modelMapper.map(eq(shelfList), any(Type.class)))
                .thenReturn(shelfDtoList);

        WsDto<ShelfDto> result = shelfService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(shelfDtoList, result.getDtoList());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(shelfRepository, times(1)).findAll(specification, pageable);
        verify(modelMapper, times(1)).map(eq(shelfList), any(Type.class));
    }

    @Test
    void testFindByIdentifier_Success() {
        String identifier = "SHELF-001";

        Shelf shelf = new Shelf();
        shelf.setIdentifier(identifier);

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier(identifier);

        when(shelfRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(shelfDto);

        ShelfDto result = shelfService.findByIdentifier(identifier);

        assertEquals(identifier, result.getIdentifier());
    }

    @Test
    void testSave_Success() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF-001");

        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF-001");

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(null);
        when(modelMapper.map(shelfDto, Shelf.class))
                .thenReturn(shelf);

        ShelfDto result = shelfService.save(shelfDto);

        assertNotNull(result);
        verify(shelfRepository).save(shelf);
    }

    @Test
    void testSave_AlreadyExists() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF-001");

        Shelf existingShelf = new Shelf();
        existingShelf.setIdentifier("SHELF-001");
        existingShelf.setDeleted(false);

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(existingShelf);

        ShelfDto result = shelfService.save(shelfDto);

        assertFalse(result.isSuccess());
        assertEquals("Shelf with identifier - SHELF-001 already exists", result.getMessage());

        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    void testSave_DeletedShelfExists() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF-001");

        Shelf existingShelf = new Shelf();
        existingShelf.setIdentifier("SHELF-001");
        existingShelf.setDeleted(true);

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(existingShelf);

        ShelfDto result = shelfService.save(shelfDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Shelf with identifier - SHELF-001 was deleted and cannot be created again.",
                result.getMessage()
        );

        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    void testUpdate_Success() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF-001");

        Shelf existingShelf = new Shelf();
        existingShelf.setIdentifier("SHELF-001");

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(existingShelf);

        ShelfDto result = shelfService.update(shelfDto);

        assertNotNull(result);
        verify(modelMapper).map(shelfDto, existingShelf);
        verify(shelfRepository).save(existingShelf);
    }

    @Test
    void testUpdate_NotFound() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF-001");

        when(shelfRepository.findByIdentifier("SHELF-001"))
                .thenReturn(null);

        ShelfDto result = shelfService.update(shelfDto);

        assertFalse(result.isSuccess());
        assertEquals("Shelf with identifier - SHELF-001 not found", result.getMessage());

        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "SHELF-001";

        Shelf shelf = new Shelf();
        shelf.setIdentifier(identifier);
        shelf.setDeleted(false);

        when(shelfRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(shelf);

        shelfService.delete(identifier);

        assertTrue(shelf.getDeleted());

        verify(shelfRepository).save(shelf);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF-001");

        List<Shelf> shelfList = List.of(shelf);
        Page<Shelf> shelfPage = new PageImpl<>(shelfList, pageable, shelfList.size());

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF-001");

        List<ShelfDto> dtoList = List.of(shelfDto);

        when(shelfRepository.findAllByDeletedFalse(pageable))
                .thenReturn(shelfPage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<ShelfDto> result = shelfService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void testToggleStatus_Success() {
        String identifier = "SHELF-001";

        Shelf shelf = new Shelf();
        shelf.setIdentifier(identifier);
        shelf.setStatus(true);

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier(identifier);

        when(shelfRepository.findByIdentifier(identifier))
                .thenReturn(shelf);
        when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(shelfDto);

        ShelfDto result = shelfService.toggleStatus(identifier);

        assertFalse(shelf.isStatus());
        assertNotNull(result);

        verify(shelfRepository).save(shelf);
    }

    @Test
    void testFindActiveShelves_Success() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF-001");

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("SHELF-001");

        List<Shelf> shelfList = List.of(shelf);
        List<ShelfDto> dtoList = List.of(shelfDto);

        when(shelfRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(shelfList);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(dtoList);

        List<ShelfDto> result = shelfService.findActiveShelves();

        assertEquals(1, result.size());
        assertEquals("SHELF-001", result.get(0).getIdentifier());
    }

}