package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryService cartEntryService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART1");
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");
        CartEntryDto entry = new CartEntryDto();
        entry.setTotalPrice(BigDecimal.valueOf(100));
        entry.setDiscount(BigDecimal.valueOf(20));
        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(dto);
        when(cartEntryService.findAllByCartIdentifier("CART1")).thenReturn(List.of(entry));
        CartDto response = cartService.findByIdentifier("CART1");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("CART1", response.getIdentifier());
        Assertions.assertEquals(BigDecimal.valueOf(100), response.getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(20), response.getDiscount());
    }

    @Test
    void findByIdentifierNullCartTest() {
        when(cartRepository.findByIdentifier("CART1")).thenReturn(null);
        CartDto response = cartService.findByIdentifier("CART1");
        Assertions.assertNull(response);
    }

    @Test
    void saveCustomerNotFoundTest() {
        CartDto dto = new CartDto();
        dto.setUsername("CUST1");
        when(customerRepository.findByIdentifier("CUST1")).thenReturn(null);
        CartDto response = cartService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Customer with identifier - CUST1 not found", response.getMessage());
    }

    @Test
    void saveExistingCartTest() {
        CartDto dto = new CartDto();
        dto.setUsername("CUST1");
        dto.setCoupon("SALE50");
        CartEntryDto entry = new CartEntryDto();
        dto.setCartEntries(List.of(entry));

        Customer customer = new Customer();
        Cart existingCart = new Cart();
        existingCart.setIdentifier("CUST1");

        CartEntryDto savedEntry = new CartEntryDto();
        savedEntry.setTotalPrice(BigDecimal.valueOf(100));
        savedEntry.setDiscount(BigDecimal.valueOf(20));

        CartDto mappedDto = new CartDto();

        when(customerRepository.findByIdentifier("CUST1")).thenReturn(customer);
        when(cartRepository.findByIdentifier("CUST1")).thenReturn(existingCart);
        when(cartEntryService.findAllByCartIdentifier("CUST1")).thenReturn(List.of(savedEntry));
        when(modelMapper.map(existingCart, CartDto.class)).thenReturn(mappedDto);

        CartDto response = cartService.save(dto);

        Assertions.assertNotNull(response);
        verify(cartEntryService).save(entry);
        verify(cartRepository, atLeastOnce()).save(existingCart);
    }

    @Test
    void saveNewCartTest() {
        CartDto dto = new CartDto();
        dto.setUsername("CUST1");

        CartEntryDto entry = new CartEntryDto();
        dto.setCartEntries(List.of(entry));

        Customer customer = new Customer();

        Cart savedCart = new Cart();
        savedCart.setIdentifier("CUST1");

        CartEntryDto savedEntry = new CartEntryDto();
        savedEntry.setTotalPrice(BigDecimal.valueOf(100));
        savedEntry.setDiscount(BigDecimal.valueOf(20));

        CartDto mappedDto = new CartDto();
        mappedDto.setIdentifier("CUST1");

        when(customerRepository.findByIdentifier("CUST1")).thenReturn(customer);
        when(cartRepository.findByIdentifier("CUST1")).thenReturn(null).thenReturn(savedCart);
        when(cartRepository.save(any(Cart.class))).thenReturn(savedCart);
        when(cartEntryService.findAllByCartIdentifier("CUST1")).thenReturn(List.of(savedEntry));
        when(modelMapper.map(savedCart, CartDto.class)).thenReturn(mappedDto);

        CartDto response = cartService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("CUST1", response.getIdentifier());
        verify(cartEntryService).save(entry);
    }

    @Test
    void saveUsingIdentifierTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CUST1");

        Customer customer = new Customer();
        Cart cart = new Cart();
        cart.setIdentifier("CUST1");

        when(customerRepository.findByIdentifier("CUST1")).thenReturn(customer);
        when(cartRepository.findByIdentifier("CUST1")).thenReturn(cart);
        when(cartEntryService.findAllByCartIdentifier("CUST1")).thenReturn(List.of());
        when(modelMapper.map(cart, CartDto.class)).thenReturn(new CartDto());

        Assertions.assertNotNull(cartService.save(dto));
    }

    @Test
    void updateCartNotFoundTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");
        when(cartRepository.findByIdentifier("CART1")).thenReturn(null);
        CartDto response = cartService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart with identifier - CART1 not found", response.getMessage());
    }

    @Test
    void updateSuccessTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");
        dto.setUsername("CUST1");

        CartEntryDto newEntry = new CartEntryDto();
        newEntry.setIdentifier(null);

        CartEntryDto existingEntry = new CartEntryDto();
        existingEntry.setIdentifier("ENTRY1");

        dto.setCartEntries(List.of(newEntry, existingEntry));

        Cart existingCart = new Cart();

        CartEntryDto entry = new CartEntryDto();
        entry.setTotalPrice(BigDecimal.valueOf(100));
        entry.setDiscount(BigDecimal.valueOf(10));

        CartDto mappedDto = new CartDto();

        when(cartRepository.findByIdentifier("CART1")).thenReturn(existingCart);
        when(cartEntryService.findAllByCartIdentifier("CART1")).thenReturn(List.of(entry));
        when(modelMapper.map(existingCart, CartDto.class)).thenReturn(mappedDto);

        CartDto response = cartService.update(dto);

        Assertions.assertNotNull(response);
        verify(cartEntryService).save(newEntry);
        verify(cartEntryService).update(existingEntry);
    }

    @Test
    void updateWithoutEntriesTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        Cart cart = new Cart();

        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        when(cartEntryService.findAllByCartIdentifier("CART1")).thenReturn(List.of());
        when(modelMapper.map(cart, CartDto.class)).thenReturn(new CartDto());

        Assertions.assertNotNull(cartService.update(dto));
    }

    @Test
    void deleteTest() {
        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        boolean response = cartService.delete("CART1");
        Assertions.assertTrue(response);
        verify(cartEntryService).deleteByCartIdentifier("CART1");
        verify(cartRepository).deleteByIdentifier("CART1");
        verify(cartRepository, never()).save(any());
    }

    @Test
    void deleteCartNullTest() {
        when(cartRepository.findByIdentifier("CART1")).thenReturn(null);
        boolean response = cartService.delete("CART1");
        Assertions.assertFalse(response);
        verify(cartEntryService, never()).deleteByCartIdentifier(any());
        verify(cartRepository, never()).deleteByIdentifier(any());
    }

    @Test
    void deleteCartEntryNotFoundTest() {
        when(cartEntryService.findByIdentifier("ENTRY1")).thenReturn(null);
        boolean response = cartService.deleteCartEntry("ENTRY1");
        Assertions.assertFalse(response);
    }

    @Test
    void deleteCartEntryCartNotFoundTest() {
        CartEntryDto entry = new CartEntryDto();
        entry.setCartIdentifier("CART1");
        when(cartEntryService.findByIdentifier("ENTRY1")).thenReturn(entry);
        when(cartRepository.findByIdentifier("CART1")).thenReturn(null);
        boolean response = cartService.deleteCartEntry("ENTRY1");
        Assertions.assertFalse(response);
    }

    @Test
    void deleteCartEntrySuccessTest() {
        CartEntryDto entry = new CartEntryDto();
        entry.setCartIdentifier("CART1");

        Cart cart = new Cart();

        CartEntryDto remaining = new CartEntryDto();
        remaining.setTotalPrice(BigDecimal.valueOf(100));
        remaining.setDiscount(BigDecimal.valueOf(10));

        when(cartEntryService.findByIdentifier("ENTRY1")).thenReturn(entry);
        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        when(cartEntryService.findAllByCartIdentifier("CART1")).thenReturn(List.of(remaining));

        boolean response = cartService.deleteCartEntry("ENTRY1");

        Assertions.assertTrue(response);
        verify(cartEntryService).delete("ENTRY1");
        verify(cartRepository).save(cart);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        CartEntryDto entry = new CartEntryDto();
        entry.setTotalPrice(BigDecimal.valueOf(100));
        entry.setDiscount(BigDecimal.valueOf(20));

        when(cartRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(cart)));
        when(modelMapper.map(any(List.class), any(Type.class))).thenReturn(List.of(dto));
        when(cartEntryService.findAllByCartIdentifier("CART1")).thenReturn(List.of(entry));

        List<CartDto> response = cartService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(BigDecimal.valueOf(100), response.get(0).getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(20), response.get(0).getDiscount());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);
        when(cartRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));
        when(modelMapper.map(any(List.class), any(Type.class))).thenReturn(List.of());
        List<CartDto> response = cartService.findAll(pageable);
        Assertions.assertTrue(response.isEmpty());
    }

}