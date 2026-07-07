package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.service.BaseService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class PriceServiceImpl extends BaseService implements PriceService {

    private final PriceRepository priceRepository;
    private final ModelMapper modelMapper;

    public PriceServiceImpl(PriceRepository priceRepository, ModelMapper modelMapper) {
        this.priceRepository = priceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingPrice != null) {
            priceDto.setMessage("Price with identifier - " + identifier + " already exists");
            priceDto.setSuccess(false);
            return priceDto;
        }

        priceDto.setDifference(priceDto.getSellingPrice().subtract(priceDto.getCostPrice()));

        Price price = modelMapper.map(priceDto, Price.class);
        priceRepository.save(price);

        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingPrice == null) {
            priceDto.setMessage("Price with identifier - " + identifier + " is not found");
            priceDto.setSuccess(false);
            return priceDto;
        }

        priceDto.setDifference(priceDto.getSellingPrice().subtract(priceDto.getCostPrice()));
        Price price = modelMapper.map(priceDto, Price.class);
        priceRepository.save(price);
        return priceDto;
    }

    @Override
    public void delete(String identifier) {
        Price price = priceRepository.findByIdentifierAndDeletedFalse(identifier);

        if (price != null) {
            price.setDeleted(true);
            priceRepository.save(price);
        }
    }

    @Override
    public List<PriceDto> findAll() {
        Type listOfType = new TypeToken<List<PriceDto>>() {
        }.getType();
        return modelMapper.map(priceRepository.findByDeletedFalse(), listOfType);
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        return modelMapper.map(priceRepository.findByIdentifierAndDeletedFalse(identifier), PriceDto.class);
    }

    @Override
    public Page<PriceDto> findAll(Pageable pageable, String search) {
        Page<Price> prices;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Price> specification = buildGlobalSearchSpec(Price.class, search);

            List<Price> filteredPrices = priceRepository.findAll(specification, pageable)
                    .getContent()
                    .stream()
                    .filter(price -> !price.isDeleted())
                    .toList();

            prices = new PageImpl<>(filteredPrices, pageable, filteredPrices.size());

        } else {
            prices = priceRepository.findByDeletedFalse(pageable);
        }

        return prices.map(price -> modelMapper.map(price, PriceDto.class));
    }
}