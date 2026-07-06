package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BrandService {

    BrandDto save(BrandDto brandDto);
    BrandDto update(BrandDto brandDto);
    boolean delete(String identifier);
    WsDto<BrandDto> findAll(Pageable pageable);
    BrandDto findByIdentifier(String identifier);
    BrandDto toggleStatus(String identifier);
    List<BrandDto> findIfTrue();
    WsDto<BrandDto> findAll(Specification<Brand> example, Pageable pageable, String keyword);

}