package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PriceServiceImpl implements PriceService {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PriceDto findByIdentifier(String identifier) {

        Price price = priceRepository.findByIdentifier(identifier);

        if (price == null) {
            return null;
        }

        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public PriceDto save(PriceDto priceDto) {

        priceDto.setIdentifier(priceDto.getProduct() + "." + priceDto.getPriceType());
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);

        if (existingPrice != null) {
            priceDto.setMessage("Price with identifier - " + identifier + " already exists");
            priceDto.setSuccess(false);
            return priceDto;
        }

        Price price = modelMapper.map(priceDto, Price.class);
        priceRepository.save(price);

        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {

        priceDto.setIdentifier(priceDto.getProduct() + "." + priceDto.getPriceType());
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);

        if (existingPrice == null) {
            priceDto.setMessage("Price with identifier - " + identifier + " not found");
            priceDto.setSuccess(false);
            return priceDto;
        }

        modelMapper.map(priceDto, existingPrice);
        priceRepository.save(existingPrice);

        return priceDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        priceRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {

        Page<Price> pricePage = priceRepository.findAll(pageable);

        WsDto<PriceDto> priceDto = new WsDto<>();

        List<PriceDto> priceDtos = pricePage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, PriceDto.class))
                .toList();

        priceDto.setContent(priceDtos);
        priceDto.setPage(pricePage.getNumber());
        priceDto.setSizePerPage(pricePage.getSize());
        priceDto.setTotalPages(pricePage.getTotalPages());
        priceDto.setTotalRecords(pricePage.getTotalElements());

        return priceDto;
    }
}



