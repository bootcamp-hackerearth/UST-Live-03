package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.exception.ResourseNotFoundException;
import com.ust.pos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CartEntryServiceTest {

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    private CartEntry cartEntry;
    private CartEntryDto cartEntryDto;
    private Price sellingPrice;
    private Price mrpPrice;

    @BeforeEach
    void setup() {
        cartEntry = new CartEntry();
        cartEntry.setIdentifier("prod1_cart1");
        cartEntry.setCartId("cart1");
        cartEntry.setQuantity(BigDecimal.ONE);

        cartEntryDto = new CartEntryDto();
        cartEntryDto.setProduct("prod1");
        cartEntryDto.setCartId("cart1");
        cartEntryDto.setQuantity(BigDecimal.ONE);

        sellingPrice = new Price();
        sellingPrice.setPriceAmount(new BigDecimal("100"));

        mrpPrice = new Price();
        mrpPrice.setPriceAmount(new BigDecimal("150"));
    }

    @Test
    void testFindByIdentifier_notFound() {

        when(cartEntryRepository.findByIdentifier("id1"))
                .thenReturn(null);

        assertThrows(
                ResourseNotFoundException.class,
                () -> cartEntryService.findByIdentifier("id1")
        );
    }

    @Test
    void testGetSellingPrice() {
        when(priceRepository.findByProductAndPriceType("prod1", "Selling Price"))
                .thenReturn(sellingPrice);

        BigDecimal result = cartEntryService.getSellingPrice("prod1");
        assertEquals(new BigDecimal("100"), result);
    }

    @Test
    void testGetDiscount() {

        when(priceRepository.findByProductAndPriceType("prod1", "Selling Price"))
                .thenReturn(sellingPrice);
        when(priceRepository.findByProductAndPriceType("prod1", "MRP"))
                .thenReturn(mrpPrice);

        cartEntryDto.setQuantity(new BigDecimal("2"));

        BigDecimal discount = cartEntryService.getDiscount(cartEntryDto);
        assertEquals(new BigDecimal("100"), discount);
    }

    @Test
    void testSave_newEntry() {

        when(priceRepository.findByProductAndPriceType("prod1", "MRP"))
                .thenReturn(mrpPrice);
        when(priceRepository.findByProductAndPriceType("prod1", "Selling Price"))
                .thenReturn(sellingPrice);
        when(cartEntryRepository.findByIdentifier(anyString())).thenReturn(null);

        CartEntry mappedEntity = new CartEntry();

        when(modelMapper.map(any(CartEntryDto.class), eq(CartEntry.class)))
                .thenReturn(mappedEntity);
        when(cartEntryRepository.findAllByCartId("cart1"))
                .thenReturn(Collections.emptyList());

        Cart cart = new Cart();

        when(cartRepository.findByIdentifier("cart1")).thenReturn(cart);
        CartEntryDto result = cartEntryService.save(cartEntryDto);

        assertNotNull(result);
        verify(cartEntryRepository).save(mappedEntity);
        verify(cartRepository).save(cart);
    }

    @Test
    void testSave_existingEntry() {

        CartEntry existing = new CartEntry();
        existing.setIdentifier("prod1_cart1");
        existing.setCartId("cart1");
        existing.setQuantity(new BigDecimal("2"));
        existing.setTotalPrice(new BigDecimal("300"));
        existing.setDiscount(new BigDecimal("50"));
        existing.setOriginalPrice(new BigDecimal("350"));

        when(priceRepository.findByProductAndPriceType(
                "prod1",
                "MRP"))
                .thenReturn(mrpPrice);
        when(priceRepository.findByProductAndPriceType(
                "prod1",
                "Selling Price"))
                .thenReturn(sellingPrice);
        when(cartEntryRepository.findByIdentifier("prod1_cart1"))
                .thenReturn(existing);
        when(cartEntryRepository.findAllByCartId("cart1"))
                .thenReturn(List.of(existing));

        Cart cart = new Cart();
        cart.setIdentifier("cart1");

        when(cartRepository.findByIdentifier("cart1"))
                .thenReturn(cart);
        CartEntryDto result = cartEntryService.save(cartEntryDto);

        assertNotNull(result);
        assertEquals(
                new BigDecimal("3"),
                result.getQuantity());
        verify(cartEntryRepository)
                .save(existing);
        verify(cartRepository)
                .save(cart);
    }

    @Test
    void testRecalculate() {

        CartEntry entry1 = new CartEntry();
        entry1.setTotalPrice(new BigDecimal("100"));
        entry1.setDiscount(new BigDecimal("10"));
        entry1.setOriginalPrice(new BigDecimal("120"));

        CartEntry entry2 = new CartEntry();
        entry2.setTotalPrice(new BigDecimal("200"));
        entry2.setDiscount(new BigDecimal("20"));
        entry2.setOriginalPrice(new BigDecimal("250"));

        when(cartEntryRepository.findAllByCartId("cart1"))
                .thenReturn(Arrays.asList(entry1, entry2));

        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("cart1")).thenReturn(cart);

        cartEntryService.recalculate("cart1");

        assertEquals(new BigDecimal("300"), cart.getTotalPrice());
        assertEquals(new BigDecimal("30"), cart.getDiscount());
        assertEquals(new BigDecimal("370"), cart.getOriginalPrice());

        verify(cartRepository).save(cart);
    }

    @Test
    void testFindByCartId() {

        List<CartEntry> cartEntries = List.of(cartEntry);

        when(cartEntryRepository.findByCartId("cart1"))
                .thenReturn(cartEntries);

        List<CartEntryDto> dtoList = List.of(cartEntryDto);

        doReturn(dtoList)
                .when(modelMapper)
                .map(any(), any(java.lang.reflect.Type.class));

        List<CartEntryDto> result = cartEntryService.findByCartId("cart1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cartEntryRepository).findByCartId("cart1");
    }

    @Test
    void updateQuantityTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCartId("CART1");
        dto.setQuantity(BigDecimal.valueOf(2));

        CartEntry existingCartEntry = new CartEntry();
        existingCartEntry.setIdentifier("P1_CART1");
        existingCartEntry.setQuantity(BigDecimal.valueOf(5));

        Price sellingPrice1 = new Price();
        sellingPrice1.setPriceAmount(BigDecimal.valueOf(100));

        Price mrpPrice1 = new Price();
        mrpPrice1.setPriceAmount(BigDecimal.valueOf(120));

        CartEntry savedEntry = new CartEntry();
        savedEntry.setTotalPrice(BigDecimal.valueOf(200));
        savedEntry.setDiscount(BigDecimal.valueOf(40));
        savedEntry.setOriginalPrice(BigDecimal.valueOf(240));

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        when(priceRepository.findByProductAndPriceType(
                "P1", "Selling Price"))
                .thenReturn(sellingPrice1);

        when(priceRepository.findByProductAndPriceType(
                "P1", "MRP"))
                .thenReturn(mrpPrice1);

        when(cartEntryRepository.findByIdentifier("P1_CART1"))
                .thenReturn(existingCartEntry);

        when(cartEntryRepository.findAllByCartId("CART1"))
                .thenReturn(List.of(savedEntry));

        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        doAnswer(invocation -> {
            CartEntryDto source = invocation.getArgument(0);
            CartEntry target = invocation.getArgument(1);

            target.setIdentifier(source.getIdentifier());
            target.setQuantity(source.getQuantity());
            target.setUnitPrice(source.getUnitPrice());
            target.setDiscount(source.getDiscount());
            target.setOriginalPrice(source.getOriginalPrice());
            target.setTotalPrice(source.getTotalPrice());

            return null;
        }).when(modelMapper).map(any(CartEntryDto.class),
                any(CartEntry.class));

        CartEntryDto result = cartEntryService.updateQuantity(dto);

        assertNotNull(result);

        assertEquals("P1_CART1", result.getIdentifier());
        assertEquals(BigDecimal.valueOf(100), result.getUnitPrice());

        assertEquals(BigDecimal.valueOf(40),
                result.getDiscount());

        assertEquals(BigDecimal.valueOf(240),
                result.getOriginalPrice());

        assertEquals(BigDecimal.valueOf(200),
                result.getTotalPrice());

        assertEquals(BigDecimal.valueOf(2),
                result.getQuantity());

        verify(modelMapper)
                .map(any(CartEntryDto.class), eq(existingCartEntry));

        verify(cartEntryRepository)
                .save(existingCartEntry);

        verify(cartRepository)
                .save(cart);

        assertEquals(BigDecimal.valueOf(200),
                cart.getTotalPrice());

        assertEquals(BigDecimal.valueOf(40),
                cart.getDiscount());

        assertEquals(BigDecimal.valueOf(240),
                cart.getOriginalPrice());
    }

    @Test
    void testDelete() {

        when(cartEntryRepository.findByIdentifier("id1")).thenReturn(cartEntry);

        when(cartEntryRepository.findAllByCartId("cart1"))
                .thenReturn(Collections.emptyList());

        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("cart1")).thenReturn(cart);

        cartEntryService.delete("id1");

        verify(cartEntryRepository).deleteByIdentifier("id1");
        verify(cartRepository).save(cart);
    }

    @Test
    void testDeleteAllByCartId() {

        when(cartEntryRepository.findAllByCartId("cart1"))
                .thenReturn(Collections.emptyList());

        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("cart1")).thenReturn(cart);

        cartEntryService.deleteAllByCartId("cart1");

        verify(cartEntryRepository).deleteAllByCartId("cart1");
        verify(cartRepository).save(cart);
    }

    @Test
    void testFindAll() {

        Page<CartEntry> page =
                new PageImpl<>(List.of(cartEntry));

        when(cartEntryRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        List<CartEntryDto> dtoList = List.of(cartEntryDto);

        doReturn(dtoList)
                .when(modelMapper)
                .map(any(), any(java.lang.reflect.Type.class));

        List<CartEntryDto> result =
                cartEntryService.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(cartEntryRepository)
                .findAll(any(Pageable.class));
    }
}