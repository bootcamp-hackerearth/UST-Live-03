package com.ust.pos.cart.impl;

import com.ust.pos.cart.CartService;
import com.ust.pos.cartentry.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;


@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;

    private final ModelMapper modelMapper;

    private final com.ust.pos.cartentry.CartEntryService cartEntryService;

    public CartServiceImpl(CartRepository cartRepository, ModelMapper modelMapper, CartEntryService cartEntryService) {
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
        this.cartEntryService = cartEntryService;
    }

    @Override
    public CartDto save(CartDto cartDto)
    {
        Cart cart = modelMapper.map(cartDto, Cart.class);
        cartRepository.save(cart);
        return cartDto;
    }

    @Override
    public CartDto recalculateCart(String cartId)
    {
        Cart cart = cartRepository.findByIdentifier(cartId);
        if(cart == null)
        {
            cart = new Cart();
            cart.setIdentifier(cartId);
        }
        List<CartEntryDto> entries = cartEntryService.findByCartId(cartId);
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal discount = cart.getDiscount() != null ? cart.getDiscount() : BigDecimal.ZERO;
        for(CartEntryDto entry: entries)
        {
            if(entry.getTotalPrice() != null)
            {
                totalPrice = totalPrice.add(entry.getTotalPrice());
            }
        }
        cart.setTotalPrice(totalPrice.subtract(discount));

        cartRepository.save(cart);
        CartDto cartDto = modelMapper.map(cart, CartDto.class);
        cartDto.setCartEntries(entries);
        return cartDto;
    }

    @Override
    public CartDto update(CartDto cartDto)
    {
        String identifier = cartDto.getIdentifier();
        Cart existingCart = cartRepository.findByIdentifier(identifier);
        if(existingCart == null)
        {
            cartDto.setMessage("Cart with identifier - "+identifier+" not found");
            cartDto.setSuccess(false);
            return cartDto;
        }
        Cart cart = modelMapper.map(cartDto, Cart.class);
        cartRepository.save(cart);
        return cartDto;
    }

    @Override
    public void delete(String identifier)
    {
        cartRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<CartDto> findAll()
    {
        Type listoftype = new TypeToken<List<CartDto>>(){}.getType();
        return modelMapper.map(cartRepository.findAll(), listoftype);
    }

    @Override
    public CartDto findByIdentifier(String identifier)
    {
        return modelMapper.map(cartRepository.findByIdentifier(identifier), CartDto.class);
    }

    @Override
    public List<CartDto> findAll(Pageable pageable)
    {
        Type listoftype = new TypeToken<List<CartDto>>(){}.getType();
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        return modelMapper.map(cartPage.getContent(), listoftype);
    }
}