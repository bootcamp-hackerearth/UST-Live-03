package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Price;
import com.ust.pos.modell.PriceRepository;
import com.ust.pos.price.service.PriceService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class PriceServiceImpl implements PriceService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PriceRepository priceRepository;

    @Override
    public PriceDto findByIdentifier(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        return price != null ? modelMapper.map(price, PriceDto.class) : null;
    }

    @Override
    public PriceDto save(PriceDto priceDto) {

        String identifier = priceDto.getProduct() + "-" + priceDto.getType();
        priceDto.setIdentifier(identifier);

        Price existingPrice = priceRepository.findByIdentifier(identifier);

        if (existingPrice != null) {
            priceDto.setMessage("Price already exists for product + type");
            priceDto.setSuccess(false);
            return priceDto;
        }

        Price price = modelMapper.map(priceDto, Price.class);
        price.setIdentifier(identifier);

        priceRepository.save(price);

        priceDto.setSuccess(true);
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {

        Price existingPrice = priceRepository.findByIdentifier(priceDto.getIdentifier());

        if (existingPrice == null) {
            priceDto.setMessage("Price not found");
            priceDto.setSuccess(false);
            return priceDto;
        }
        String newIdentifier =  priceDto.getProduct() + "-" + priceDto.getType();

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

        priceRepository.save(existingPrice);

        return modelMapper.map(existingPrice,PriceDto.class);
    }


    @Override
    @Transactional
    public void delete(String identifier) {
        priceRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> pricePage = priceRepository.findAll(pageable);

        WsDto<PriceDto> priceWsDto = new WsDto<>();
        priceWsDto.setDtoList(modelMapper.map(pricePage.getContent(), listType));
        priceWsDto.setTotalRecords(pricePage.getTotalElements());
        priceWsDto.setTotalPage(pricePage.getTotalPages());
        priceWsDto.setSizePerPage(pageable.getPageSize());
        priceWsDto.setPage(pageable.getPageNumber());

        return priceWsDto;
    }
}