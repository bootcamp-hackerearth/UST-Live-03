package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {

    BrandDto save(BrandDto brandDto);

    BrandDto findById(Long id);

    WsDto<BrandDto> findAll(Pageable pageable);

    BrandDto update(BrandDto brandDto);

    void deleteById(Long id);

    BrandDto findByIdentifier(String identifier);

    BrandDto changeBrandStatus(String identifier, boolean status);

    List<BrandDto> findActiveBrand();
}
