package com.ust.pos.cartentry.service.impl;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
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
    CartEntryRepository cartEntryRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    PriceService priceService;

    @Override
    public CartEntryDto save(CartEntryDto cartEntryDto) {
        String product = cartEntryDto.getProductIdentifier();
        BigDecimal qty = cartEntryDto.getQuantity();
        String cart = cartEntryDto.getCartIdentifier();
        String identifier = product + "_" + cart;
        CartEntry cartEntry = cartEntryRepository.findByIdentifier(identifier);
        if (cartEntry == null) {
            cartEntry = new CartEntry();
        }
        BigDecimal existQty = cartEntry.getQuantity();
        BigDecimal updatedQuantity = existQty.add(qty);
        cartEntry.setIdentifier(identifier);
        cartEntry.setProductIdentifier(product);
        cartEntry.setQuantity(updatedQuantity);
        cartEntry.setCartIdentifier(cart);
        PriceDto sellingpriceDto = priceService.findByIdentifier(cartEntry.getProductIdentifier() + "_" + "SELLING_PRICE");
        PriceDto mrpDto = priceService.findByIdentifier(cartEntry.getProductIdentifier() + "_" + "MRP");
        cartEntry.setUnitPrice(BigDecimal.valueOf(sellingpriceDto.getAmount()));
        Long mrp = mrpDto.getAmount();
        cartEntry.setOriginalPrice(BigDecimal.valueOf(mrp).multiply(updatedQuantity));
        cartEntry.setDiscount((BigDecimal.valueOf(mrp).subtract(cartEntry.getUnitPrice())).multiply(updatedQuantity));
        cartEntry.setTotalPrice(cartEntry.getUnitPrice().multiply(updatedQuantity));
        cartEntryRepository.save(cartEntry);
        return modelMapper.map(cartEntry, CartEntryDto.class);
    }

    @Override
    public WsDto<CartEntryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        if (pageable == null) {
            List<CartEntryDto> cartEntryDtoList = modelMapper.map(cartEntryRepository.findAll(), listType);
            WsDto<CartEntryDto> response = new WsDto<>();
            response.setDtoList(cartEntryDtoList);
            response.setTotalRecords(cartEntryDtoList.size());
            return response;
        }
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAll(pageable);
        List<CartEntryDto> cartEntryDtoList = modelMapper.map(cartEntryPage.getContent(), listType);
        WsDto<CartEntryDto> wsDto = new WsDto<>();
        wsDto.setDtoList(cartEntryDtoList);
        wsDto.setPage(cartEntryPage.getNumber());
        wsDto.setSizePerPage(cartEntryPage.getSize());
        wsDto.setTotalPages(cartEntryPage.getTotalPages());
        wsDto.setTotalRecords(cartEntryPage.getTotalElements());
        return wsDto;
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(cartEntryRepository.findByIdentifier(identifier), CartEntryDto.class);
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        cartEntryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<CartEntryDto> findByCart(String cartIdentifier) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        return modelMapper.map(cartEntryRepository.findByCartIdentifier(cartIdentifier), listType);
    }

}