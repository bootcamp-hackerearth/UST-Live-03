package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface BrandService {

    BrandDto save(BrandDto brandDto);

    BrandDto findById(Long id);

    WsDto<BrandDto> findAll(Pageable pageable);

    BrandDto update(BrandDto brandDto);

    void delete(String identifier);

    BrandDto findByIdentifier(String identifier);

    BrandDto changeBrandStatus(String identifier, boolean status);

    List<BrandDto> findActiveBrand();

    WsDto<BrandDto> findAll(Specification<Brand> example, Pageable pageable);

}