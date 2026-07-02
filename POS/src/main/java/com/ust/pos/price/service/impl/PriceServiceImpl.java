package com.ust.pos.price.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.model.Product;
import com.ust.pos.price.service.PriceService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
@Service
public class PriceServiceImpl extends CommonService implements PriceService{
    public static final String PRODUCT_WITH_IDENTIFIER = "Product with identifier - ";
    private final PriceRepository priceRepository;

    private final ModelMapper modelMapper;

    public PriceServiceImpl(PriceRepository priceRepository, ModelMapper modelMapper) {
        this.priceRepository = priceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PriceDto save(PriceDto priceDto) {
        String identifier =priceDto.getIdentifier();
        Price existingProduct = priceRepository.findByIdentifier(identifier);
            if (existingProduct != null) {
                if (Boolean.TRUE.equals(existingProduct.getDeleted())) {
                    priceDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " has been soft deleted. Restore it by changing status.");
                    priceDto.setSuccess(false);
                    return priceDto;
                }
           priceDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " already exists");
           priceDto.setSuccess(false);
            return priceDto;
        }
        Price price = modelMapper.map(priceDto, Price.class);
            price.setDeleted(false);
            price.setStatus(true);
            setAuditFields(price, true);
        priceRepository.save(price);
        return priceDto;
    }

    @Override
    public PriceDto update(PriceDto priceDto) {
        String identifier =priceDto.getIdentifier();
        Price existingProduct = priceRepository.findByIdentifier(identifier);
        if (existingProduct == null) {
           priceDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " not found");
           priceDto.setSuccess(false);
            return priceDto;
        }
        modelMapper.map(priceDto, existingProduct);
        setAuditFields(existingProduct,false);
        priceRepository.save(existingProduct);
        return priceDto;
    }

    @Override
    @Transactional
    public boolean delete(String identifier) {

        Price price = priceRepository.findByIdentifier(identifier);

        if (price == null) {
            return false;
        }
        softDelete(price);
        setAuditFields(price, false);
        priceRepository.save(price);
        return true;
    }

    @Override
    public PageDto<PriceDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<PriceDto>>() {
        }.getType();
        Page<Price> pricePage = priceRepository.findByDeletedFalse(pageable);
        PageDto<PriceDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(pricePage.getContent(), listType));
        pageDto.setTotalRecords(pricePage.getTotalElements());
        pageDto.setTotalPages(pricePage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public PageDto<PriceDto> findAll(Specification<Price> spec, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Price> productPage = priceRepository.findAll(spec, pageable);
        PageDto<PriceDto> PageDto = new PageDto<>();
        PageDto.setDtoList(modelMapper.map(productPage.getContent(), listType));
        PageDto.setTotalRecords(productPage.getTotalElements());
        PageDto.setTotalPages(productPage.getTotalPages());
        PageDto.setSizePerPage(pageable.getPageSize());
        PageDto.setPage(pageable.getPageNumber());
        PageDto.setKeyword(keyword);
        return PageDto;
    }


    @Override
    public PriceDto findByIdentifier(String identifier) {

        Price price = priceRepository.findByIdentifier(identifier);

        PriceDto priceDto = new PriceDto();

        if (price == null) {
            priceDto.setSuccess(false);
            priceDto.setMessage("Price not found");
            return priceDto;
        }
        return modelMapper.map(price, PriceDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Price price = priceRepository.findByIdentifier(identifier);
        if (price != null) {
            boolean currentStatus = Boolean.TRUE.equals(price.getStatus());
            price.setStatus(!currentStatus);
            priceRepository.save(price);
        }
    }

    @Override
    public List<PriceDto> findActivePrices() {
        Type listType = new TypeToken<List<PriceDto>>() {}.getType();
        return modelMapper.map(priceRepository.findByStatusTrue(),listType);
    }
}
