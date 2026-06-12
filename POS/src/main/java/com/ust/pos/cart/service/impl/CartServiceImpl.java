package com.ust.pos.cart.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
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
    CartRepository cartRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    CartEntryService cartEntryService;
    @Autowired
    CartEntryRepository cartEntryRepository;

    @Override
    public CartDto save(CartDto cartDto) {
        Cart existingCart = cartRepository.findByIdentifier(cartDto.getIdentifier());
        if (existingCart != null) {
            cartDto.setMessage("Cart with identifier - " + cartDto.getIdentifier() + " already exists");
            cartDto.setSuccess(false);
            return cartDto;
        }
        Cart cart = modelMapper.map(cartDto, Cart.class);
        cartRepository.save(cart);
        return cartDto;
    }

    @Override
    public CartDto recalculate(String identifier) {
        Cart cart = cartRepository.findByIdentifier(identifier);
        if (cart == null) {
            CartDto dto = new CartDto();
            dto.setMessage("Cart not found");
            dto.setSuccess(false);
            return dto;
        }
        List<CartEntry> cartEntryList = cartEntryRepository.findByCartIdentifier(identifier);
        BigDecimal totalOriginalPrice = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartEntry cartEntry : cartEntryList) {
            totalOriginalPrice = totalOriginalPrice.add(cartEntry.getOriginalPrice());
            totalDiscount = totalDiscount.add(cartEntry.getDiscount());
            totalPrice = totalPrice.add(cartEntry.getTotalPrice());
        }
        cart.setOriginalPrice(totalOriginalPrice);
        cart.setDiscount(totalDiscount);
        cart.setTotalPrice(totalPrice);
        Cart saved = cartRepository.save(cart);
        return modelMapper.map(saved, CartDto.class);
    }

    @Override
    public WsDto<CartDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        if (pageable == null) {
            List<CartDto> cartDtoList = modelMapper.map(cartRepository.findAll(), listType);
            WsDto<CartDto> response = new WsDto<>();
            response.setDtoList(cartDtoList);
            response.setTotalRecords(cartDtoList.size());
            return response;
        }
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        List<CartDto> cartDtoList = modelMapper.map(cartPage.getContent(), listType);
        WsDto<CartDto> wsDto = new WsDto<>();
        wsDto.setDtoList(cartDtoList);
        wsDto.setPage(cartPage.getNumber());
        wsDto.setSizePerPage(cartPage.getSize());
        wsDto.setTotalPages(cartPage.getTotalPages());
        wsDto.setTotalRecords(cartPage.getTotalElements());
        return wsDto;
    }

    @Override
    public CartDto findByIdentifier(String identifier) {
        return modelMapper.map(cartRepository.findByIdentifier(identifier), CartDto.class);
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        cartEntryRepository.deleteByCartIdentifier(identifier);
        cartRepository.deleteByIdentifier(identifier);
    }

}