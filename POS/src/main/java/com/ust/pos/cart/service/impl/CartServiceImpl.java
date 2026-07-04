package com.ust.pos.cart.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl extends BaseService implements CartService {

    private final CartRepository cartRepository;

    private final CartEntryRepository cartEntryRepository;

    private final ModelMapper modelMapper;

    public CartServiceImpl(CartRepository cartRepository, CartEntryRepository cartEntryRepository, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.cartEntryRepository = cartEntryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartDto findByIdentifier(String identifier) {

        Cart cart = cartRepository.findByIdentifier(identifier);

        if (cart == null) {
            throw new ResourseNotFoundException("Data cannot found");
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
        cartRepository.deleteByIdentifier(identifier);

    }

    @Override
    public WsDto<CartDto> findAll(Pageable pageable) {

        Page<Cart> cartPage = cartRepository.findAll(pageable);
        WsDto<CartDto> cartDto = new WsDto<>();

        List<CartDto> cartDtos = cartPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, CartDto.class))
                .toList();

        cartDto.setContent(cartDtos);
        cartDto.setPage(cartPage.getNumber());
        cartDto.setSizePerPage(cartPage.getSize());
        cartDto.setTotalPages(cartPage.getTotalPages());
        cartDto.setTotalRecords(cartPage.getTotalElements());

        return cartDto;
    }
}









