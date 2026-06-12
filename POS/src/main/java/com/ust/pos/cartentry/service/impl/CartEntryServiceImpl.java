package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.CartEntry;
import com.ust.pos.modell.CartEntryRepository;
import com.ust.pos.price.service.PriceService;
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
public class CartEntryServiceImpl implements CartEntryService {

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PriceService priceService;

    @Autowired
    private CartService cartService;


    @Override
    public CartEntryDto save(CartEntryDto dto) {
        String productId = dto.getProductIdentifier();
        String cartId = dto.getCartIdentifier();
        int qty = dto.getQuantity();
        if (productId == null || productId.isBlank()) {
            throw new RuntimeException("Product identifier is missing");
        }
        String identifier = cartId + "-" + productId;
        PriceDto selling = priceService.findByIdentifier(productId + "-SELLING");
        PriceDto mrp = priceService.findByIdentifier(productId + "-MRP");
        if (selling == null && mrp == null) {
            throw new RuntimeException("Price not configured for product: " + productId);
        }
        BigDecimal unitPrice;
        BigDecimal discount = BigDecimal.ZERO;
        if (selling != null) {
            unitPrice = selling.getPriceAmount();
        } else {
            unitPrice = mrp.getPriceAmount();
        }
        if (selling != null && mrp != null) {
            BigDecimal mrpPrice = mrp.getPriceAmount();
            BigDecimal sellingPrice = selling.getPriceAmount();
            discount = mrpPrice.subtract(sellingPrice);
        }
        CartEntry entry = cartEntryRepository.findByIdentifier(identifier);
        if (entry == null) {
            entry = new CartEntry();
            entry.setIdentifier(identifier);
            entry.setProductIdentifier(productId);
            entry.setCartIdentifier(cartId);
            entry.setQuantity(0);
        }
        int updatedQty = entry.getQuantity() + qty;
        entry.setQuantity(updatedQty);
        entry.setUnitPrice(unitPrice);
        entry.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(updatedQty)));
        entry.setDiscount(discount);
        cartEntryRepository.save(entry);
        return modelMapper.map(entry, CartEntryDto.class);
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(
                cartEntryRepository.findByIdentifier(identifier),
                CartEntryDto.class
        );
    }

    @Override
    public WsDto<CartEntryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);

        WsDto<CartEntryDto> cartEntryWsDto = new WsDto<>();
        cartEntryWsDto.setDtoList(modelMapper.map(cartEntryPage.getContent(), listType));
        cartEntryWsDto.setTotalRecords(cartEntryPage.getTotalElements());
        cartEntryWsDto.setTotalPage(cartEntryPage.getTotalPages());
        cartEntryWsDto.setSizePerPage(pageable.getPageSize());
        cartEntryWsDto.setPage(pageable.getPageNumber());

        return cartEntryWsDto;
    }
}