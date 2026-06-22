package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.common.CommonService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartEntryServiceImpl extends CommonService implements CartEntryService {
    public static final String NOT_FOUND = " not found";
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final CartEntryRepository cartEntryRepository;
    private final ModelMapper modelMapper;

    public CartEntryServiceImpl(CartRepository cartRepository, ProductRepository productRepository,
                                PriceRepository priceRepository, CartEntryRepository cartEntryRepository,
                                ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
        this.cartEntryRepository = cartEntryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (cartEntry == null) {
            return null;
        }
        return modelMapper.map(cartEntry, CartEntryDto.class);
    }

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        String cartIdentifier = cartEntryDto.getCartIdentifier();
        String productIdentifier = cartEntryDto.getProductIdentifier();
        Cart cart = cartRepository.findByIdentifier(cartIdentifier);
        if (cart == null) {
            cartEntryDto.setMessage("Cart with identifier - " + cartIdentifier + NOT_FOUND);
            cartEntryDto.setSuccess(false);
            return cartEntryDto;
        }
        Product product = productRepository.findByIdentifier(productIdentifier);
        if (product == null) {
            cartEntryDto.setMessage("Product with identifier - " + productIdentifier + NOT_FOUND);
            cartEntryDto.setSuccess(false);
            return cartEntryDto;
        }
        Price price = priceRepository.findByProductId(product.getId());
        if (price == null) {
            cartEntryDto.setMessage("Price not found for product - " + productIdentifier);
            cartEntryDto.setSuccess(false);
            return cartEntryDto;
        }
        String identifier = cartIdentifier + "_" + productIdentifier;
        CartEntry existingCartEntry = cartEntryRepository.findByIdentifier(identifier);
        BigDecimal quantity = BigDecimal.valueOf(cartEntryDto.getQuantity());
        CartEntry cartEntry;
        if (existingCartEntry != null) {
            quantity = BigDecimal.valueOf(existingCartEntry.getQuantity()).add(quantity);
            cartEntry = existingCartEntry;
        } else {
            cartEntry = new CartEntry();
            cartEntry.setIdentifier(identifier);
            cartEntry.setCartIdentifier(cartIdentifier);
            cartEntry.setProductIdentifier(productIdentifier);
        }
        BigDecimal sellingPrice = price.getSellingPrice();
        BigDecimal mrpPrice = price.getMrpPrice();
        BigDecimal totalPrice = sellingPrice.multiply(quantity);
        BigDecimal savingsPerProduct = mrpPrice.subtract(sellingPrice);
        BigDecimal totalDiscount = savingsPerProduct.multiply(quantity);
        cartEntry.setQuantity(quantity.intValue());
        cartEntry.setMrpPrice(mrpPrice);
        cartEntry.setSellingPrice(sellingPrice);
        cartEntry.setDiscount(totalDiscount);
        cartEntry.setTotalPrice(totalPrice);
        cartEntryRepository.save(cartEntry);
        return findByIdentifier(identifier);
    }

    @Override
    public CartEntryDto update(CartEntryDto cartEntryDto) {
        String identifier = cartEntryDto.getIdentifier();
        CartEntry existingCartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (existingCartEntry == null) {
            cartEntryDto.setMessage("Cart Entry with identifier - " + identifier + NOT_FOUND);
            cartEntryDto.setSuccess(false);
            return cartEntryDto;
        }
        Product product = productRepository.findByIdentifier(existingCartEntry.getProductIdentifier());
        Price price = priceRepository.findByProductId(product.getId());
        BigDecimal quantity = BigDecimal.valueOf(cartEntryDto.getQuantity());
        BigDecimal sellingPrice = price.getSellingPrice();
        BigDecimal mrpPrice = price.getMrpPrice();
        BigDecimal totalPrice = sellingPrice.multiply(quantity);
        BigDecimal savingsPerProduct = mrpPrice.subtract(sellingPrice);
        BigDecimal totalDiscount = savingsPerProduct.multiply(quantity);
        existingCartEntry.setQuantity(quantity.intValue());
        existingCartEntry.setMrpPrice(mrpPrice);
        existingCartEntry.setSellingPrice(sellingPrice);
        existingCartEntry.setDiscount(totalDiscount);
        existingCartEntry.setTotalPrice(totalPrice);
        cartEntryRepository.save(existingCartEntry);
        return findByIdentifier(identifier);
    }

    @Override
    public boolean delete(String identifier) {
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (cartEntry == null) {
            return false;
        }
        cartEntryRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public boolean deleteByCartIdentifier(String cartIdentifier) {
        cartEntryRepository.deleteByCartIdentifier(cartIdentifier);
        return true;
    }

    @Override
    public List<CartEntryDto> findAll(Pageable pageable) {
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);
        return cartEntryPage.getContent().stream().map(cartEntry -> modelMapper.map(cartEntry, CartEntryDto.class)).toList();
    }

    @Override
    public List<CartEntryDto> findAllByCartIdentifier(String cartIdentifier) {
        List<CartEntry> cartEntries = cartEntryRepository.findAllByCartIdentifier(cartIdentifier);
        return cartEntries.stream().map(cartEntry -> modelMapper.map(cartEntry, CartEntryDto.class)).toList();
    }
}