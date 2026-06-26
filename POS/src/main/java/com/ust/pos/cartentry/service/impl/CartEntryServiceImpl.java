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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartEntryServiceImpl implements CartEntryService {

    private final CartEntryRepository cartEntryRepository;
    private final CartService cartService;
    private final PriceService priceService;
    private final ModelMapper modelMapper;

    public CartEntryServiceImpl(CartEntryRepository cartEntryRepository, CartService cartService, PriceService priceService, ModelMapper modelMapper) {
        this.cartEntryRepository = cartEntryRepository;
        this.cartService = cartService;
        this.priceService = priceService;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        if (cartEntryDto.getQuantity() == null || cartEntryDto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            cartEntryDto.setSuccess(false);
            cartEntryDto.setMessage("Quantity must be greater than 0");
            return cartEntryDto;
        }
        String cartId = cartEntryDto.getCartId();
        cartService.save(cartId);
        String productId = cartEntryDto.getProductId();
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
        entry.setQuantity(existingQty.add(cartEntryDto.getQuantity()));
        PriceDto mrpDto = priceService.findByIdentifier(productId + "_MRP");
        PriceDto spDto = priceService.findByIdentifier(productId + "_Selling_Price");
        if (mrpDto == null || spDto == null ||
                mrpDto.getAmount() == null || spDto.getAmount() == null) {
            cartEntryDto.setSuccess(false);
            cartEntryDto.setMessage("Price not configured for product: " + productId);
            return cartEntryDto;
        }
        BigDecimal mrp = BigDecimal.valueOf(mrpDto.getAmount());
        BigDecimal sp = BigDecimal.valueOf(spDto.getAmount());
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
    public CartEntryDto update(CartEntryDto cartEntryDto) {
        CartEntry entry = cartEntryRepository.findByIdentifier(cartEntryDto.getIdentifier());
        if (entry == null) {
            cartEntryDto.setSuccess(false);
            cartEntryDto.setMessage("Cart entry not found");
            return cartEntryDto;
        }
        if (cartEntryDto.getQuantity() == null || cartEntryDto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            cartEntryDto.setSuccess(false);
            cartEntryDto.setMessage("Invalid quantity");
            return cartEntryDto;
        }
        entry.setQuantity(cartEntryDto.getQuantity());
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