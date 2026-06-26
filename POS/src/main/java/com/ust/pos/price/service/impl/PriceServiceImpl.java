package com.ust.pos.price.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PriceServiceImpl extends BaseService implements PriceService {

    private static final String VALIDATION_MESSAGE = "Price with identifier - ";
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
            priceDto.setMessage(
                    existingPrice.isDeleted()
                            ? VALIDATION_MESSAGE + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : VALIDATION_MESSAGE + identifier
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

        priceDto.setIdentifier(priceDto.getProduct() + "." + priceDto.getPriceType());
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);

        if (existingPrice == null) {
            priceDto.setMessage(VALIDATION_MESSAGE + identifier + " not found");
            priceDto.setSuccess(false);
            return priceDto;
        }

        modelMapper.map(priceDto, existingPrice);
        setModifiedDetails(existingPrice);
        priceRepository.save(existingPrice);

        return priceDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        Price price = priceRepository.findByIdentifier(identifier);
        setModifiedDetails(price);
        softDelete(price);
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {

        Page<Price> pricePage = priceRepository.findByIsDeletedFalse(pageable);

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



