package com.ust.pos.price.service.impl;

import com.ust.pos.CommonService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.PriceService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
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

    private static final String PRICE_WITH_IDENTIFIER = "Price with identifier - ";

    private final PriceRepository priceRepository;
    private final ModelMapper modelMapper;

    public PriceServiceImpl(PriceRepository priceRepository,
                            ModelMapper modelMapper) {
        this.priceRepository = priceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PriceDto findByIdentifier(String identifier) {
        Price price =priceRepository.findByIdentifier(identifier);
        if(price==null){
            throw new ResourceNotFoundException("Data Cannot Found");
        }
        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public PriceDto findByProductIdentifier(String productIdentifier) {
        return modelMapper.map(
                priceRepository.findByProductIdentifier(productIdentifier),
                PriceDto.class
        );
    }

    @Override
    public PriceDto save(PriceDto dto) {

        if (dto == null || dto.getIdentifier() == null) {
            throw new IllegalArgumentException("Identifier is required");
        }

        Price existingByProduct = priceRepository.findByProductIdentifier(dto.getProductIdentifier());

        if (existingByProduct != null && !existingByProduct.getDeleted()) {
            dto.setSuccess(false);
            dto.setMessage("ProductIdentifier already exists: " + dto.getProductIdentifier());
            return dto;
        }

        Price existing = priceRepository.findByIdentifier(dto.getIdentifier());

        if (existing != null) {
            if (BooleanUtils.isNotTrue(existing.getDeleted())) {
                dto.setSuccess(false);
                dto.setMessage(PRICE_WITH_IDENTIFIER + dto.getIdentifier() + " already exists");
                return dto;
            }

            dto.setSuccess(false);
            dto.setMessage(PRICE_WITH_IDENTIFIER + dto.getIdentifier() +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        Price price = modelMapper.map(dto, Price.class);
        setAuditFields(price, true);

        Price saved = priceRepository.save(price);

        PriceDto response = modelMapper.map(saved, PriceDto.class);
        response.setSuccess(true);
        response.setMessage("Price created successfully");

        return response;
    }

    @Override
    public PriceDto update(PriceDto dto) {

        String identifier = dto.getIdentifier();
        Price existing = priceRepository.findByIdentifier(identifier);

        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage(PRICE_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        if (BooleanUtils.isTrue(existing.getDeleted())) {
            dto.setSuccess(false);
            dto.setMessage(PRICE_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        if (dto.getProductIdentifier() != null &&
                !dto.getProductIdentifier().equalsIgnoreCase(existing.getProductIdentifier())) {

            Price conflict = priceRepository.findByProductIdentifier(dto.getProductIdentifier());

            if (conflict != null && !conflict.getDeleted()) {
                dto.setSuccess(false);
                dto.setMessage("ProductIdentifier already exists: " + dto.getProductIdentifier());
                return dto;
            }
        }

        modelMapper.map(dto, existing);
        setAuditFields(existing, false);

        priceRepository.save(existing);

        dto.setSuccess(true);
        dto.setMessage("Price updated successfully");

        return dto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        Price price = priceRepository.findByIdentifier(identifier);

        if (price != null) {
            softDelete(price);
            setAuditFields(price, false);
            priceRepository.save(price);
        }
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> page = priceRepository.findByDeletedFalse(pageable);

        WsDto<PriceDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
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

    @Override
    public PriceDto toggleStatus(String identifier) {

        Price price = priceRepository.findByIdentifier(identifier);

        if (price == null) {
            PriceDto dto = new PriceDto();
            dto.setSuccess(false);
            dto.setMessage(PRICE_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        price.setStatus(!price.getStatus());
        setAuditFields(price, false);

        priceRepository.save(price);

        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public List<PriceDto> findIfTrue() {

        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();

        return modelMapper.map(
                priceRepository.findByStatusIsTrueAndDeletedFalse(),
                listType
        );
    }
}