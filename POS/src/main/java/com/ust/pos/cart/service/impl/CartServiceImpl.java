package com.ust.pos.cart.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl extends BaseService implements CartService {

    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final CartEntryService cartEntryService;
    private final CartEntryRepository cartEntryRepository;

    public CartServiceImpl(CartRepository cartRepository, ModelMapper modelMapper, CartEntryService cartEntryService,
                           CartEntryRepository cartEntryRepository) {
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
        this.cartEntryService = cartEntryService;
        this.cartEntryRepository = cartEntryRepository;
    }

    public CartDto findByIdentifier(String identifier) {
        Cart cart = cartRepository.findByIdentifier(identifier);
        if (cart == null) {
            throw new EntityNotFoundException("Cart not found for identifier: " + identifier);
        }
        return modelMapper.map(cart, CartDto.class);
    }

    @Override
    public CartDto save(CartDto cartDto) {
        String identifier = cartDto.getIdentifier();
        Cart existingCart = cartRepository.findByIdentifier(identifier);
        if (existingCart != null) {
            cartDto.setMessage(
                    existingCart.isDeleted()
                            ? "Cart with identifier - " + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : "Cart with identifier - " + identifier
                            + " already exists."
            );
            cartDto.setSuccess(false);
            return cartDto;
        }
        Cart cart = modelMapper.map(cartDto, Cart.class);
        setCreatedDetails(cart);
        cartRepository.save(cart);
        cartEntryService.recalculate(cart.getIdentifier());
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
        List<CartDto> cartDtoList = new ArrayList<>();
        Page<Cart> cartPage = cartRepository.findByIsDeletedFalse(pageable);
        for (Cart cart : cartPage.getContent()) {
            cartDtoList.add(
                    modelMapper.map(cart, CartDto.class)
            );
        }

        for (CartDto cartDto : cartDtoList) {
            cartDto.setCartEntryDtoList(
                    cartEntryService.findByCartId(
                            cartDto.getIdentifier()
                    )
            );
        }

        WsDto<CartDto> wsDto =
                new WsDto<>();

        wsDto.setContent(cartDtoList);
        wsDto.setPage(cartPage.getNumber());
        wsDto.setSizePerPage(cartPage.getSize());
        wsDto.setTotalPages(cartPage.getTotalPages());
        wsDto.setTotalRecords(cartPage.getTotalElements());

        return wsDto;
    }
}