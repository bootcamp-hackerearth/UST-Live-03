package com.ust.pos.cart.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartEntryRepository cartEntryRepository;

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
    public List<CartEntryDto> getAllCartEntriesByCartId(String cartId) {

        List<CartEntry> cartEntryList = cartEntryRepository.findByCartId(cartId);

        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();

        return modelMapper.map(cartEntryList, listType);
    }

    @Override
    public BigDecimal findTotalPrice(List<CartEntryDto> cartEntryDtoList) {

        BigDecimal sum = new BigDecimal(0);

        for (CartEntryDto cartEntryDto : cartEntryDtoList) {
            sum = sum.add(cartEntryDto.getTotalPrice());
        }

        return sum;
    }

    @Override
    public BigDecimal getDiscount(List<CartEntryDto> cartEntryDtoList) {

        BigDecimal discount = new BigDecimal(0);

        for (CartEntryDto cartEntryDto : cartEntryDtoList) {
            discount = discount.add(cartEntryDto.getDiscount());
        }

        return discount;
    }

    @Override
    public CartDto save(CartDto cartDto) {

        cartDto.setCartEntryDtoList(getAllCartEntriesByCartId(cartDto.getIdentifier()));
        cartDto.setTotalPrice(findTotalPrice(cartDto.getCartEntryDtoList()));
        cartDto.setDiscount(getDiscount(cartDto.getCartEntryDtoList()));

        cartRepository.save(modelMapper.map(cartDto, Cart.class));

        return cartDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        cartRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<CartDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();

        Page<Cart> cartPage = cartRepository.findAll(pageable);

        WsDto<CartDto> dto = new WsDto<>();

        dto.setContent(modelMapper.map(cartPage.getContent(), listType));
        dto.setTotalRecords(cartPage.getTotalElements());
        dto.setTotalPages(cartPage.getTotalPages());
        dto.setSizePerPage(pageable.getPageSize());
        dto.setPage(pageable.getPageNumber());

        return dto;
    }
}

