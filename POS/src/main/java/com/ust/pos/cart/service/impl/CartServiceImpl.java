package com.ust.pos.cart.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
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
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Override
    public CartDto save(CartDto cartDto) {

        Cart cart = cartRepository.findByIdentifier(cartDto.getIdentifier());

        if(cart == null) {

            cart = new Cart();
            cart.setIdentifier(cartDto.getIdentifier());
        }

        cartRepository.save(cart);
        recalculate(cartDto.getIdentifier());
        Cart savedCart = cartRepository.findByIdentifier(cartDto.getIdentifier());
        return modelMapper.map(savedCart, CartDto.class);
    }

    @Override
    public void delete(String identifier) {
        cartRepository.deleteByIdentifier(identifier);
    }

    @Override
    public PaginatedResponseDto<CartDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();
        Page<Cart> cartPage = cartRepository.findAll(pageable);

        List<CartDto> items = modelMapper.map(cartPage.getContent(), listType);

        PaginatedResponseDto<CartDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(cartPage.getTotalElements());
        response.setTotalPages(cartPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());

        return response;
    }

    @Override
    public CartDto findByIdentifier(String identifier) {
        return modelMapper.map(cartRepository.findByIdentifier(identifier), CartDto.class);
    }

    @Override
    public void recalculate(String cartId) {

        Cart cart = cartRepository.findByIdentifier(cartId);

        List<CartEntry> cartEntries = cartEntryRepository.findByCartId(cartId);

        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;

        for(CartEntry entry : cartEntries) {

            totalPrice = totalPrice.add(entry.getTotalPrice());
            discount = discount.add(entry.getDiscount());
        }

        cart.setTotalPrice(totalPrice);
        cart.setDiscount(discount);
        cartRepository.save(cart);
    }
}
