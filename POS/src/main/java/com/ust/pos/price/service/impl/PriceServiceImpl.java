package com.ust.pos.price.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.PriceDto;
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
@Transactional
public class PriceServiceImpl extends CommonService implements PriceService {

    private final PriceRepository priceRepository;
    private final ModelMapper modelMapper;

    PriceServiceImpl(PriceRepository priceRepository, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.priceRepository = priceRepository;
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (existingPrice != null) {
            priceDto.setMessage("Price with identifier - " + identifier + " already exists");
            priceDto.setSuccess(false);
            return priceDto;
        }
        priceDto.setDifference((priceDto.getSellingPrice().subtract(priceDto.getCostPrice())).
                subtract(priceDto.getDiscountPrice()));
        priceDto.setSellingPrice(priceDto.getSellingPrice().subtract(priceDto.getDiscountPrice()));
        Price price = modelMapper.map(priceDto, Price.class);
        setAuditFields(price, true);
        priceRepository.save(price);
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (existingPrice == null) {
            priceDto.setMessage("Price with identifier - " + identifier + " is not found");
            priceDto.setSuccess(false);
            return priceDto;
        }
        priceDto.setDifference(priceDto.getSellingPrice().subtract(priceDto.getCostPrice()));
        modelMapper.map(priceDto, existingPrice);
        setAuditFields(existingPrice, false);
        priceRepository.save(existingPrice);
        return priceDto;
    }

    @Override
    public void delete(String identifier) {
        Price price = priceRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (price != null) {
            price.setDelete(true);
            setAuditFields(price, false);
            priceRepository.save(price);
        }
    }

    @Override
    public List<PriceDto> findAll() {
        Type listOfType = new TypeToken<List<PriceDto>>() {
        }.getType();
        return modelMapper.map(priceRepository.findByIsDeleteFalse(), listOfType);
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        return modelMapper.map(priceRepository.
                findByIdentifierAndIsDeleteFalse(identifier), PriceDto.class);
    }

    @Override
    public Page<PriceDto> findAll(Pageable pageable, String search) {
        Page<Price> prices;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Price> specification = buildGlobalSearchSpec(Price.class, search);
            prices = priceRepository.findAll(specification, pageable);
        } else {
            prices = priceRepository.findByIsDeleteFalse(pageable);
        }

        return prices.map(price -> modelMapper.map(price, PriceDto.class));
    }
}
