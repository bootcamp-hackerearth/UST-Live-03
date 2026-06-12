package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
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
@Transactional
public class PriceServiceImpl implements PriceService {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PriceDto save(PriceDto priceDto) {

        String identifier = priceDto.getProductId() + "_" + priceDto.getPriceType().replace(" ", "_");
        Price existing = priceRepository.findByIdentifier(identifier);
        if (existing != null) {
            priceDto.setSuccess(false);
            priceDto.setMessage("Price already exists");
            return priceDto;
        }
        priceDto.setIdentifier(identifier);
        priceDto.setProductName(productService.findByIdentifier(priceDto.getProductId()).getProductName());
        Price price = modelMapper.map(priceDto, Price.class);
        priceRepository.save(price);
        priceDto.setSuccess(true);
        priceDto.setMessage("Price saved successfully");
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        Price existing = priceRepository.findByIdentifier(priceDto.getIdentifier());
        if (existing == null) {
            priceDto.setSuccess(false);
            priceDto.setMessage("Price not found");
            return priceDto;
        }
        priceDto.setProductName(productService.findByIdentifier(priceDto.getProductId()).getProductName());
        existing.setProductId(priceDto.getProductId());
        existing.setProductName(priceDto.getProductName());
        existing.setPriceType(priceDto.getPriceType());
        existing.setValue(priceDto.getValue());
        priceRepository.save(existing);
        priceDto.setSuccess(true);
        priceDto.setMessage("Price updated successfully");
        return priceDto;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        if (price == null) {
            PriceDto dto = new PriceDto();
            dto.setSuccess(false);
            dto.setMessage("Price not found");
            return dto;
        }
        PriceDto dto = modelMapper.map(price, PriceDto.class);
        dto.setSuccess(true);
        return dto;
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

    @Override
    public void delete(String identifier) {
        priceRepository.deleteByIdentifier(identifier);
    }
}