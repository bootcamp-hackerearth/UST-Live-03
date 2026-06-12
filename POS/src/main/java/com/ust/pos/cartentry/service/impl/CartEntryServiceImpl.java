package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private PriceRepository priceRepository;

    @Lazy
    @Autowired
    private CartService cartService;

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        String identifier = cartEntryDto.getProduct() + "-" + cartEntryDto.getCart();
        cartEntryDto.setIdentifier(identifier);
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        Price price = priceRepository.findByIdentifier(cartEntryDto.getProduct());
        BigDecimal newQty = cartEntryDto.getQuantity();
        if (cartEntry != null) {
            BigDecimal existingQty = cartEntry.getQuantity();
            newQty = existingQty.add(newQty);
        } else {
            cartEntry = new CartEntry();
        }
        BigDecimal totalPrice = price.getSellingPrice().multiply(newQty);
        BigDecimal discount = price.getMrp().subtract(price.getSellingPrice());
        cartEntryDto.setQuantity(newQty);
        cartEntryDto.setDiscount(discount.multiply(newQty));
        cartEntryDto.setTotalPrice(totalPrice);
        cartEntryDto.setPrice(price.getMrp());
        cartEntryDto.setSellingPrice(price.getSellingPrice());
        modelMapper.map(cartEntryDto, cartEntry);
        cartEntryRepository.save(cartEntry);
        cartService.recalculate(cartEntry.getCart());
        return cartEntryDto;
    }

    @Override
    public WsDto<CartEntryDto> findAllEntriesForCart(Pageable pageable) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);
        WsDto<CartEntryDto> cartEntryDtoWsDto = new WsDto<>();
        cartEntryDtoWsDto.setDtoList(modelMapper.map(cartEntryPage.getContent(), listType));
        cartEntryDtoWsDto.setTotalRecords(cartEntryPage.getTotalElements());
        cartEntryDtoWsDto.setTotalPages(cartEntryPage.getTotalPages());
        cartEntryDtoWsDto.setSizePerPage(pageable.getPageSize());
        cartEntryDtoWsDto.setPage(pageable.getPageNumber());
        return cartEntryDtoWsDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        cartEntryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public void deleteAllByCart(String cart) {
        List<CartEntry> cartEntries = cartEntryRepository.findByCart(cart);
        if (cartEntries.isEmpty()) {
            return;
        }
        cartEntryRepository.deleteAll(cartEntries);
    }
}
