package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface BrandService {

    BrandDto save(BrandDto brandDto);

    WsDto<BrandDto> findAll(Pageable pageable);

    BrandDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    BrandDto update(BrandDto brandDto);

    BrandDto toggleStatus(String identifier, boolean status);

    WsDto<BrandDto> findAll(Specification<Brand> specification, Pageable pageable);

}