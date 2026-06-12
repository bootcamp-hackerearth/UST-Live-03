package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.model.ProductRepository;
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
import java.util.Optional;

@Service
public class PriceServiceImpl implements PriceService {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PaginationResponseDto<PriceDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();

        if (pageable == null) {

            List<PriceDto> priceDtoList =
                    modelMapper.map(
                            priceRepository.findAll(),
                            listType
                    );

            PaginationResponseDto<PriceDto> response =
                    new PaginationResponseDto<>();

            response.setDtoList(priceDtoList);
            response.setTotalRecords(priceDtoList.size());

            return response;
        }

        Page<Price> pricePage =
                priceRepository.findAll(pageable);

        List<PriceDto> priceDtoList =
                modelMapper.map(
                        pricePage.getContent(),
                        listType
                );

        PaginationResponseDto<PriceDto> paginationResponseDto =
                new PaginationResponseDto<>();

        paginationResponseDto.setDtoList(priceDtoList);
        paginationResponseDto.setPage(pricePage.getNumber());
        paginationResponseDto.setSizePerPage(pricePage.getSize());
        paginationResponseDto.setTotalPages(pricePage.getTotalPages());
        paginationResponseDto.setTotalRecords(
                pricePage.getTotalElements()
        );

        return paginationResponseDto;
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
    public PriceDto findById(long id) {
        Price price = priceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Price not found"));
        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);

        if (price == null) {
            return null;
        }
        return modelMapper.map(price, PriceDto.class);

    }

    @Transactional
    @Override
    public PriceDto update(PriceDto priceDto) {
        Optional<Price> priceOptional = priceRepository.findById(priceDto.getId());

        if (priceOptional.isEmpty()) {
            priceDto.setMessage("Price - " + priceDto.getIdentifier() + " not found");
            priceDto.setSuccess(false);
            return priceDto;
        }

        Price existingPrice = priceOptional.get();

        modelMapper.map(priceDto, existingPrice);
        priceRepository.save(existingPrice);
        priceDto.setMessage("Successfully updated price");
        priceDto.setSuccess(true);

        return priceDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        priceRepository.deleteByIdentifier(identifier);
    }
}
