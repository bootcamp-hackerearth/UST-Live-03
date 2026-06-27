package com.ust.pos.price.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Price;
import com.ust.pos.modell.PriceRepository;
import com.ust.pos.price.service.PriceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl extends BaseService implements PriceService {

    private final ModelMapper modelMapper;
    private final PriceRepository priceRepository;

    @Override
    public PriceDto findByIdentifier(String identifier) {
        Price price = priceRepository.findByIdentifierAndDeletedFalse(identifier);
        return price != null ? modelMapper.map(price, PriceDto.class) : null;
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        String identifier = priceDto.getProduct() + "-" + priceDto.getType();
        priceDto.setIdentifier(identifier);
        Price existingPrice = priceRepository.findByIdentifier(identifier);

        if (existingPrice != null) {

            if (Boolean.TRUE.equals(existingPrice.getDeleted())) {
                priceDto.setMessage("Price with identifier - " + identifier + " was deleted and cannot be created again.");
                priceDto.setSuccess(false);
                return priceDto;
            }
            priceDto.setMessage("Price already exists for product and type");
            priceDto.setSuccess(false);
            return priceDto;
        }

        Price price = modelMapper.map(priceDto, Price.class);
        price.setIdentifier(identifier);
        setCreatedDetails(price);
        Price saved = priceRepository.save(price);
        PriceDto response = modelMapper.map(saved, PriceDto.class);
        response.setSuccess(true);
        response.setMessage("Price saved successfully");
        return response;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        Price existingPrice = priceRepository.findByIdentifierAndDeletedFalse(priceDto.getIdentifier());

        if (existingPrice == null) {
            priceDto.setMessage("Price not found");
            priceDto.setSuccess(false);
            return priceDto;
        }

        String newIdentifier = priceDto.getProduct() + "-" + priceDto.getType();
        Price duplicate = priceRepository.findByIdentifier(newIdentifier);

        if (duplicate != null && !duplicate.getId().equals(existingPrice.getId())) {
            priceDto.setMessage("Price already exists for this product and type");
            priceDto.setSuccess(false);
            return priceDto;
        }

        existingPrice.setProduct(priceDto.getProduct());
        existingPrice.setPriceAmount(priceDto.getPriceAmount());
        existingPrice.setType(priceDto.getType());
        existingPrice.setIdentifier(newIdentifier);
        setModifiedDetails(existingPrice);
        Price updated = priceRepository.save(existingPrice);
        PriceDto response = modelMapper.map(updated, PriceDto.class);
        response.setSuccess(true);
        response.setMessage("Price updated successfully");
        return response;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Price price = priceRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(price);
        setModifiedDetails(price);
        priceRepository.save(price);
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {}.getType();
        Page<Price> pricePage = priceRepository.findAllByDeletedFalse(pageable);
        WsDto<PriceDto> priceWsDto = new WsDto<>();
        priceWsDto.setDtoList(modelMapper.map(pricePage.getContent(), listType));
        priceWsDto.setTotalRecords(pricePage.getTotalElements());
        priceWsDto.setTotalPage(pricePage.getTotalPages());
        priceWsDto.setSizePerPage(pageable.getPageSize());
        priceWsDto.setPage(pageable.getPageNumber());
        return priceWsDto;
    }
}