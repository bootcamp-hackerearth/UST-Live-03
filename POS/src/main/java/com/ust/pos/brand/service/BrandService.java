package com.ust.pos.brand.service;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Transactional
public interface BrandService {
    BrandDto save(BrandDto brandDto);

    BrandDto update(BrandDto brandDto);

    boolean delete(String identifier);

    PageDto<BrandDto> findAll(Pageable pageable);

    BrandDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<BrandDto> findActiveBrands();

    PageDto<BrandDto> findAll(Specification<Brand> spec, Pageable pageable, String keyword);
}
