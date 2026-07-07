package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface BrandService {

    BrandDto findByIdentifier(String identifier);

    BrandDto save(BrandDto dto);

    BrandDto update(BrandDto dto);

    void delete(String identifier);

    WsDto<BrandDto> findAll(Pageable pageable);

    WsDto<BrandDto> findAll(Specification<Brand> example, Pageable pageable);

    List<BrandDto> findIfTrue();

    BrandDto toggleStatus(String identifier);
}

