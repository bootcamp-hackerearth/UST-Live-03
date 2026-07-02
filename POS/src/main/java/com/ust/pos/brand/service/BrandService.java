package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    BrandDto save(BrandDto brandDto);

    BrandDto update(BrandDto brandDto);

    void delete(String identifier);

    Page<BrandDto> findAll(String search,Pageable pageable);

    List<BrandDto> findAll();


    BrandDto findByIdentifier(String identifier);

    void updateStatusOnly(String identifier, boolean status);
}
