package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
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
        priceDto.setIdentifier(priceDto.getProduct() + "_" + priceDto.getPriceType());
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
        String oldIdentifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(oldIdentifier);
        if (existingPrice == null) {
            priceDto.setMessage("Price not found for Product '"
                    + priceDto.getProduct() + "' with Price Type '"
                    + priceDto.getPriceType() + "'");
            priceDto.setSuccess(false);
            return priceDto;
        }
        String product = existingPrice.getProduct().split(",")[0];
        String newIdentifier = product + "_" + priceDto.getPriceType();
        existingPrice.setProduct(product);
        Price duplicate = priceRepository.findByIdentifier(newIdentifier);
        if (duplicate != null && !duplicate.getId().equals(existingPrice.getId())) {
            priceDto.setMessage("Another price already exists for this Product + Price Type");
            priceDto.setSuccess(false);
            return priceDto;
        }
        existingPrice.setPriceAmount(priceDto.getPriceAmount());
        existingPrice.setPriceType(priceDto.getPriceType());
        existingPrice.setIdentifier(newIdentifier);
        priceRepository.save(existingPrice);
        priceDto.setIdentifier(newIdentifier);
        return priceDto;
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
        priceWsDto.setTotalPages(pricePage.getTotalPages());
        priceWsDto.setSizePerPage(pageable.getPageSize());
        priceWsDto.setPage(pageable.getPageNumber());
        return priceWsDto;
    }
}



