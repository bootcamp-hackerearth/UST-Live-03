package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.model.*;
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
import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartService cartService;

    @Test
    void saveNewCartEntryTest() {

        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setCartId("CART001");
        cartEntryDto.setProduct("PROD001");
        cartEntryDto.setQuantity(new BigDecimal("2"));

        Price sellingPrice = new Price();
        sellingPrice.setCostPrice(new BigDecimal("100"));

        Price mrp = new Price();
        mrp.setCostPrice(new BigDecimal("120"));

        CartEntry savedCartEntry = new CartEntry();
        savedCartEntry.setIdentifier("CART001_PROD001");

        CartEntryDto savedCartEntryDto = new CartEntryDto();
        savedCartEntryDto.setIdentifier("CART001_PROD001");

        Mockito.when(cartEntryRepository.findByIdentifier("CART001_PROD001")).thenReturn(null);
        Mockito.when(priceRepository.findByProductAndPriceType("PROD001", "SP")).thenReturn(sellingPrice);
        Mockito.when(priceRepository.findByProductAndPriceType("PROD001", "MRP")).thenReturn(mrp);
        Mockito.when(cartEntryRepository.save(Mockito.any(CartEntry.class))).thenReturn(savedCartEntry);
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(null);
        Mockito.when(modelMapper.map(savedCartEntry, CartEntryDto.class)).thenReturn(savedCartEntryDto);

        CartEntryDto response = cartEntryService.save(cartEntryDto);

        Assertions.assertEquals("CART001_PROD001", response.getIdentifier());

        Mockito.verify(cartEntryRepository).save(Mockito.any(CartEntry.class));
        Mockito.verify(cartService).recalculate("CART001");
    }

    @Test
    void saveExistingCartEntryTest() {

        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setCartId("CART001");
        cartEntryDto.setProduct("PROD001");
        cartEntryDto.setQuantity(new BigDecimal("2"));

        CartEntry existingCartEntry = new CartEntry();
        existingCartEntry.setIdentifier("CART001_PROD001");
        existingCartEntry.setQuantity(new BigDecimal("3"));

        Price sellingPrice = new Price();
        sellingPrice.setCostPrice(new BigDecimal("100"));

        Price mrp = new Price();
        mrp.setCostPrice(new BigDecimal("120"));

        CartEntry savedCartEntry = new CartEntry();
        savedCartEntry.setIdentifier("CART001_PROD001");

        CartEntryDto savedCartEntryDto = new CartEntryDto();
        savedCartEntryDto.setIdentifier("CART001_PROD001");

        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        Mockito.when(cartEntryRepository.findByIdentifier("CART001_PROD001")).thenReturn(existingCartEntry);
        Mockito.when(priceRepository.findByProductAndPriceType("PROD001", "SP")).thenReturn(sellingPrice);
        Mockito.when(priceRepository.findByProductAndPriceType("PROD001", "MRP")).thenReturn(mrp);
        Mockito.when(cartEntryRepository.save(Mockito.any(CartEntry.class)))
                .thenReturn(savedCartEntry);
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        Mockito.when(modelMapper.map(savedCartEntry, CartEntryDto.class)).thenReturn(savedCartEntryDto);

        CartEntryDto response = cartEntryService.save(cartEntryDto);

        Assertions.assertEquals("CART001_PROD001", response.getIdentifier());

        Mockito.verify(cartEntryRepository).save(existingCartEntry);
        Mockito.verify(cartService).recalculate("CART001");
    }

    @Test
    void savePriceNotFoundTest() {

        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setCartId("CART001");
        cartEntryDto.setProduct("PROD001");
        cartEntryDto.setQuantity(new BigDecimal("2"));

        Mockito.when(cartEntryRepository.findByIdentifier("CART001_PROD001")).thenReturn(null);
        Mockito.when(priceRepository.findByProductAndPriceType("PROD001", "SP")).thenReturn(null);
        Mockito.when(priceRepository.findByProductAndPriceType("PROD001", "MRP")).thenReturn(null);

        CartEntryDto response = cartEntryService.save(cartEntryDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Price details not found for product 'PROD001'", response.getMessage());

        Mockito.verify(cartEntryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveZeroQuantityNoExistingEntryTest() {

        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setCartId("CART001");
        cartEntryDto.setProduct("PROD001");
        cartEntryDto.setQuantity(BigDecimal.ZERO);

        Mockito.when(cartEntryRepository.findByIdentifier("CART001_PROD001")).thenReturn(null);

        CartEntryDto response = cartEntryService.save(cartEntryDto);

        Assertions.assertNotNull(response);

        Mockito.verify(cartEntryRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(cartService, Mockito.never()).recalculate(Mockito.any());
    }

    @Test
    void saveZeroQuantityWithExistingEntryTest() {

        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setCartId("CART001");
        cartEntryDto.setProduct("PROD001");
        cartEntryDto.setQuantity(new BigDecimal("-3"));

        CartEntry existingCartEntry = new CartEntry();
        existingCartEntry.setIdentifier("CART001_PROD001");
        existingCartEntry.setQuantity(new BigDecimal("3"));

        Mockito.when(cartEntryRepository.findByIdentifier("CART001_PROD001")).thenReturn(existingCartEntry);

        CartEntryDto response = cartEntryService.save(cartEntryDto);

        Assertions.assertNotNull(response);

        Mockito.verify(cartEntryRepository).delete(existingCartEntry);
        Mockito.verify(cartService).recalculate("CART001");
    }

    @Test
    void deleteTest() {

        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("CART001_PROD001");
        cartEntry.setCartId("CART001");

        Mockito.when(cartEntryRepository.findByIdentifier("CART001_PROD001")).thenReturn(cartEntry);
        Mockito.doNothing().when(cartEntryRepository).delete(cartEntry);

        cartEntryService.delete("CART001_PROD001");

        Mockito.verify(cartEntryRepository).delete(cartEntry);
        Mockito.verify(cartService).recalculate("CART001");
    }

    @Test
    void deleteNotFoundTest() {

        Mockito.when(cartEntryRepository.findByIdentifier("CART001_PROD001")).thenReturn(null);

        cartEntryService.delete("CART001_PROD001");

        Mockito.verify(cartEntryRepository, Mockito.never()).delete(Mockito.any());
        Mockito.verify(cartService, Mockito.never()).recalculate(Mockito.any());
    }

    @Test
    void findAllTest() {

        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("CART001_PROD001");

        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setIdentifier("CART001_PROD001");

        List<CartEntry> cartEntries = List.of(cartEntry);
        List<CartEntryDto> cartEntryDtos = List.of(cartEntryDto);

        Page<CartEntry> cartEntryPage = new PageImpl<>(cartEntries);

        Mockito.when(cartEntryRepository.findAll(Mockito.any(Pageable.class))).thenReturn(cartEntryPage);
        Mockito.when(modelMapper.map(Mockito.anyList(), Mockito.any(Type.class))).thenReturn(cartEntryDtos);

        PaginatedResponseDto<CartEntryDto> response = cartEntryService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findByIdentifierTest() {

        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("CART001_PROD001");

        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setIdentifier("CART001_PROD001");

        Mockito.when(cartEntryRepository.findByIdentifier("CART001_PROD001")).thenReturn(cartEntry);
        Mockito.when(modelMapper.map(cartEntry, CartEntryDto.class)).thenReturn(cartEntryDto);

        CartEntryDto response = cartEntryService.findByIdentifier("CART001_PROD001");

        Assertions.assertEquals("CART001_PROD001", response.getIdentifier());
    }

    @Test
    void findByCartIdTest() {

        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("CART001_PROD001");
        cartEntry.setCartId("CART001");

        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setIdentifier("CART001_PROD001");

        List<CartEntry> entries = List.of(cartEntry);
        List<CartEntryDto> entryDtos = List.of(cartEntryDto);

        Mockito.when(cartEntryRepository.findByCartId("CART001")).thenReturn(entries);
        Mockito.when(modelMapper.map(Mockito.anyList(), Mockito.any(Type.class))).thenReturn(entryDtos);

        List<CartEntryDto> response = cartEntryService.findByCartId("CART001");

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("CART001_PROD001", response.get(0).getIdentifier());
    }
}