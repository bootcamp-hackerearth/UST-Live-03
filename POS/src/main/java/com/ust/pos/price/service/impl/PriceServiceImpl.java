package com.ust.pos.price.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
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
            throw new ResourseNotFoundException("Data cannot found");
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

    @Override
    public WsDto<PriceDto> findAll(Specification<Price> example, Pageable pageable) {

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> page = priceRepository.findAll(example, pageable);

        WsDto<PriceDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}



