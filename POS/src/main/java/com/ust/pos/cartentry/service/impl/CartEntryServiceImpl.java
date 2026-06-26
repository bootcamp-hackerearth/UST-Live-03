package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartEntryServiceImpl implements CartEntryService {

    private final PriceService priceService;
    private final CartEntryRepository cartEntryRepository;
    private final ModelMapper modelMapper;

    public CartEntryServiceImpl(PriceService priceService,
                                CartEntryRepository cartEntryRepository,
                                ModelMapper modelMapper) {
        this.priceService = priceService;
        this.cartEntryRepository = cartEntryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {

        cartEntryDto.setIdentifier(
                cartEntryDto.getCartId() + "_" + cartEntryDto.getProduct()
        );

        CartEntry cartEntry =
                cartEntryRepository.findByIdentifier(cartEntryDto.getIdentifier());

        if (cartEntry == null) {
            cartEntry = new CartEntry();
        }

        BigDecimal existingQty = cartEntry.getQuantity() != null
                ? cartEntry.getQuantity()
                : BigDecimal.ZERO;

        BigDecimal requestQty = cartEntryDto.getQuantity() != null
                ? cartEntryDto.getQuantity()
                : BigDecimal.ZERO;

        cartEntryDto.setQuantity(existingQty.add(requestQty));

        PriceDto priceDto = priceService.findByIdentifier(cartEntryDto.getProduct());

        cartEntryDto.setUnitPrice(priceDto.getSellingPrice());
        BigDecimal discount = cartEntryDto.getDiscount() != null
                ? cartEntryDto.getDiscount()
                : BigDecimal.ZERO;
        cartEntryDto.setTotalPrice(
                cartEntryDto.getUnitPrice()
                        .multiply(cartEntryDto.getQuantity())
                        .subtract(discount)
        );
        modelMapper.map(cartEntryDto, cartEntry);
        cartEntryRepository.save(cartEntry);
        return cartEntryDto;
    }

    @Override
    public CartEntryDto decreaseQuantity(String identifier) {
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (cartEntry == null) {
            return null;
        }
        BigDecimal quantity = cartEntry.getQuantity();

        if (quantity.compareTo(BigDecimal.ONE) <= 0) {
            cartEntryRepository.delete(cartEntry);
            return null;
        }
        quantity = quantity.subtract(BigDecimal.ONE);
        cartEntry.setQuantity(quantity);
        cartEntry.setTotalPrice(
                cartEntry.getUnitPrice().multiply(quantity)
        );
        cartEntryRepository.save(cartEntry);
        return modelMapper.map(cartEntry, CartEntryDto.class);
    }

    @Override
    public CartEntryDto update(CartEntryDto cartEntryDto) {
        String identifier = cartEntryDto.getIdentifier();
        CartEntry existingCartEntry =
                cartEntryRepository.findByIdentifier(identifier);
        if (existingCartEntry == null) {
            cartEntryDto.setMessage("CartEntry with identifier - " + identifier + " is not found");
            cartEntryDto.setSuccess(false);
            return cartEntryDto;
        }
        modelMapper.map(cartEntryDto, existingCartEntry);
        cartEntryRepository.save(existingCartEntry);
        return cartEntryDto;
    }

    @Override
    public void delete(String identifier) {
        cartEntryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<CartEntryDto> findAll() {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        return modelMapper.map(cartEntryRepository.findAll(), listType);
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(
                cartEntryRepository.findByIdentifier(identifier),
                CartEntryDto.class
        );
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
}