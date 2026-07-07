package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    @Spy
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
    void findByIdentifierTest() {

        Cart cart = new Cart();
        cart.setIdentifier("USER1");

        CartDto dto = new CartDto();
        dto.setIdentifier("USER1");

        CartEntryDto e1 = new CartEntryDto();
        e1.setTotalPrice(BigDecimal.valueOf(100));
        e1.setDiscount(BigDecimal.valueOf(10));

        CartEntryDto e2 = new CartEntryDto();
        e2.setTotalPrice(BigDecimal.valueOf(200));
        e2.setDiscount(BigDecimal.valueOf(20));

        when(cartRepository.findByIdentifier("USER1")).thenReturn(cart);

        when(modelMapper.map(cart, CartDto.class)).thenReturn(dto);

        when(cartEntryService.findAllByCartIdentifier("USER1")).thenReturn(List.of(e1, e2));

        CartDto result = cartService.findByIdentifier("USER1");

        assertEquals(BigDecimal.valueOf(300), result.getTotalPrice());

        assertEquals(BigDecimal.valueOf(30), result.getDiscount());

    }

    @Test
    void findByIdentifierNotFoundTest() {

        when(cartRepository.findByIdentifier("USER1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> cartService.findByIdentifier("USER1"));

    }

    @Test
    void saveExistingCartTest() {

        Customer customer = new Customer();

        Cart existing = new Cart();
        existing.setIdentifier("USER1");

        CartDto mappedDto = new CartDto();
        mappedDto.setIdentifier("USER1");

        CartEntryDto entry = new CartEntryDto();
        entry.setProductIdentifier("P1");

        CartEntryDto savedEntry = new CartEntryDto();
        savedEntry.setTotalPrice(BigDecimal.valueOf(100));
        savedEntry.setDiscount(BigDecimal.valueOf(10));

        CartDto dto = new CartDto();
        dto.setUsername("USER1");
        dto.setCoupon("TEST");
        dto.setCartEntries(List.of(entry));

        doReturn(dto).when(cartService).findByIdentifier("USER1");

        when(customerRepository.findByIdentifier("USER1")).thenReturn(customer);

        when(cartRepository.findByIdentifier("USER1")).thenReturn(existing);

        when(modelMapper.map(existing, CartDto.class)).thenReturn(mappedDto);

        when(cartEntryService.findAllByCartIdentifier("USER1")).thenReturn(List.of(savedEntry));

        CartDto result = cartService.save(dto);

        assertNotNull(result);

        verify(cartEntryService).save(any());

        verify(cartRepository, atLeastOnce()).save(existing);
    }

    @Test
    void saveNewCartTest() {

        Customer customer = new Customer();

        CartEntryDto entry = new CartEntryDto();

        CartEntryDto existing = new CartEntryDto();
        existing.setTotalPrice(BigDecimal.valueOf(100));
        existing.setDiscount(BigDecimal.valueOf(20));

        CartDto dto = new CartDto();
        dto.setUsername("USER1");
        dto.setCoupon("CPN");
        dto.setCartEntries(List.of(entry));

        CartDto response = new CartDto();

        doReturn(response).when(cartService).findByIdentifier("USER1");

        when(customerRepository.findByIdentifier("USER1")).thenReturn(customer);

        when(cartRepository.findByIdentifier("USER1")).thenReturn(null);

        when(cartEntryService.findAllByCartIdentifier("USER1")).thenReturn(List.of(existing));

        CartDto result = cartService.save(dto);

        assertNotNull(result);

        verify(cartRepository, times(2)).save(any(Cart.class));

        verify(cartEntryService).save(any());

    }

    @Test
    void saveWithoutEntriesTest() {

        Customer customer = new Customer();

        CartDto dto = new CartDto();
        dto.setUsername("USER1");

        CartDto response = new CartDto();

        doReturn(response).when(cartService).findByIdentifier("USER1");

        when(customerRepository.findByIdentifier("USER1")).thenReturn(customer);

        when(cartRepository.findByIdentifier("USER1")).thenReturn(null);

        when(cartEntryService.findAllByCartIdentifier("USER1")).thenReturn(List.of());

        cartService.save(dto);

        verify(cartRepository, times(2)).save(any());

    }

    @Test
    void saveCustomerNotFoundTest() {

        CartDto dto = new CartDto();
        dto.setUsername("USER1");

        when(customerRepository.findByIdentifier("USER1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> cartService.save(dto));

    }

    @Test
    void updateTest() {

        Cart existing = new Cart();
        existing.setIdentifier("USER1");

        CartEntryDto newEntry = new CartEntryDto();

        CartEntryDto existingEntry = new CartEntryDto();
        existingEntry.setIdentifier("ID");

        CartEntryDto summary = new CartEntryDto();
        summary.setTotalPrice(BigDecimal.valueOf(200));
        summary.setDiscount(BigDecimal.valueOf(50));

        CartDto dto = new CartDto();
        dto.setIdentifier("USER1");
        dto.setUsername("USER1");
        dto.setCoupon("TEST");
        dto.setCartEntries(List.of(newEntry, existingEntry));

        CartDto response = new CartDto();

        doReturn(response).when(cartService).findByIdentifier("USER1");

        when(cartRepository.findByIdentifier("USER1")).thenReturn(existing);

        when(cartEntryService.findAllByCartIdentifier("USER1")).thenReturn(List.of(summary));

        CartDto result = cartService.update(dto);

        assertNotNull(result);

        verify(cartEntryService).save(newEntry);

        verify(cartEntryService).update(existingEntry);

        verify(cartRepository, atLeast(2)).save(existing);

    }

    @Test
    void updateWithoutEntriesTest() {

        Cart existing = new Cart();

        CartDto dto = new CartDto();
        dto.setIdentifier("USER1");

        CartDto response = new CartDto();

        doReturn(response).when(cartService).findByIdentifier("USER1");

        when(cartRepository.findByIdentifier("USER1")).thenReturn(existing);

        when(cartEntryService.findAllByCartIdentifier("USER1")).thenReturn(List.of());

        cartService.update(dto);

        verify(cartRepository, atLeast(2)).save(existing);

    }

    @Test
    void updateNotFoundTest() {

        CartDto dto = new CartDto();
        dto.setIdentifier("USER1");

        when(cartRepository.findByIdentifier("USER1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> cartService.update(dto));

    }

    @Test
    void deleteTest() {

        Cart cart = new Cart();

        when(cartRepository.findByIdentifier("USER1")).thenReturn(cart);

        boolean result = cartService.delete("USER1");

        assertTrue(result);

        verify(cartEntryService).deleteByCartIdentifier("USER1");

        verify(cartRepository).deleteByIdentifier("USER1");

    }

    @Test
    void deleteNotFoundTest() {

        when(cartRepository.findByIdentifier("USER1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> cartService.delete("USER1"));

    }

    @Test
    void deleteCartEntryTest() {

        CartEntryDto entry = new CartEntryDto();
        entry.setCartIdentifier("USER1");

        Cart cart = new Cart();

        CartEntryDto e = new CartEntryDto();
        e.setTotalPrice(BigDecimal.valueOf(100));
        e.setDiscount(BigDecimal.valueOf(10));

        when(cartEntryService.findByIdentifier("ENTRY1")).thenReturn(entry);

        when(cartRepository.findByIdentifier("USER1")).thenReturn(cart);

        when(cartEntryService.findAllByCartIdentifier("USER1")).thenReturn(List.of(e));

        boolean result = cartService.deleteCartEntry("ENTRY1");

        assertTrue(result);

        verify(cartEntryService).delete("ENTRY1");

        verify(cartRepository).save(cart);

    }

    @Test
    void deleteCartEntryNotFoundTest() {

        when(cartEntryService.findByIdentifier("ENTRY1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> cartService.deleteCartEntry("ENTRY1"));

    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Cart cart = new Cart();
        cart.setIdentifier("USER1");

        CartDto dto = new CartDto();
        dto.setIdentifier("USER1");

        CartEntryDto entry = new CartEntryDto();
        entry.setTotalPrice(BigDecimal.valueOf(100));
        entry.setDiscount(BigDecimal.valueOf(20));

        Page<Cart> page = new PageImpl<>(List.of(cart));

        List<CartDto> dtos = List.of(dto);

        when(cartRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtos);

        when(cartEntryService.findAllByCartIdentifier("USER1")).thenReturn(List.of(entry));

        List<CartDto> result = cartService.findAll(pageable);

        assertEquals(1, result.size());

        assertEquals(BigDecimal.valueOf(100), result.get(0).getTotalPrice());

        assertEquals(BigDecimal.valueOf(20), result.get(0).getDiscount());

    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Cart> page = new PageImpl<>(List.of());

        when(cartRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of());

        List<CartDto> result = cartService.findAll(pageable);

        assertTrue(result.isEmpty());

    }

}