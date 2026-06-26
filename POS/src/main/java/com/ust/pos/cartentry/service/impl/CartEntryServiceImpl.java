package com.ust.pos.cartentry.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CartEntryServiceImpl extends BaseService implements CartEntryService {

    private final CartEntryRepository cartEntryRepository;
    private final CartRepository cartRepository;
    private final PriceRepository priceRepository;
    private final ModelMapper modelMapper;

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (cartEntry == null) {
            return null;
        }
        return modelMapper.map(cartEntry, CartEntryDto.class);
    }

    @Override
    public BigDecimal getSellingPrice(String product) {
        Price price = priceRepository.
                findByProductAndPriceTypeAndDeletedFalse(product, "Selling Price");
        return price.getPriceAmount();
    }

    @Override
    public BigDecimal getDiscount(CartEntryDto cartEntryDto) {
        BigDecimal sellingPrice = getSellingPrice(cartEntryDto.getProduct());
        Price price = priceRepository.
                findByProductAndPriceTypeAndDeletedFalse(cartEntryDto.getProduct(), "MRP");
        BigDecimal mrp = price.getPriceAmount();
        BigDecimal discount = mrp.subtract(sellingPrice);

        return discount.multiply(cartEntryDto.getQuantity());
    }

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        String product = cartEntryDto.getProduct();
        String cartId = cartEntryDto.getCartId();

        Price price = priceRepository.
                findByProductAndPriceTypeAndDeletedFalse(cartEntryDto.getProduct(), "MRP");
        BigDecimal mrp = price.getPriceAmount();

        cartEntryDto.setIdentifier(product + "_" + cartId);
        cartEntryDto.setUnitPrice(getSellingPrice(cartEntryDto.getProduct()));

        String identifier = cartEntryDto.getIdentifier();
        CartEntry existingCartEntry = cartEntryRepository.findByIdentifier(identifier);

        if (existingCartEntry != null) {
            cartEntryDto.setQuantity(cartEntryDto.getQuantity().
                    add(existingCartEntry.getQuantity()));
        }
        cartEntryDto.setDiscount(getDiscount(cartEntryDto));
        cartEntryDto.setOriginalPrice(mrp.multiply(cartEntryDto.getQuantity()));
        cartEntryDto.setTotalPrice(getSellingPrice(product).
                multiply(cartEntryDto.getQuantity()));

        if (existingCartEntry != null) {
            modelMapper.map(cartEntryDto, existingCartEntry);
            cartEntryRepository.save(existingCartEntry);
        } else {
            cartEntryRepository.save(modelMapper.map(cartEntryDto, CartEntry.class));
        }
        recalculate(cartId);
        return cartEntryDto;
    }

    @Override
    public void recalculate(String cartId) {
        List<CartEntry> cartEntries = cartEntryRepository.findAllByCartId(cartId);
        BigDecimal cartEntryTotalPrice = new BigDecimal(0);
        BigDecimal cartEntryTotaldiscount = new BigDecimal(0);
        BigDecimal cartEntryOriginalPrice = new BigDecimal(0);
        for (CartEntry cartEntry : cartEntries) {
            cartEntryTotalPrice = cartEntryTotalPrice.add(cartEntry.getTotalPrice());
            cartEntryTotaldiscount = cartEntryTotaldiscount.add(cartEntry.getDiscount());
            cartEntryOriginalPrice = cartEntryOriginalPrice.add(cartEntry.getOriginalPrice());
        }
        Cart cart = cartRepository.findByIdentifier(cartId);
        cart.setTotalPrice(cartEntryTotalPrice);
        cart.setDiscount(cartEntryTotaldiscount);
        cart.setOriginalPrice(cartEntryOriginalPrice);
        cartRepository.save(cart);
    }

    @Override
    public List<CartEntryDto> findByCartId(String cartId) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        List<CartEntry> cartEntryList = cartEntryRepository.findByCartId(cartId);
        return modelMapper.map(cartEntryList, listType);
    }

    @Override
    @Transactional
    public void delete(CartEntryDto cartEntryDto) {
        String cartId = cartEntryRepository.findByIdentifier(cartEntryDto.getIdentifier()).getCartId();
        String product = cartEntryDto.getProduct();
        String identifier = product + "_" + cartId;
        cartEntryRepository.deleteByIdentifier(identifier);
        recalculate(cartId);
    }

    @Override
    @Transactional
    public void deleteAllByCartId(String cartId) {
        cartEntryRepository.deleteAllByCartId(cartId);
        recalculate(cartId);
    }

    @Override
    public WsDto<CartEntryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);

        WsDto<CartEntryDto> cartEntryWsDto = new WsDto<>();
        cartEntryWsDto.setDtoList(modelMapper.map(cartEntryPage.getContent(), listType));
        cartEntryWsDto.setTotalRecords(cartEntryPage.getTotalElements());
        cartEntryWsDto.setTotalPages(cartEntryPage.getTotalPages());
        cartEntryWsDto.setSizePerPage(pageable.getPageSize());
        cartEntryWsDto.setPage(pageable.getPageNumber());
        return cartEntryWsDto;
    }
}









