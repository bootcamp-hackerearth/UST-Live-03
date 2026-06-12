package com.ust.pos.cart.service.impl;

import com.ust.pos.cart.service.CartService;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartEntryService cartEntryService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CartEntryRepository cartEntryRepository;

    @Override
    public CartDto save(CartDto cartDto) {
        String identifier = cartDto.getIdentifier();
        if (cartRepository.existsByIdentifier(identifier)) {
            cartDto.setMessage("Already exists");
            cartDto.setSuccess(false);
            return cartDto;
        }
        Cart cart = modelMapper.map(cartDto, Cart.class);
        cartRepository.save(cart);
        return cartDto;
    }

    @Override
    public CartDto recalculate(String cart) {
        List<CartEntryDto> cartEntries = cartEntryService.findAllCarts(cart);
        Cart cartModel = cartRepository.findByIdentifier(cart);
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;


        for(CartEntryDto cartEntryDto: cartEntries){
            totalPrice = totalPrice.add(cartEntryDto.getTotalPrice());
            totalDiscount = totalDiscount.add(cartEntryDto.getDiscount());
        }

        cartModel.setTotalDiscount(totalDiscount);
        cartModel.setTotalPrice(totalPrice);
        cartRepository.save(cartModel);
        CartDto cartDto = modelMapper.map(cartModel, CartDto.class);
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        cartDto.setEntryDtoList(modelMapper.map(cartEntries, listType));
        return cartDto;
    }

    @Override
    public CartDto findByIdentifier(String identifier) {
        Cart cart = cartRepository.findByIdentifier(identifier);
        CartDto cartDto = modelMapper.map(cart, CartDto.class);
        cartDto.setEntryDtoList(cartEntryService.findAllCarts(cartDto.getIdentifier()));
        return cartDto;
    }

    @Override
    public List<CartDto> findActiveStatus() {
        List<Cart> allCarts = cartRepository.findAll();
        List<Cart> activeCarts = allCarts.stream().filter(Cart::isStatus).toList();

        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        return modelMapper.map(activeCarts, listType);    }

    @Override
    public void deleteByIdentifier(String identifier) {
        cartRepository.deleteByIdentifier(identifier);
        cartEntryService.deleteAllByCart(identifier);
    }
}
