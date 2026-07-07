package com.ust.pos.price.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Price;
import com.ust.pos.modell.PriceRepository;
import com.ust.pos.price.service.PriceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl extends BaseService implements PriceService {

    private final ModelMapper modelMapper;
    private final PriceRepository priceRepository;

    @Override
    public PriceDto findByIdentifier(String identifier) {
        Price price = priceRepository.findByIdentifierAndDeletedFalse(identifier);
        if (price == null) {
            throw new ResourceNotFoundException("price with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        String identifier = priceDto.getProduct() + "-" + priceDto.getType();
        priceDto.setIdentifier(identifier);
        Price existingPrice = priceRepository.findByIdentifier(identifier);

        if (existingPrice != null) {
            if (Boolean.TRUE.equals(existingPrice.getDeleted())) {
                priceDto.setMessage("Price with Identifier " + identifier + " already exists (Soft-Deleted)");
                priceDto.setSuccess(false);
                return priceDto;
            }
            priceDto.setMessage("Price already exists for product + type");
            priceDto.setSuccess(false);
            return priceDto;
        }

        Price price = modelMapper.map(priceDto, Price.class);
        price.setIdentifier(identifier);
        if (price.getStatus() == null) {
            price.setStatus(true);
        }
        setCreatedDetails(price);
        priceRepository.save(price);
        priceDto.setSuccess(true);
        return priceDto;
    }


    @Override
    public PriceDto update(PriceDto priceDto) {
        Price existingPrice = priceRepository.findByIdentifierAndDeletedFalse(priceDto.getIdentifier());

        if (existingPrice == null) {
            priceDto.setMessage("Price not found");
            priceDto.setSuccess(false);
            return priceDto;
        }
        String newIdentifier = priceDto.getProduct() + "-" + priceDto.getType();
        Price duplicate = priceRepository.findByIdentifierAndDeletedFalse(newIdentifier);

        if (duplicate != null && !duplicate.getId().equals(existingPrice.getId())) {
            priceDto.setMessage("Price already exists for this product and type");
            priceDto.setSuccess(false);
            return priceDto;
        }
        String originalCreatedBy = existingPrice.getCreatedBy();
        java.time.LocalDateTime originalCreatedOn = existingPrice.getCreatedOn();

        existingPrice.setProduct(priceDto.getProduct());
        existingPrice.setPriceAmount(priceDto.getPriceAmount());
        existingPrice.setType(priceDto.getType());
        existingPrice.setIdentifier(newIdentifier);

        existingPrice.setCreatedBy(originalCreatedBy);
        existingPrice.setCreatedOn(originalCreatedOn);

        setModifiedDetails(existingPrice);

        priceRepository.save(existingPrice);

        return modelMapper.map(existingPrice, PriceDto.class);
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Price price = priceRepository.findByIdentifierAndDeletedFalse(identifier);
        if (price != null) {
            softDelete(price);
            setModifiedDetails(price);
            priceRepository.save(price);
        }
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> pricePage = priceRepository.findAllByDeletedFalse(pageable);

        WsDto<PriceDto> priceWsDto = new WsDto<>();
        priceWsDto.setDtoList(modelMapper.map(pricePage.getContent(), listType));
        priceWsDto.setTotalRecords(pricePage.getTotalElements());
        priceWsDto.setTotalPage(pricePage.getTotalPages());
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
        wsDto.setTotalPage(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}