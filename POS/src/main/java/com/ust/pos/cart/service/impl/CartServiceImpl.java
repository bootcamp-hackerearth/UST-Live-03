package com.ust.pos.cart.service.impl;

import com.ust.pos.dto.CartDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import com.ust.pos.cart.service.CartService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Override
    public CartDto findByIdentifier(String identifier) {
        Cart cart = cartRepository.findByIdentifier(identifier);
        if (cart == null) {
            return null;
        }
        return modelMapper.map(cart, CartDto.class);
    }

    @Override
    public CartDto save(CartDto cartDto) {
        String identifier = cartDto.getIdentifier();
        Cart existingCart = cartRepository.findByIdentifier(identifier);
        if (existingCart != null) {
            cartDto.setMessage("Cart with identifier - " + identifier + " already exists");
            cartDto.setSuccess(false);
            return cartDto;
        }
        Cart cart = modelMapper.map(cartDto, Cart.class);
        cartRepository.save(cart);
        return cartDto;
    }

    @Override
    public CartDto reCalculate(String identifier) {
        Cart cart = cartRepository.findByIdentifier(identifier);
        if (cart == null) {
            CartDto cartDto = new CartDto();
            cartDto.setIdentifier(identifier);
            save(cartDto);
            cart = cartRepository.findByIdentifier(identifier);
        }

        List<CartEntry> entries = cartEntryRepository.findByCartIdentifier(identifier);
        BigDecimal totalOriginalPrice = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartEntry entry : entries) {
            totalOriginalPrice = totalOriginalPrice.add(entry.getOriginalPrice());
            totalDiscount = totalDiscount.add(entry.getDiscount());
            totalPrice = totalPrice.add(entry.getTotalPrice());
        }
        cart.setTotalOriginalPrice(totalOriginalPrice);
        cart.setTotalDiscount(totalDiscount);
        cart.setTotalPrice(totalPrice);

        Cart saveToCart = cartRepository.save(cart);
        return modelMapper.map(saveToCart, CartDto.class);
    }

    @Override
    public CartDto update(CartDto cartDto) {
        String identifier = cartDto.getIdentifier();
        Cart existingCart = cartRepository.findByIdentifier(identifier);
        if (existingCart == null) {
            cartDto.setMessage("Cart with identifier - " + identifier + " not found");
            cartDto.setSuccess(false);
            return cartDto;
        }
        modelMapper.map(cartDto, existingCart);
        cartRepository.save(existingCart);
        return cartDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        cartRepository.deleteByIdentifier(identifier);
        cartEntryRepository.deleteByCartIdentifier(identifier);
    }

    @Override
    public List<CartDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        return modelMapper.map(cartPage.getContent(), listType);
    }
}