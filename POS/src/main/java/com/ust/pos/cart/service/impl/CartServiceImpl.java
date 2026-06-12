package com.ust.pos.cart.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
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
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<CartDto> findAll(Pageable pageable) {
        Page<Cart> page = cartRepository.findAll(pageable);
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        return modelMapper.map(page.getContent(), listType);
    }

    @Override
    public CartDto findByIdentifier(String identifier) {
        Cart cart = cartRepository.findByIdentifier(identifier);
        if (cart == null) {
            CartDto dto = new CartDto();
            dto.setSuccess(false);
            dto.setMessage("Cart not found");
            return dto;
        }
        return modelMapper.map(cart, CartDto.class);
    }

    @Override
    public CartDto save(String identifier) {
        Cart existing = cartRepository.findByIdentifier(identifier);
        if (existing != null) {
            CartDto dto = modelMapper.map(existing, CartDto.class);
            dto.setSuccess(true);
            dto.setMessage("Cart already exists");
            return dto;
        }
        Cart cart = new Cart();
        cart.setIdentifier(identifier);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setOriginalPrice(BigDecimal.ZERO);
        cart.setDiscount(BigDecimal.ZERO);
        cartRepository.save(cart);
        CartDto dto = modelMapper.map(cart, CartDto.class);
        dto.setSuccess(true);
        dto.setMessage("Cart created successfully");
        return dto;
    }

    @Override
    public void delete(String identifier) {
        cartEntryRepository.deleteByCartId(identifier);
        cartRepository.deleteByIdentifier(identifier);
    }

    @Override
    public CartDto recalculate(String identifier) {
        List<CartEntry> entries = cartEntryRepository.findByCartId(identifier);
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal original = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        for (CartEntry e : entries) {
            total = total.add(e.getTotalPrice() != null ? e.getTotalPrice() : BigDecimal.ZERO);
            original = original.add(e.getOriginalPrice() != null ? e.getOriginalPrice() : BigDecimal.ZERO);
            discount = discount.add(e.getDiscount() != null ? e.getDiscount() : BigDecimal.ZERO);
        }
        Cart cart = cartRepository.findByIdentifier(identifier);
        if (cart == null) {
            CartDto dto = new CartDto();
            dto.setSuccess(false);
            dto.setMessage("Cart not found");
            return dto;
        }
        cart.setTotalPrice(total);
        cart.setOriginalPrice(original);
        cart.setDiscount(discount);
        Cart saved = cartRepository.save(cart);
        return modelMapper.map(saved, CartDto.class);
    }
}