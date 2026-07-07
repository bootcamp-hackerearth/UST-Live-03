package com.ust.pos.price.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
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
public class PriceServiceImpl extends BaseService implements PriceService {

    private final PriceRepository priceRepository;
    private final ModelMapper modelMapper;

    public PriceServiceImpl(PriceRepository priceRepository, ModelMapper modelMapper) {
        this.priceRepository = priceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);

        if (price == null) {
            throw new ResourceNotFoundException(
                    "Price with identifier '" + identifier + "' not found");
        }

        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        priceDto.setIdentifier(priceDto.getProduct() + priceDto.getPriceType());
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice != null) {
            priceDto.setMessage(
                    existingPrice.isDeleted()
                            ? " Price with identifier - " + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : " Price with identifier - " + identifier
                            + " already exists."
            );
            priceDto.setSuccess(false);
            return priceDto;
        }
        Price price = modelMapper.map(priceDto, Price.class);
        setCreatedDetails(price);
        priceRepository.save(price);
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice == null) {
            priceDto.setMessage("Price with identifier - " + identifier + " not found");
            priceDto.setSuccess(false);
            return priceDto;
        }
        modelMapper.map(priceDto, existingPrice);
        setModifiedDetails(existingPrice);
        priceRepository.save(existingPrice);
        return priceDto;
    }

    @Transactional
    public void delete(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        setModifiedDetails(price);
        softDelete(price);
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();

        Page<Price> pricePage = priceRepository.findByIsDeletedFalse(pageable);

        List<PriceDto> priceDtos = modelMapper.map(
                pricePage.getContent(),
                listType
        );

        WsDto<PriceDto> wsDto =
                new WsDto<>();

        wsDto.setContent(priceDtos);
        wsDto.setPage(pricePage.getNumber());
        wsDto.setSizePerPage(pricePage.getSize());
        wsDto.setTotalPages(pricePage.getTotalPages());
        wsDto.setTotalRecords(pricePage.getTotalElements());

        return wsDto;
    }

    @Override
    public PriceDto findByProductAndPriceType(String product, String priceType) {
        Price price = priceRepository.findByProductAndPriceType(product, priceType);

        if (price == null) {
            return null;
        }
        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public WsDto<PriceDto> findAll(Specification<Price> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> pricePage = priceRepository.findAll(example,pageable);
        WsDto<PriceDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(pricePage.getContent(), listType));
        wsDto.setTotalRecords(pricePage.getTotalElements());
        wsDto.setTotalPages(pricePage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}