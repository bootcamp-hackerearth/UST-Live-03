package com.ust.pos.cart.service.impl;


import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartEntryService cartEntryService;


    @Override
    public CartDto findByIdentifier(String identifier) {
        return modelMapper.map(cartRepository.findByIdentifier(identifier), CartDto.class);
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
        cartEntryService.recalculate(cart.getIdentifier());
        return cartDto;
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
    }

    @Override
    public List<CartDto> findAll(Pageable pageable) {
        List<CartDto> cartDtoList = new ArrayList<>();
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        for (Cart cart : cartPage.getContent()) {
            cartDtoList.add(modelMapper.map(cart, CartDto.class));
        }
        for (CartDto cartDto : cartDtoList) {
            cartDto.setCartEntryDtoList(cartEntryService.findByCartId(cartDto.getIdentifier()));
        }
        return cartDtoList;
    }

}
