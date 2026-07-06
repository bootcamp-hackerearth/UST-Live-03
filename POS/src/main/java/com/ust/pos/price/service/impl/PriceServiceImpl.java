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
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice != null) {
            if (existingPrice.isDeleted()) {
                priceDto.setMessage("Price with identifier " + identifier + " was previously deleted. " +
                        "Please contact backend team to restore."
                );
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
    public PriceDto update(PriceDto priceDto) {
        String identifier = priceDto.getIdentifier();
        Price existingPrice = priceRepository.findByIdentifier(identifier);
        if (existingPrice == null) {
            priceDto.setMessage("Price with identifier - " + identifier + " not found");
            priceDto.setSuccess(false);
            return priceDto;
        }
        modelMapper.map(priceDto, existingPrice);
        setAuditFields(existingPrice, false);
        priceRepository.save(existingPrice);
        return priceDto;
    }

    @Override
    public boolean delete(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        if (price == null) return false;
        softDelete(price);
        setAuditFields(price, false);
        priceRepository.save(price);
        return true;
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
    public List<PriceDto> findIfTrue() {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        return modelMapper.map(priceRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }

    @Override
    public PriceDto toggleStatus(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        price.setStatus(!price.isStatus());
        setAuditFields(price, false);
        priceRepository.save(price);
        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public PriceDto findByIdentifierAndDeletedFalse(String identifier) {
        Price price = priceRepository.findByIdentifierAndDeletedFalse(identifier);
        if (price == null) {
            return null;
        }
        return modelMapper.map(price, PriceDto.class);
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