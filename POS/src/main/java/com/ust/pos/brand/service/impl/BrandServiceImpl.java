package com.ust.pos.brand.service.impl;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import com.ust.pos.service.BaseService;
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
@Transactional
public class BrandServiceImpl extends BaseService implements BrandService {
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    public BrandServiceImpl(BrandRepository brandRepository, ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifierAndDeletedFalse(identifier), BrandDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Brand brands = brandRepository.findByIdentifierAndDeletedFalse(identifier);
        if (brands != null) {
            brands.setStatus(!brands.isStatus());
            brandRepository.save(brands);
        }
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingBrand != null) {
            brandDto.setMessage("Brand with identifier - " + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }

        Brand brand = modelMapper.map(brandDto, Brand.class);
        brandRepository.save(brand);
        return brandDto;
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingBrand == null) {
            brandDto.setMessage("Brand with identifier - " + identifier + " not found");
            brandDto.setSuccess(false);
            return brandDto;
        }

        modelMapper.map(brandDto, existingBrand);
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Override
    public void delete(String identifier) {
        Brand brand = brandRepository.findByIdentifierAndDeletedFalse(identifier);

        if (brand != null) {
            brand.setDeleted(true);
            brandRepository.save(brand);
        }
    }

    @Override
    public List<BrandDto> findAll() {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(brandRepository.findByDeletedFalse(), listType);
    }

    @Override
    public Page<BrandDto> findAll(Pageable pageable, String search) {
        Page<Brand> brands;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Brand> specification = buildGlobalSearchSpec(Brand.class, search);
            brands = brandRepository.findAll(specification, pageable);
        } else {
            brands = brandRepository.findByDeletedFalse(pageable);
        }

        return brands.map(brand -> modelMapper.map(brand, BrandDto.class));
    }
}