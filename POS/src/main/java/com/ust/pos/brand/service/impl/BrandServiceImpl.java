package com.ust.pos.brand.service.impl;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.BrandRepository;
import com.ust.pos.model.Brand;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    BrandRepository brandRepository;

    @Override
    public List<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage=brandRepository.findAll(pageable);
        return modelMapper.map(brandPage.getContent(), listType);
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand != null) {
            brandDto.setMessage("Brand with identifier - " + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand price = modelMapper.map(brandDto, Brand.class);
        brandRepository.save(price);
        return brandDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        brandRepository.deleteByIdentifier(identifier);
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand == null) {
            brandDto.setMessage("Brand with identifier - " + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        modelMapper.map(brandDto, existingBrand);
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Override
    public BrandDto changeToggleStatus(String identifier, boolean status) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand != null) {
            brand.setStatus(status);
            brandRepository.save(brand);
        }
        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public List<BrandDto> findActiveStatus() {
        List<Brand> allBrands = brandRepository.findAll();
        List<Brand> activeBrands = allBrands.stream().filter(Brand::isStatus).toList();
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(activeBrands, listType);
    }
}
