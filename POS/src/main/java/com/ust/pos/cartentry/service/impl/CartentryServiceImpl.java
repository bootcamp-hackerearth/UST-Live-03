package com.ust.pos.cartentry.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cartentry.service.CartentryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
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
public class CartentryServiceImpl extends BaseService implements CartentryService {

    private final CartEntryRepository cartEntryRepository;

    private final PriceRepository priceRepository;

    private final CartRepository cartRepository;

    private final ModelMapper modelMapper;

    public CartentryServiceImpl(CartEntryRepository cartEntryRepository, PriceRepository priceRepository, CartRepository cartRepository, ModelMapper modelMapper) {
        this.cartEntryRepository = cartEntryRepository;
        this.priceRepository = priceRepository;
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WsDto<CartEntryDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();

        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);

        WsDto<CartEntryDto> dto = new WsDto<>();

        dto.setContent(modelMapper.map(cartEntryPage.getContent(), listType));
        dto.setTotalRecords(cartEntryPage.getTotalElements());
        dto.setTotalPages(cartEntryPage.getTotalPages());
        dto.setSizePerPage(pageable.getPageSize());
        dto.setPage(pageable.getPageNumber());

        return dto;
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(cartEntryRepository.findByIdentifier(identifier), CartEntryDto.class);
    }

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {

        String product = cartEntryDto.getProduct();
        String cartId = cartEntryDto.getCartId();

        cartEntryDto.setIdentifier(product + "_" + cartId);
        cartEntryDto.setUnitPrice(getSellingPrice(cartEntryDto.getProduct()));

        String identifier = cartEntryDto.getIdentifier();
        CartEntry existingCart = cartEntryRepository.findByIdentifier(identifier);

        if (existingCart != null) {


            if (existingCart.isDeleted()) {
                cartEntryDto.setMessage(
                        "CartEntry - " + identifier + " already exists but was deleted, Please contact Administrator"
                );
                return cartEntryDto;
            }

            cartEntryDto.setQuantity(cartEntryDto.getQuantity().add(existingCart.getQuantity()));
        }

        cartEntryDto.setDiscount(getDiscount(cartEntryDto));
        cartEntryDto.setOriginalPrice(cartEntryDto.getUnitPrice().multiply(cartEntryDto.getQuantity()));
        cartEntryDto.setTotalPrice(getSellingPrice(product).multiply(cartEntryDto.getQuantity()));

        if (existingCart != null) {
            modelMapper.map(cartEntryDto, existingCart);
            setModifiedDetails(existingCart);
            cartEntryRepository.save(existingCart);
        } else {
            CartEntry cartEntry = modelMapper.map(cartEntryDto, CartEntry.class);
            setCreatedDetails(cartEntry);
            cartEntryRepository.save(cartEntry);
        }

        recalculate(cartId);
        return cartEntryDto;
    }

    @Override
    public void recalculate(String cartId) {
        List<CartEntry> cartEntries = cartEntryRepository.findByCartId(cartId);

        BigDecimal cartEntryTotalPrice = new BigDecimal(0);
        BigDecimal cartEntryTotaldiscount = new BigDecimal(0);
        BigDecimal cartEntryOriginalPrice = new BigDecimal(0);

        for (CartEntry cartEntry : cartEntries) {
            cartEntryTotalPrice = cartEntryTotalPrice.add(cartEntry.getTotalPrice());
            cartEntryTotaldiscount = cartEntryTotaldiscount.add(cartEntry.getDiscount());
            cartEntryOriginalPrice = cartEntryOriginalPrice.add(getMrpPrice(cartEntry.getProduct()).multiply(cartEntry.getQuantity()));
        }

        Cart cart = cartRepository.findByIdentifier(cartId);
        cart.setTotalPrice(cartEntryTotalPrice);
        cart.setDiscount(cartEntryTotaldiscount);
        cart.setOriginalPrice(cartEntryOriginalPrice);
        cartRepository.save(cart);
    }

    @Transactional
    @Override
    public void delete(String identifier, String cartId) {
        cartEntryRepository.deleteByIdentifier(identifier);
        recalculate(cartId);
    }

    @Transactional
    @Override
    public void deleteAllByCartId(String cartId) {
        cartEntryRepository.deleteAllByCartId(cartId);
        recalculate(cartId);
    }

    @Override
    public BigDecimal getMrpPrice(String product) {
        Price price = priceRepository.findByProductAndPriceType(product, "MRP");
        return price.getPriceAmount();
    }

    @Override
    public BigDecimal getSellingPrice(String product) {
        Price price = priceRepository.findByProductAndPriceType(product, "SELLING_PRICE");
        return price.getPriceAmount();
    }

    @Override
    public BigDecimal getDiscount(CartEntryDto cartEntryDto) {
        BigDecimal mrp = getMrpPrice(cartEntryDto.getProduct());
        BigDecimal sellingPrice = getSellingPrice(cartEntryDto.getProduct());
        BigDecimal discount = mrp.subtract(sellingPrice);
        return discount.multiply(cartEntryDto.getQuantity());
    }

    @Override
    public BigDecimal getTotalPrice(String product, BigDecimal quantity) {
        return getSellingPrice(product).multiply(quantity);
    }

    @Override
    public CartEntryDto updateQuantity(CartEntryDto cartEntryDto) {

        String product = cartEntryDto.getProduct();
        String cartId = cartEntryDto.getCartId();

        cartEntryDto.setIdentifier(product + "_" + cartId);
        cartEntryDto.setUnitPrice(getSellingPrice(cartEntryDto.getProduct()));

        String identifier = cartEntryDto.getIdentifier();
        CartEntry existingCart = cartEntryRepository.findByIdentifier(identifier);

        cartEntryDto.setDiscount(getDiscount(cartEntryDto));
        cartEntryDto.setOriginalPrice(cartEntryDto.getUnitPrice().multiply(cartEntryDto.getQuantity()));
        cartEntryDto.setTotalPrice(getSellingPrice(product).multiply(cartEntryDto.getQuantity()));

        if (existingCart != null) {
            modelMapper.map(cartEntryDto, existingCart);
            cartEntryRepository.save(existingCart);
        } else {
            CartEntry cartEntry = modelMapper.map(cartEntryDto, CartEntry.class);
            cartEntryRepository.save(cartEntry);
        }

        recalculate(cartId);
        return cartEntryDto;
    }

    @Override
    public List<CartEntryDto> findByCartId(String cartId) {

        List<CartEntry> cartEntries = cartEntryRepository.findByCartId(cartId);

        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();

        return modelMapper.map(cartEntries, listType);
    }
}
