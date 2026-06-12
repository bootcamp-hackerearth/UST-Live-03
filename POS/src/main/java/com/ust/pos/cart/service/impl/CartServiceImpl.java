package com.ust.pos.cart.service.impl;

import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Cart;
import com.ust.pos.modell.CartRepository;
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
            cartDto.setMessage("Cart already exists with identifier: " + identifier);
            cartDto.setSuccess(false);
            return cartDto;
        }
        Cart cart = modelMapper.map(cartDto, Cart.class);

        if (cart.getTotalPrice() == null) {
            cart.setTotalPrice(BigDecimal.ZERO);
        }

        if (cart.getOriginalPrice() == null) {
            cart.setOriginalPrice(BigDecimal.ZERO);
        }

        if (cart.getDiscount() == null) {
            cart.setDiscount(BigDecimal.ZERO);
        }

        cartRepository.save(cart);
        cartDto.setSuccess(true);
        return cartDto;
    }

    @Override
    public CartDto update(CartDto cartDto) {
        String identifier = cartDto.getIdentifier();
        Cart existingCart = cartRepository.findByIdentifier(identifier);

        if (existingCart == null) {
            cartDto.setMessage("Cart not found with identifier: " + identifier);
            cartDto.setSuccess(false);
            return cartDto;
        }

        existingCart.setCoupon(cartDto.getCoupon());

        if (cartDto.getTotalPrice() != null) {
            existingCart.setTotalPrice(cartDto.getTotalPrice());
        }

        if (cartDto.getOriginalPrice() != null) {
            existingCart.setOriginalPrice(cartDto.getOriginalPrice());
        }

        if (cartDto.getDiscount() != null) {
            existingCart.setDiscount(cartDto.getDiscount());
        }

        cartRepository.save(existingCart);
        cartDto.setSuccess(true);
        return cartDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Cart existingCart = cartRepository.findByIdentifier(identifier);

        if (existingCart != null) {
            cartRepository.delete(existingCart);
        }
    }

    @Override
    public WsDto<CartDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartDto>>() {}.getType();
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        WsDto<CartDto> cartWsDto = new WsDto<>();
        cartWsDto.setDtoList(modelMapper.map(cartPage.getContent(), listType));
        cartWsDto.setTotalRecords(cartPage.getTotalElements());
        cartWsDto.setTotalPage(cartPage.getTotalPages());
        cartWsDto.setSizePerPage(pageable.getPageSize());
        cartWsDto.setPage(pageable.getPageNumber());
        return cartWsDto;
    }

}