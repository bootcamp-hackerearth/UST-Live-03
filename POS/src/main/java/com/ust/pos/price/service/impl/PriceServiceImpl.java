package com.ust.pos.price.service.impl;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class PriceServiceImpl implements PriceService {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PriceDto findByIdentifier(String identifier) {
        return modelMapper.map(priceRepository.findByIdentifier(identifier), PriceDto.class);
    }

    @Override
    public PriceDto save(PriceDto dto) {

        Price existingByProduct = priceRepository.findByProductIdentifier(dto.getProductIdentifier());

        if (existingByProduct != null) {
            dto.setSuccess(false);
            dto.setMessage("ProductIdentifier already exists: " + dto.getProductIdentifier());
            return dto;
        }

        Price existing = priceRepository.findByIdentifier(

                dto.getIdentifier());

        if (existing != null) {
            dto.setSuccess(false);
            dto.setMessage("Price identifier already exists  " + dto.getIdentifier());
            return dto;
        }

        Price price = modelMapper.map(dto, Price.class);
        priceRepository.save(price);

        dto.setSuccess(true);
        dto.setMessage("Price saved successfully");
        return dto;
    }


    @Override
    public PriceDto update(PriceDto dto) {

        Price existing = priceRepository.findByIdentifier(dto.getIdentifier());
        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage("Price not found : " + dto.getIdentifier());
            return dto;
        }
        if (dto.getProductIdentifier() != null &&
                !dto.getProductIdentifier().equalsIgnoreCase(existing.getProductIdentifier())) {

            Price conflicting = priceRepository.findByProductIdentifier(dto.getProductIdentifier());
            if (conflicting != null) {
                dto.setSuccess(false);
                dto.setMessage("ProductIdentifier already exists: " + dto.getProductIdentifier());
                return dto;
            }
        }

        modelMapper.map(dto, existing);
        priceRepository.save(existing);
        return dto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        priceRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> pricePage = priceRepository.findAll(pageable);

        WsDto<PriceDto> priceWsDto = new WsDto<>();
        priceWsDto.setDtoList(modelMapper.map(pricePage.getContent(), listType));
        priceWsDto.setTotalRecords(pricePage.getTotalElements());
        priceWsDto.setTotalPages(pricePage.getTotalPages());
        priceWsDto.setSizePerPage(pageable.getPageSize());
        priceWsDto.setPage(pageable.getPageNumber());

        return priceWsDto;
    }

    @Override
    public PriceDto toggleStatus(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        price.setStatus(!price.isStatus());
        priceRepository.save(price);
        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public List<PriceDto> findIfTrue() {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        return modelMapper.map(priceRepository.findByStatusIsTrue(), listType);
    }

    @Override
    public PriceDto findByProductIdentifier(String productIdentifier) {
        return modelMapper.map(priceRepository.findByProductIdentifier(productIdentifier), PriceDto.class);
    }


}