package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.price.service.PriceService;
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
    private CartService cartService;

    @Autowired
    private PriceService priceService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartEntryDto save(CartEntryDto dto) {
        if (dto.getQuantity() == null || dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            dto.setSuccess(false);
            dto.setMessage("Quantity must be greater than 0");
            return dto;
        }
        String cartId = dto.getCartId();
        String productId = dto.getProductId();
        String identifier = cartId + "_" + productId;
        CartEntry entry = cartEntryRepository.findByIdentifier(identifier);
        if (entry == null) {
            entry = new CartEntry();
            entry.setIdentifier(identifier);
            entry.setCartId(cartId);
            entry.setProductId(productId);
            entry.setQuantity(BigDecimal.ZERO);
        }
        BigDecimal existingQty = entry.getQuantity() != null ? entry.getQuantity() : BigDecimal.ZERO;
        entry.setQuantity(existingQty.add(dto.getQuantity()));
        PriceDto mrpDto = priceService.findByIdentifier(productId + "_MRP");
        PriceDto spDto = priceService.findByIdentifier(productId + "_Selling_Price");
        if (mrpDto == null || spDto == null ||
                mrpDto.getValue() == null || spDto.getValue() == null) {
            dto.setSuccess(false);
            dto.setMessage("Price not configured for product: " + productId);
            return dto;
        }
        BigDecimal mrp = mrpDto.getValue();
        BigDecimal sp = spDto.getValue();

        entry.setMrp(mrp);
        entry.setSellingPrice(sp);

        BigDecimal qty = entry.getQuantity();
        entry.setOriginalPrice(qty.multiply(mrp));
        entry.setTotalPrice(qty.multiply(sp));
        entry.setDiscount(mrp.subtract(sp).multiply(qty));

        CartEntry saved = cartEntryRepository.save(entry);
        cartService.recalculate(cartId);
        CartEntryDto response = modelMapper.map(saved, CartEntryDto.class);
        response.setIdentifier(identifier);
        response.setSuccess(true);
        response.setMessage("Cart entry saved successfully");
        return response;
    }

    @Override
    public CartEntryDto update(CartEntryDto dto) {
        CartEntry entry = cartEntryRepository.findByIdentifier(dto.getIdentifier());

        if (entry == null) {
            dto.setSuccess(false);
            dto.setMessage("Cart entry not found");
            return dto;
        }

        if (dto.getQuantity() == null || dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            dto.setSuccess(false);
            dto.setMessage("Invalid quantity");
            return dto;
        }

        entry.setQuantity(dto.getQuantity());

        BigDecimal mrp = entry.getMrp();
        BigDecimal sp = entry.getSellingPrice();

        if (mrp != null && sp != null) {
            BigDecimal qty = entry.getQuantity();
            entry.setOriginalPrice(qty.multiply(mrp));
            entry.setTotalPrice(qty.multiply(sp));
            entry.setDiscount(mrp.subtract(sp).multiply(qty));
        }

        CartEntry saved = cartEntryRepository.save(entry);

        cartService.recalculate(entry.getCartId());

        CartEntryDto response = modelMapper.map(saved, CartEntryDto.class);
        response.setSuccess(true);
        response.setMessage("Cart entry updated successfully");

        return response;
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {

        CartEntry entry = cartEntryRepository.findByIdentifier(identifier);

        if (entry == null) {
            CartEntryDto dto = new CartEntryDto();
            dto.setSuccess(false);
            dto.setMessage("Cart entry not found");
            return dto;
        }

        CartEntryDto dto = modelMapper.map(entry, CartEntryDto.class);
        dto.setSuccess(true);
        return dto;
    }

    @Override
    public List<CartEntryDto> findAll(Pageable pageable) {
        Page<CartEntry> page = cartEntryRepository.findAll(pageable);
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        return modelMapper.map(page.getContent(), listType);
    }

    @Override
    public List<CartEntryDto> findByCartId(String cartId) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        return modelMapper.map(cartEntryRepository.findByCartId(cartId), listType);
    }

    @Override
    public void delete(String identifier) {
        CartEntry entry = cartEntryRepository.findByIdentifier(identifier);
        if (entry != null) {
            String cartId = entry.getCartId();
            cartEntryRepository.deleteByIdentifier(identifier);
            cartService.recalculate(cartId);
        }
    }
}