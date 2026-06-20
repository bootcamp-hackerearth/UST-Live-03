package com.ust.pos.price.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.price.service.PriceService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PriceServiceImpl extends CommonService implements PriceService {
    private final PriceRepository priceRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public PriceServiceImpl(PriceRepository priceRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.priceRepository = priceRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PriceDto createPrice(PriceDto priceDto) {

        var product = productRepository.findById(priceDto.getProductId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (priceRepository.existsByProductId(priceDto.getProductId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Price already exists for this product");
        }

        priceDto.setProductName(product.getProductName());
        priceDto.setIdentifier(product.getIdentifier());

        Price price = modelMapper.map(priceDto, Price.class);
        setAuditFields(price, true);
        priceRepository.save(price);

        return priceDto;
    }

    @Override
    public PriceDto updatePrice(PriceDto priceDto) {

        Price price = priceRepository.findById(priceDto.getId()).orElseThrow(() -> new RuntimeException("Price record not found"));

        price.setSellingPrice(priceDto.getSellingPrice());
        price.setCostPrice(priceDto.getCostPrice());

        productRepository.findById(price.getProductId()).ifPresent(product -> {
            price.setProductName(product.getProductName());
            price.setIdentifier(product.getIdentifier());
        });

        setAuditFields(price, false);
        priceRepository.save(price);

        modelMapper.map(price, priceDto);

        return priceDto;
    }

    @Override
    public WsDto<PriceDto> findAll(Pageable pageable) {
        Page<Price> pricePage = priceRepository.findAll(pageable);
        List<PriceDto> dtoList = pricePage.getContent().stream().map(price -> {
            PriceDto dto = modelMapper.map(price, PriceDto.class);
            productRepository.findById(price.getProductId()).ifPresent(product -> {
                dto.setProductName(product.getProductName());
                dto.setIdentifier(product.getIdentifier());
            });
            return dto;
        }).toList();
        WsDto<PriceDto> wsDto = new WsDto<>();
        wsDto.setDtoList(dtoList);
        wsDto.setTotalRecords(pricePage.getTotalElements());
        wsDto.setTotalPages(pricePage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public boolean deletePrice(Long id) {

        if (!priceRepository.existsById(id)) {
            return false;
        }

        priceRepository.deleteById(id);

        return true;
    }

    @Override
    public PriceDto getPriceById(Long id) {

        PriceDto dto = new PriceDto();

        priceRepository.findById(id).ifPresentOrElse(price -> {

            modelMapper.map(price, dto);

            productRepository.findById(price.getProductId()).ifPresent(product -> {
                dto.setProductName(product.getProductName());
                dto.setIdentifier(product.getIdentifier());
            });

            dto.setSuccess(true);

        }, () -> {
            dto.setSuccess(false);
            dto.setMessage("Price not found");
        });

        return dto;
    }
}