package com.ust.pos.price.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.*;
import com.ust.pos.price.service.PriceService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
public class PriceServiceImpl extends BaseService implements PriceService {

    private static final String DELETED_MESSAGE =
            " has been deleted. Please contact the administrator.";

    private final PriceRepository priceRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public PriceServiceImpl(PriceRepository priceRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.priceRepository = priceRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PaginationResponseDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();

        Page<Price> pricePage =
                priceRepository.findByIsDeletedFalse(pageable);

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
    public PaginationResponseDto<PriceDto> findAll(Specification<Price> example, Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Price> page = priceRepository.findAll(example, pageable);

        PaginationResponseDto<PriceDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationResponseDto.setTotalRecords(page.getTotalElements());
        paginationResponseDto.setTotalPages(page.getTotalPages());
        paginationResponseDto.setSizePerPage(pageable.getPageSize());
        paginationResponseDto.setPage(pageable.getPageNumber());

        return paginationResponseDto;
    }

    @Override
    public PriceDto save(PriceDto priceDto) {

        Product product = productRepository.findByIdentifier(priceDto.getProduct());

        if (product == null) {
            PriceDto response = new PriceDto();
            response.setMessage("Product not found");
            response.setSuccess(false);
            return response;
        }

        if (product.isDeleted()) {
            PriceDto response = new PriceDto();
            response.setMessage(
                    "Product " + product.getIdentifier()
                            + DELETED_MESSAGE
            );
            response.setSuccess(false);
            return response;
        }

        priceDto.setIdentifier(
                priceDto.getProduct() + priceDto.getPriceType()
        );

        Price existingPrice =
                priceRepository.findByIdentifier(priceDto.getIdentifier());

        if (existingPrice != null) {

            if (existingPrice.isDeleted()) {
                PriceDto response = new PriceDto();
                response.setMessage(
                        "Price " + priceDto.getIdentifier()
                                + DELETED_MESSAGE
                );
                response.setSuccess(false);
                return response;
            }

            PriceDto response = new PriceDto();
            response.setMessage(
                    priceDto.getPriceType()
                            + " already set for "
                            + priceDto.getProduct()
            );
            response.setSuccess(false);
            return response;
        }

        Price price = modelMapper.map(priceDto, Price.class);

        setCreatedDetails(price);

        Price savedPrice = priceRepository.save(price);

        PriceDto response =
                modelMapper.map(savedPrice, PriceDto.class);

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

        Optional<Price> priceOptional =
                priceRepository.findById(priceDto.getId());

        if (priceOptional.isEmpty()) {
            priceDto.setMessage(
                    "Price - " + priceDto.getIdentifier() + " not found"
            );
            priceDto.setSuccess(false);
            return priceDto;
        }

        Price existingPrice = priceOptional.get();

        if (existingPrice.isDeleted()) {
            priceDto.setMessage(
                    "Price " + existingPrice.getIdentifier()
                            + DELETED_MESSAGE
            );
            priceDto.setSuccess(false);
            return priceDto;
        }

        Product product =
                productRepository.findByIdentifier(existingPrice.getProduct());

        if (product != null && product.isDeleted()) {
            priceDto.setMessage(
                    "Product " + product.getIdentifier()
                            + DELETED_MESSAGE
            );
            priceDto.setSuccess(false);
            return priceDto;
        }

        modelMapper.map(priceDto, existingPrice);

        setModifiedDetails(existingPrice);

        priceRepository.save(existingPrice);

        priceDto.setMessage("Successfully updated price");
        priceDto.setSuccess(true);

        return priceDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        softDelete(price);
        setModifiedDetails(price);
        priceRepository.save(price);
    }
}
