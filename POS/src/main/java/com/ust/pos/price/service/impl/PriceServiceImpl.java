package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class PriceServiceImpl implements PriceService {
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
        if(existingPrice != null)
        {
            priceDto.setMessage("Price for product - "+identifier+" already exists");
            priceDto.setSuccess(false);
            return priceDto;
        }
        priceDto.setDifference(priceDto.getCostPrice().subtract(priceDto.getSellingPrice()));
        Price price = modelMapper.map(priceDto, Price.class);
        price.setDeleted(false);
        priceRepository.save(price);
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifierAndDeletedFalse(identifier);
        if(existingPrice == null)
        {
            priceDto.setMessage("Price for product - "+identifier+" not defined");
            priceDto.setSuccess(false);
            return priceDto;
        }
        existingPrice.setDifference(priceDto.getCostPrice().subtract(priceDto.getSellingPrice()));
        modelMapper.map(priceDto, existingPrice);
        priceRepository.save(existingPrice);
        return priceDto;
    }

    @Override
    public void delete(String identifier) {
        Price price = priceRepository.findByIdentifierAndDeletedFalse(identifier);
        if(price != null)
        {
            price.setDeleted(true);
            priceRepository.save(price);
        }
    }

    @Override
    public List<PriceDto> findAll() {
        Type listType = new TypeToken<List<PriceDto>>(){}.getType();
        return modelMapper.map(priceRepository.findByDeletedFalse(), listType);
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable)
    {
        Type listtype = new TypeToken<List<PriceDto>>(){}.getType();
        Page<Price> pricePage = priceRepository.findByDeletedFalse(pageable);

        WsDto<PriceDto> priceDtoWsDto = new WsDto<>();
        priceDtoWsDto.setDtoList(modelMapper.map(pricePage.getContent(), listtype));
        priceDtoWsDto.setTotalRecords(pricePage.getTotalElements());
        priceDtoWsDto.setTotalPage(pricePage.getTotalPages());
        priceDtoWsDto.setSizePerPage(pageable.getPageSize());
        priceDtoWsDto.setPage(pageable.getPageNumber());

        return priceDtoWsDto;
    }

    @Override
    public Page<PriceDto> findAll(Example<Price> example, Pageable pageable) {
        Page<Price> pricePage = priceRepository.findAll(example, pageable);
        return pricePage.map(price -> modelMapper.map(price, PriceDto.class));
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        return modelMapper.map(priceRepository.findByIdentifierAndDeletedFalse(identifier), PriceDto.class);
    }
}
