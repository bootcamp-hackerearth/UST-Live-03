package com.ust.pos.cart.service.impl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
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
    private static final String CART_WITH_IDENTIFIER = "Cart with identifier - " ;
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
            cartDto.setMessage(CART_WITH_IDENTIFIER + identifier + " already exists");
            cartDto.setSuccess(false);
            return cartDto;
        }
        Cart cart = modelMapper.map(cartDto, Cart.class);
        cartRepository.save(cart);
        return cartDto;
    }

    @Override
    public CartDto recalculate(String cart) {
        List<CartEntryDto> cartEntries = cartEntryService.findAllEntriesForCart(cart);
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
        cartDto.setCartEntryDtoList(modelMapper.map(cartEntries, listType));
        return cartDto;
    }

    @Override
    public CartDto findByIdentifier(String identifier){
        CartDto cartDto = new CartDto();
        modelMapper.map(cartRepository.findByIdentifier(identifier),cartDto);
        cartDto.setCartEntryDtoList(cartEntryService.findAllEntriesForCart(identifier));
        return cartDto;
    }
    @Override
    public void deleteByIdentifier(String identifier) {
        cartRepository.deleteByIdentifier(identifier);
        cartEntryService.deleteAllByCart(identifier);
    }
}
