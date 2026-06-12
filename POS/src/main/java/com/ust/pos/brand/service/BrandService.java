package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginatedResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {

    BrandDto save(BrandDto brandDto);

    BrandDto update(BrandDto brandDto);

    void delete(String identifier);

    PaginatedResponseDto<BrandDto> findAll(Pageable pageable);

    BrandDto findByIdentifier(String identifier);

    List<BrandDto> findAllActive();

    void changeStatus(String identifier, boolean status);
}
