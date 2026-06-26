package com.ust.pos.cartentry.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.CartEntry;
import com.ust.pos.modell.CartEntryRepository;
import com.ust.pos.price.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CartEntryServiceImpl extends BaseService implements CartEntryService {

    private final CartEntryRepository cartEntryRepository;
    private final ModelMapper modelMapper;
    private final PriceService priceService;

    @Override
    public CartEntryDto save(CartEntryDto dto) {
        validateProductIdentifier(dto.getProductIdentifier());

        String productId = dto.getProductIdentifier();
        String cartId = dto.getCartIdentifier();
        int qty = dto.getQuantity();
        String identifier = cartId + "-" + productId;

        CartEntry entry = cartEntryRepository.findByIdentifier(identifier);

        if (isRemovalRequest(qty, entry)) {
            return removeEntry(entry, productId, cartId);
        }

        PriceDto selling = priceService.findByIdentifier(productId + "-SELLING");
        PriceDto mrp = priceService.findByIdentifier(productId + "-MRP");
        validatePriceExists(selling, mrp, productId);

        BigDecimal unitPrice = resolveUnitPrice(selling, mrp);
        BigDecimal discount = resolveDiscount(selling, mrp);

        entry = prepareEntry(entry, identifier, productId, cartId);
        applyQuantityAndPrice(entry, qty, unitPrice, discount);

        cartEntryRepository.save(entry);
        return modelMapper.map(entry, CartEntryDto.class);
    }

    private void validateProductIdentifier(String productId) {
        if (productId == null || productId.isBlank()) {
            throw new NoSuchElementException("Product identifier is missing");
        }
    }

    private boolean isRemovalRequest(int qty, CartEntry entry) {
        if (qty == -1000) {
            return true;
        }
        return entry != null && !entry.getDeleted() && (entry.getQuantity() + qty) <= 0;
    }

    private CartEntryDto removeEntry(CartEntry entry, String productId, String cartId) {
        if (entry != null && !entry.getDeleted()) {
            softDelete(entry);
            setModifiedDetails(entry);
            cartEntryRepository.save(entry);
        }
        CartEntryDto removedDto = new CartEntryDto();
        removedDto.setProductIdentifier(productId);
        removedDto.setCartIdentifier(cartId);
        removedDto.setQuantity(0);
        return removedDto;
    }

    private void validatePriceExists(PriceDto selling, PriceDto mrp, String productId) {
        if (selling == null && mrp == null) {
            throw new NoSuchElementException("Price not configured for product: " + productId);
        }
    }

    private BigDecimal resolveUnitPrice(PriceDto selling, PriceDto mrp) {
        return selling != null ? selling.getPriceAmount() : mrp.getPriceAmount();
    }

    private BigDecimal resolveDiscount(PriceDto selling, PriceDto mrp) {
        if (selling != null && mrp != null) {
            return mrp.getPriceAmount().subtract(selling.getPriceAmount());
        }
        return BigDecimal.ZERO;
    }

    private CartEntry prepareEntry(CartEntry entry, String identifier, String productId, String cartId) {
        if (entry == null || entry.getDeleted()) {
            if (entry == null) {
                entry = new CartEntry();
            }
            entry.setIdentifier(identifier);
            entry.setProductIdentifier(productId);
            entry.setCartIdentifier(cartId);
            entry.setQuantity(0);
            entry.setDeleted(false);
            if (entry.getStatus() == null) {
                entry.setStatus(true);
            }
            setCreatedDetails(entry);
        } else {
            setModifiedDetails(entry);
        }
        return entry;
    }

    private void applyQuantityAndPrice(CartEntry entry, int qty, BigDecimal unitPrice, BigDecimal discount) {
        int updatedQty = entry.getQuantity() + qty;
        entry.setQuantity(updatedQty);
        entry.setUnitPrice(unitPrice);
        entry.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(updatedQty)));
        entry.setDiscount(discount);
    }

    @Override
    public CartEntryDto findByIdentifier(String identifier) {
        return modelMapper.map(cartEntryRepository.findByIdentifierAndDeletedFalse(identifier), CartEntryDto.class);
    }

    @Override
    public WsDto<CartEntryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CartEntryDto>>() {
        }.getType();
        Page<CartEntry> cartEntryPage = cartEntryRepository.findAllByDeletedFalse(pageable);
        WsDto<CartEntryDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(cartEntryPage.getContent(), listType));
        wsDto.setTotalRecords(cartEntryPage.getTotalElements());
        wsDto.setTotalPage(cartEntryPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }
}