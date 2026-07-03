package com.ust.pos.price.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class PriceServiceImpl extends CommonService implements PriceService {

    private final ModelMapper modelMapper;

    private final PriceRepository priceRepository;

    public PriceServiceImpl(ModelMapper modelMapper, PriceRepository priceRepository) {
        this.modelMapper = modelMapper;
        this.priceRepository = priceRepository;
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice != null) {
            if (existingPrice.isDeleted()) {
                priceDto.setMessage("Price with identifier - " + identifier + "has been soft deleted.(Rollback by changing status");
                priceDto.setSuccess(false);
                return priceDto;
            }
            priceDto.setMessage("Price with identifier - " + identifier + " already exists");
            priceDto.setSuccess(false);
            return priceDto;
        }
        Price price = modelMapper.map(priceDto, Price.class);
        setAuditFields(price, true);
        priceRepository.save(price);
        return priceDto;
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> pricePage = priceRepository.findByDeletedFalse(pageable);
        WsDto<PriceDto> priceWsDto = new WsDto<>();
        priceWsDto.setDtoList(modelMapper.map(pricePage.getContent(), listType));
        priceWsDto.setTotalRecords(pricePage.getTotalElements());
        priceWsDto.setTotalPages(pricePage.getTotalPages());
        priceWsDto.setSizePerPage(pageable.getPageSize());
        priceWsDto.setPage(pageable.getPageNumber());

        return priceWsDto;
    }

    @Override
    public WsDto<PriceDto> findAll(Specification<Price> spec, Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {}.getType();
        Page<Price> page = priceRepository.findAll(spec, pageable);
        WsDto<PriceDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public PriceDto findById(Long id) {

        return priceRepository.findById(id)
                .map(p -> modelMapper.map(p, PriceDto.class))
                .orElseThrow(() -> new RuntimeException("Price not found"));

    }

    @Override
    public void delete(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        softDelete(price);
        setAuditFields(price, false);
        priceRepository.save(price);
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        Price price = priceRepository.findByIdentifier(priceDto.getIdentifier());

        if (price == null) {
            priceDto.setSuccess(false);
            priceDto.setMessage("Price not found!");
            return priceDto;
        }

        if (!priceDto.getIdentifier().equalsIgnoreCase(price.getIdentifier()) && priceRepository.existsByIdentifier(priceDto.getIdentifier())) {
            priceDto.setMessage("This price already exist!");
            priceDto.setSuccess(false);
            return priceDto;
        }
        modelMapper.map(priceDto, price);
        setAuditFields(price, false);
        priceRepository.save(price);
        priceDto.setMessage("Product Updated Successfully");
        return priceDto;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {

        Price price = priceRepository.findByIdentifier(identifier);
        return modelMapper.map(price, PriceDto.class);

    }

    @Override
    public PriceDto changePriceStatus(String identifier, boolean status) {
        Price price = priceRepository.findByIdentifier(identifier);
        if (price == null) {
            return null; // test expects null
        }
        price.setStatus(status);
        priceRepository.save(price);
        return modelMapper.map(price, PriceDto.class);
    }
}