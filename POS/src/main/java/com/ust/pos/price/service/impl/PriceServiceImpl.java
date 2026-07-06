package com.ust.pos.price.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
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
@RequiredArgsConstructor
public class PriceServiceImpl extends BaseService implements PriceService {

    private final PriceRepository priceRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public PriceDto findByIdentifier(String identifier) {
        Price price = priceRepository.findByIdentifierAndDeletedFalse(identifier);
        if (price == null) {
            return null;
        }
        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        Product product = productRepository.findByIdentifier(priceDto.getProduct());
        priceDto.setIdentifier(product.getName() + "_" + priceDto.getPriceType());
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice != null) {
            if (Boolean.TRUE.equals(existingPrice.getDeleted())) {
                priceDto.setMessage("Price identifier - " + identifier + " not available");
                priceDto.setSuccess(false);
                return priceDto;
            }
            priceDto.setMessage("Price with identifier - " + identifier + " already exists");
            priceDto.setSuccess(false);
            return priceDto;
        }
        Price newPrice = modelMapper.map(priceDto, Price.class);
        setCreatedDetails(newPrice);
        setModifiedDetails(newPrice);
        priceRepository.save(newPrice);
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
        setModifiedDetails(existingPrice);
        priceRepository.save(existingPrice);
        priceDto.setIdentifier(newIdentifier);
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
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> pricePage = priceRepository.findAllByDeletedFalse(pageable);

        WsDto<PriceDto> priceWsDto = new WsDto<>();
        priceWsDto.setDtoList(modelMapper.map(pricePage.getContent(), listType));
        priceWsDto.setTotalRecords(pricePage.getTotalElements());
        priceWsDto.setTotalPages(pricePage.getTotalPages());
        priceWsDto.setSizePerPage(pageable.getPageSize());
        priceWsDto.setPage(pageable.getPageNumber());
        return priceWsDto;
    }

    @Override
    public WsDto<PriceDto> findAll(Specification<Price> example, Pageable pageable) {

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> page = priceRepository.findAll(example, pageable);

        WsDto<PriceDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}



