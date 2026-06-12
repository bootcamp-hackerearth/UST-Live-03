package com.ust.pos.cart.service.impl;

import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import com.ust.pos.cart.service.CartService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
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
    public CartDto save(CartDto cartDto) {
        String identifier = cartDto.getIdentifier();
        Cart existingCart = cartRepository.findByIdentifier(identifier);
        if(existingCart!=null){
            cartDto.setMessage("cart with"+identifier+"exists");
            return cartDto;
        }
        cartRepository.save(modelMapper.map(cartDto, Cart.class));
        return cartDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        cartEntryRepository.deleteAllByCartId(identifier);
        cartRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<CartDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        return modelMapper.map(cartPage.getContent(), listType);
    }

    public WsDto<CartDto> findAllws(Pageable pageable) {
        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        Page<Cart> cartPage = cartRepository.findAll(pageable);

        WsDto<CartDto> cartWsDto = new WsDto<>();
        cartWsDto.setDtoList(modelMapper.map(cartPage.getContent(), listType));
        cartWsDto.setTotalRecords(cartPage.getTotalElements());
        cartWsDto.setTotalPages(cartPage.getTotalPages());
        cartWsDto.setSizePerPage(pageable.getPageSize());
        cartWsDto.setPage(pageable.getPageNumber());
        return cartWsDto;
    }
}









