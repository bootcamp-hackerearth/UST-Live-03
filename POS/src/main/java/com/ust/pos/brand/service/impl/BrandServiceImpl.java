package com.ust.pos.brand.service.impl;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    BrandServiceImpl(BrandRepository brandRepository, ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand =
                brandRepository.findByIdentifierAndDeletedFalse(identifier);
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
        Brand existingBrand =
                brandRepository.findByIdentifierAndDeletedFalse(brandDto.getIdentifier());
        if (existingBrand == null) {
            brandDto.setSuccess(false);
            brandDto.setMessage("Brand not found");
            return brandDto;
        }
        existingBrand.setDescription(brandDto.getDescription());
        existingBrand.setStatus(brandDto.isStatus());
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
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.
                findByIdentifierAndDeletedFalse(identifier), BrandDto.class);
    }

    @Override
    public BrandDto toggleStatus(String identifier) {
        Brand brand = brandRepository.findByIdentifierAndDeletedFalse(identifier);
        if (brand != null) {
            brand.setStatus(!brand.isStatus());
            brandRepository.save(brand);
        }
        return null;
    }

    @Override
    public Page<BrandDto> findAll(Pageable pageable, String search) {
        Page<Brand> brandPage;
        if (search != null && !search.trim().isEmpty()) {
            brandPage = brandRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse
                    (search, pageable);
        } else {
            brandPage = brandRepository.findByDeletedFalse(pageable);
        }
        return brandPage.map(brand -> modelMapper.map(brand, BrandDto.class));
    }

    @Override
    public List<BrandDto> findAll() {
        Type listOfType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(brandRepository.findByDeletedFalse(), listOfType);
    }
}
