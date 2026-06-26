package com.ust.pos.cart.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends BaseService implements CartService {

    private final CartRepository cartRepository;
    private final CartEntryRepository cartEntryRepository;
    private final ModelMapper modelMapper;

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
            cartDto.setMessage("cart with" + identifier + "exists");
            return cartDto;
        }
        Cart cart = modelMapper.map(cartDto, Cart.class);
        setCreatedDetails(cart);
        cartRepository.save(cart);
        return cartDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        cartEntryRepository.deleteAllByCartId(identifier);
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









