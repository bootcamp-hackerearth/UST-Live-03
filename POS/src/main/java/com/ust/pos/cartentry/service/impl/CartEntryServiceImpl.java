package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginatedResponseDto;
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
public class CartEntryServiceImpl implements CartEntryService {

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {

        String identifier = cartEntryDto.getCartId() + "_" + cartEntryDto.getProduct();
        CartEntry existingCartEntry = cartEntryRepository.findByIdentifier(identifier);

        BigDecimal quantity = cartEntryDto.getQuantity();

        if (existingCartEntry != null) {
            quantity = existingCartEntry.getQuantity().add(quantity);
        }

        Price sellingPrice = priceRepository.findByProductAndPriceType(cartEntryDto.getProduct(), "SP");
        Price mrp = priceRepository.findByProductAndPriceType(cartEntryDto.getProduct(), "MRP");

        if (sellingPrice == null || mrp == null) {

            cartEntryDto.setSuccess(false);
            cartEntryDto.setMessage("Price details not found for product '"
                    + cartEntryDto.getProduct() + "'");
            return cartEntryDto;
        }

        BigDecimal unitPrice = sellingPrice.getCostPrice();
        BigDecimal mrpPrice = mrp.getCostPrice();

        BigDecimal originalPrice = mrpPrice.multiply(quantity);
        BigDecimal totalPrice = unitPrice.multiply(quantity);
        BigDecimal discount = originalPrice.subtract(totalPrice);

        CartEntry cartEntry;

        if(existingCartEntry != null) {
            cartEntry = existingCartEntry;
        } else {
            cartEntry = new CartEntry();
        }

        cartEntry.setIdentifier(identifier);
        cartEntry.setCartId(cartEntryDto.getCartId());
        cartEntry.setProduct(cartEntryDto.getProduct());
        cartEntry.setQuantity(quantity);
        cartEntry.setUnitPrice(unitPrice);
        cartEntry.setOriginalPrice(originalPrice);
        cartEntry.setDiscount(discount);
        cartEntry.setTotalPrice(totalPrice);

        CartEntry savedCartEntry = cartEntryRepository.save(cartEntry);

        Cart cart = cartRepository.findByIdentifier(cartEntryDto.getCartId());

        if(cart == null) {

            cart = new Cart();
            cart.setIdentifier(cartEntryDto.getCartId());
            cartRepository.save(cart);
        }

        cartService.recalculate(cartEntryDto.getCartId());
        return modelMapper.map(savedCartEntry, CartEntryDto.class);
    }

    @Override
    public void delete(String identifier) {
        cartEntryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public PaginatedResponseDto<CartEntryDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CartEntryDto>>(){}.getType();
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);

        List<CartEntryDto> items = modelMapper.map(cartEntryPage.getContent(), listType);

        PaginatedResponseDto<CartEntryDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(cartEntryPage.getTotalElements());
        response.setTotalPages(cartEntryPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());

        return response;
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(cartEntryRepository.findByIdentifier(identifier), CartEntryDto.class);
    }
}
