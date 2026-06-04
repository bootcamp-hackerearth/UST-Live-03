package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.price.service.PriceService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PriceServiceImpl implements PriceService {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<PriceDto> findAll(Pageable pageable) {

        List<Price> priceList;

        if (pageable == null) {
            priceList = priceRepository.findAll();
        } else {
            Page<Price> pricePage = priceRepository.findAll(pageable);
            priceList = pricePage.getContent();
        }

        List<PriceDto> priceDtoList = new ArrayList<>();

        for (Price price : priceList) {
            PriceDto priceDto = modelMapper.map(price, PriceDto.class);

            if (productRepository.existsByIdentifier(price.getProduct())) {
                Product product = productRepository.findByIdentifier(price.getProduct());
                priceDto.setProduct(product.getName());
            }

            priceDtoList.add(priceDto);
        }

        return priceDtoList;
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        boolean exists = productRepository.existsByIdentifier(priceDto.getProduct());

        if (!exists) {
            PriceDto response = new PriceDto();
            response.setMessage("Product not found");
            response.setSuccess(false);
            return response;
        }

        priceDto.setIdentifier(priceDto.getProduct() + priceDto.getPriceType());
        Price existingPrice = priceRepository.findByIdentifier(priceDto.getIdentifier());
        if (existingPrice != null) {
            PriceDto response = new PriceDto();
            response.setMessage(priceDto.getPriceType() + " already set for " + priceDto.getProduct());
            response.setSuccess(false);
            return response;
        }

        Price price = modelMapper.map(priceDto, Price.class);
        Price savedPrice = priceRepository.save(price);

        PriceDto response = modelMapper.map(savedPrice, PriceDto.class);
        response.setMessage("Successfully added the price");
        response.setSuccess(true);

        return response;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        return modelMapper.map(price, PriceDto.class);
    }

    @Transactional
    @Override
    public PriceDto update(PriceDto priceDto) {

        String identifier = priceDto.getIdentifier();
        Price exisingPrice = priceRepository.findByIdentifier(identifier);
        if (exisingPrice == null) {
            priceDto.setMessage("Price not found");
            priceDto.setSuccess(false);
            return priceDto;
        }

        modelMapper.map(priceDto, exisingPrice);
        priceRepository.save(exisingPrice);
        priceDto.setMessage("Price updated successfully");
        priceDto.setSuccess(true);

        return priceDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        priceRepository.deleteByIdentifier(identifier);
    }
}
