package com.ust.pos.cart.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final CartEntryRepository cartEntryRepository;

    public CartServiceImpl(CartRepository cartRepository, ModelMapper modelMapper, CartEntryRepository cartEntryRepository) {
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
        this.cartEntryRepository = cartEntryRepository;
    }

    @Override
    public CartDto save(CartDto cartDto) {
        String identifier = cartDto.getIdentifier();
        Cart existingCart = cartRepository.findByIdentifier(identifier);
        if (existingCart != null) {
            return findByIdentifier(identifier);
        }
        Cart cart = modelMapper.map(cartDto, Cart.class);
        if (cart.getTotalPrice() == null) {
            cart.setTotalPrice(BigDecimal.ZERO);
        }
        if (cart.getTotalDiscount() == null) {cart.setTotalDiscount(BigDecimal.ZERO);}
        cartRepository.save(cart);
        return modelMapper.map(cart, CartDto.class);
    }

    @Override
    public CartDto recalculate(String identifier) {
        List<CartEntry> cartEntries = cartEntryRepository.findAllByCart(identifier);
        Cart cartModel = cartRepository.findByIdentifier(identifier);
        if (cartModel == null) {
            cartModel = new Cart();
            cartModel.setIdentifier(identifier);
            cartModel.setTotalPrice(BigDecimal.ZERO);
            cartModel.setTotalDiscount(BigDecimal.ZERO);
            cartRepository.save(cartModel);
        }
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (CartEntry entry : cartEntries) {
            totalPrice = totalPrice.add(entry.getTotalPrice());
            totalDiscount = totalDiscount.add(entry.getDiscount());
        }
        cartModel.setTotalPrice(totalPrice);
        cartModel.setTotalDiscount(totalDiscount);
        cartRepository.save(cartModel);
        CartDto cartDto = modelMapper.map(cartModel, CartDto.class);
        Type listType = new TypeToken<List<CartEntryDto>>() {}.getType();
        cartDto.setCartEntryDtoList(modelMapper.map(cartEntries, listType));
        return cartDto;
    }

    @Override
    public CartDto findByIdentifier(String identifier) {
        Cart cart = cartRepository.findByIdentifier(identifier);
        if (cart == null) {
            cart = new Cart();
            cart.setIdentifier(identifier);
            cart.setTotalPrice(BigDecimal.ZERO);
            cart.setTotalDiscount(BigDecimal.ZERO);
            cartRepository.save(cart);
        }
        CartDto cartDto = modelMapper.map(cart, CartDto.class);
        List<CartEntry> cartEntries = cartEntryRepository.findAllByCart(identifier);
        Type listType = new TypeToken<List<CartEntryDto>>() {}.getType();
        cartDto.setCartEntryDtoList(modelMapper.map(cartEntries, listType));
        return cartDto;
    }

    @Override
    public void delete(String identifier) {
        cartRepository.deleteByIdentifier(identifier);
    }
}