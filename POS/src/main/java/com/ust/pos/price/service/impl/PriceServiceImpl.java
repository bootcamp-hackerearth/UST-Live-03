package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PriceDto;
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
    public PriceDto save(PriceDto priceDto) {
        String identifier =priceDto.getIdentifier();
        Price existingProduct = priceRepository.findByIdentifier(identifier);
        if (existingProduct != null) {
           priceDto.setMessage("Product with identifier - " + identifier + " already exists");
           priceDto.setSuccess(false);
            return priceDto;
        }
        Price price = modelMapper.map(priceDto, Price.class);
        priceRepository.save(price);
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        String identifier =priceDto.getIdentifier();
        Price existingProduct = priceRepository.findByIdentifier(identifier);
        if (existingProduct == null) {
           priceDto.setMessage("Product with identifier - " + identifier + " not found");
           priceDto.setSuccess(false);
            return priceDto;
        }
        modelMapper.map(priceDto, existingProduct);
        priceRepository.save(existingProduct);
        return priceDto;
    }

    @Override
    @Transactional
    public boolean delete(String identifier) {
        priceRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public PageDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> pricePage = priceRepository.findAll(pageable);
        PageDto<PriceDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(pricePage.getContent(), listType));
        pageDto.setTotalRecords(pricePage.getTotalElements());
        pageDto.setTotalPages(pricePage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {

        Price price = priceRepository.findByIdentifier(identifier);

        PriceDto priceDto = new PriceDto();

        if (price == null) {
            priceDto.setSuccess(false);
            priceDto.setMessage("Price not found");
            return priceDto;
        }

        return modelMapper.map(price, PriceDto.class);
    }
}
