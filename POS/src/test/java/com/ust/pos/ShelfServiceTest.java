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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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

        Mockito.verify(shelfRepository, Mockito.never())
                .save(Mockito.any());
    }

    // ---------------- UPDATE ----------------

    @Test
    void updateTest_Success() {
        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("S1");

        Shelf existing = new Shelf();
        existing.setIdentifier("S1");

        Mockito.when(shelfRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        // ✅ Correct: only stub what is actually used
        Mockito.doNothing()
                .when(modelMapper).map(dto, existing);

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
        Mockito.verify(shelfRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Failure_WhenIdentifierExists() {
        ShelfDto dto = new ShelfDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");

        Shelf existing = new Shelf();
        existing.setIdentifier("OLD");

        Mockito.when(shelfRepository.findById(1L))
                .thenReturn(Optional.of(existing));
        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("NEW"))
                .thenReturn(new Shelf());

        ShelfDto response = shelfService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf already exists", response.getMessage());
    }

    // ---------------- FIND BY IDENTIFIER ----------------

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

    // ---------------- UPDATE STATUS ONLY ----------------

    @Test
    void updateStatusOnlyTest() {
        Shelf shelf = new Shelf();
        shelf.setStatus(false);

        Mockito.when(shelfRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf))
                .thenReturn(shelf);

        shelfService.updateStatusOnly("S1", true);

        Assertions.assertTrue(shelf.getStatus());
        Mockito.verify(shelfRepository).save(shelf);
    }

    // DELETE

    @Test
    void deleteTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        Mockito.when(
                        shelfRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(shelf);

        shelfService.delete("S1");

        Assertions.assertTrue(shelf.isDeleted());

        Mockito.verify(shelfRepository)
                .findByIdentifierAndDeletedFalse("S1");

        Mockito.verify(shelfRepository)
                .save(shelf);
    }

    @Test
    void findAll_WithPagination_ShouldReturnShelfDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("S1");

        Page<Shelf> page =
                new PageImpl<>(List.of(shelf));

        Mockito.when(
                        shelfRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Mockito.when(
                        modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(shelfDto);

        Page<ShelfDto> response =
                shelfService.findAll(null, pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getContent().size());

        Assertions.assertEquals(
                "S1",
                response.getContent().get(0).getIdentifier());

        Mockito.verify(shelfRepository)
                .findByDeletedFalse(pageable);

        Mockito.verify(modelMapper)
                .map(shelf, ShelfDto.class);
    }

    @Test
    void findAll_WithSearch_ShouldReturnShelfDtos() {

        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("S1");

        Page<Shelf> page =
                new PageImpl<>(List.of(shelf));

        Mockito.when(
                shelfRepository.findAll(
                        Mockito.<Specification<Shelf>>any(),
                        Mockito.eq(pageable)
                )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        shelf,
                        ShelfDto.class
                )
        ).thenReturn(shelfDto);

        // Act
        Page<ShelfDto> response =
                shelfService.findAll(
                        "S",
                        pageable
                );

        // Assert
        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Assertions.assertEquals(
                "S1",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(shelfRepository)
                .findAll(
                        Mockito.<Specification<Shelf>>any(),
                        Mockito.eq(pageable)
                );

        Mockito.verify(shelfRepository, Mockito.never())
                .findByDeletedFalse(Mockito.any(Pageable.class));

        Mockito.verify(modelMapper)
                .map(
                        shelf,
                        ShelfDto.class
                );
    }
}