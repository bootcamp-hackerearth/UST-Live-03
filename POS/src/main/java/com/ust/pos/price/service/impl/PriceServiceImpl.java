package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private static final String PRICE_EXISTS = "Price already exists for Product '";
    private static final String PRICE_NOT_FOUND = "Price not found for Product '";
    private static final String WITH_PRICE_TYPE = "' with Price Type '";

    private final PriceRepository priceRepository;
    private final ModelMapper modelMapper;

    @Override
    public PriceDto findByIdentifier(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        if (price == null) {
            throw new ResourceNotFoundException("Price with identifier " + identifier + " not found");
        }
        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        String identifier = priceDto.getProduct() + "_" + priceDto.getPriceType();
        priceDto.setIdentifier(identifier);

        Price existingPrice = priceRepository.findByIdentifier(identifier);

        if (existingPrice != null) {
            if (Boolean.TRUE.equals(existingPrice.getIsDeleted())) {
                priceDto.setMessage(PRICE_EXISTS + priceDto.getProduct() + WITH_PRICE_TYPE + priceDto.getPriceType() + "' was deleted. Contact admin for further support or try with a different identifier.");
            } else {
                priceDto.setMessage(PRICE_EXISTS + priceDto.getProduct() + WITH_PRICE_TYPE + priceDto.getPriceType() + "'");
            }
            priceDto.setSuccess(false);
            return priceDto;
        }

        Price price = modelMapper.map(priceDto, Price.class);
        price.setIsDeleted(false);
        priceRepository.save(price);
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        String oldIdentifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(oldIdentifier);

        if (existingPrice == null) {
            priceDto.setMessage(PRICE_NOT_FOUND + priceDto.getProduct() + WITH_PRICE_TYPE + priceDto.getPriceType() + "'");
            priceDto.setSuccess(false);
            return priceDto;
        }

        String newIdentifier = priceDto.getProduct() + "_" + priceDto.getPriceType();
        Price duplicate = priceRepository.findByIdentifier(newIdentifier);

        if (duplicate != null && !duplicate.getId().equals(existingPrice.getId())) {
            priceDto.setMessage("Another price already exists for this Product + Price Type");
            priceDto.setSuccess(false);
            return priceDto;
        }

        existingPrice.setCostPrice(priceDto.getCostPrice());
        existingPrice.setPriceType(priceDto.getPriceType());
        existingPrice.setProduct(priceDto.getProduct());
        existingPrice.setIdentifier(newIdentifier);
        priceRepository.save(existingPrice);
        priceDto.setIdentifier(newIdentifier);
        return priceDto;
    }

    @Override
    public PriceDto delete(String identifier) {
        PriceDto priceDto = new PriceDto();
        Price price = priceRepository.findByIdentifier(identifier);

        if (price == null) {
            priceDto.setMessage("Price with identifier - " + identifier + " not found");
            priceDto.setSuccess(false);
            return priceDto;
        }

        price.setIsDeleted(true);
        price.setStatus(false);
        priceRepository.save(price);
        priceDto.setSuccess(true);
        priceDto.setMessage("Price deleted successfully");
        return priceDto;
    }

    @Override
    public PaginatedResponseDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> pricePage = priceRepository.findByIsDeleted(false, pageable);
        List<PriceDto> items = modelMapper.map(pricePage.getContent(), listType);
        PaginatedResponseDto<PriceDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(pricePage.getTotalElements());
        response.setTotalPages(pricePage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public List<PriceDto> findAllActive() {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        return modelMapper.map(priceRepository.findByStatusAndIsDeleted(true, false), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Price price = priceRepository.findByIdentifier(identifier);
        price.setStatus(status);
        priceRepository.save(price);
    }

    @Override
    public PaginatedResponseDto<PriceDto> findAll(Specification<Price> example, Pageable pageable) {

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> page = priceRepository.findAll(example, pageable);

        PaginatedResponseDto<PriceDto> paginatedResponseDto = new PaginatedResponseDto<>();
        paginatedResponseDto.setItems(modelMapper.map(page.getContent(), listType));
        paginatedResponseDto.setTotalRecords(page.getTotalElements());
        paginatedResponseDto.setTotalPages(page.getTotalPages());
        paginatedResponseDto.setSizePerPage(pageable.getPageSize());
        paginatedResponseDto.setPage(pageable.getPageNumber());

        return paginatedResponseDto;
    }
}