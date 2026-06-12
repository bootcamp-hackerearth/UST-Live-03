package com.ust.pos.cart.service.impl;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
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
@Transactional

public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartEntryService cartEntryService;

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
        cartDto.setSuccess(true);
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
    public void delete(String identifier) {
        cartRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<CartDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        return modelMapper.map(cartPage.getContent(), listType);
    }

    @Override
    public CartDto findByIdentifier(String identifier) {
        return modelMapper.map(cartRepository.findByIdentifier(identifier), CartDto.class);
    }

    @Override
    public CartDto recalculate(String cart) {
        List<CartEntryDto> cartEntries = cartEntryService.findAllCarts(cart);
        Cart cartModel = cartRepository.findByIdentifier(cart);
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal originalPrice=BigDecimal.ZERO;
        for(CartEntryDto cartEntryDto: cartEntries){
            totalPrice = totalPrice.add(cartEntryDto.getTotalPrice());
            totalDiscount = totalDiscount.add(cartEntryDto.getDiscount());
            originalPrice = originalPrice.add(cartEntryDto.getOriginalPrice());
        }
        cartModel.setDiscount(totalDiscount);
        cartModel.setTotalPrice(totalPrice);
        cartModel.setOriginalPrice(originalPrice);
        cartRepository.save(cartModel);
        CartDto cartDto = modelMapper.map(cartModel, CartDto.class);
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        cartDto.setCartEntryList(modelMapper.map(cartEntries, listType));
        return cartDto;
    }
}