package com.ust.pos.cart.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartEntryService cartEntryService;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;

    public CartServiceImpl(CartEntryService cartEntryService,
                           CartRepository cartRepository,
                           ModelMapper modelMapper) {
        this.cartEntryService = cartEntryService;
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartDto save(CartDto cartDto) {
        Cart cart = modelMapper.map(cartDto, Cart.class);
        cartRepository.save(cart);
        return cartDto;
    }

    @Override
    public CartDto recalulateCart(String cartId) {
        Cart cart = cartRepository.findByIdentifier(cartId);
        if (cart == null) {
            cart = new Cart();
            cart.setIdentifier(cartId);
        }
        List<CartEntryDto> entries = cartEntryService.findByCartId(cartId);
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal discount = cart.getDiscount() != null
                ? cart.getDiscount()
                : BigDecimal.ZERO;
        for (CartEntryDto entry : entries) {
            if (entry.getTotalPrice() != null) {
                totalPrice = totalPrice.add(entry.getTotalPrice());
            }
        }
        cart.setTotalPrice(totalPrice.subtract(discount));
        cartRepository.save(cart);
        CartDto cartDto = modelMapper.map(cart, CartDto.class);
        cartDto.setCartEntries(entries);
        return cartDto;
    }

    @Override
    public CartDto update(CartDto cartDto) {
        String identifier = cartDto.getIdentifier();
        Cart existingCart = cartRepository.findByIdentifier(identifier);
        if (existingCart == null) {
            cartDto.setMessage("Cart with identifier - " + identifier + " is not found");
            cartDto.setSuccess(false);
            return cartDto;
        }
        modelMapper.map(cartDto, existingCart);
        cartRepository.save(existingCart);
        return cartDto;
    }

    @Override
    public void delete(String identifier) {
        cartRepository.deleteByIdentifier(identifier);
    }

    @Override
    public void deleteAll() {
        cartRepository.deleteAll();
    }

    @Override
    public List<CartDto> findAll() {
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        return modelMapper.map(cartRepository.findAll(), listType);
    }

    @Override
    public CartDto findByIdentifier(String identifier) {
        return modelMapper.map(
                cartRepository.findByIdentifier(identifier),
                CartDto.class
        );
    }

    @Override
    public List<CartDto> findAll(Pageable pageable) {
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        return modelMapper.map(cartPage.getContent(), listType);
    }
}