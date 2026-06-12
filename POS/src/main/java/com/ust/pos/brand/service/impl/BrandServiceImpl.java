package com.ust.pos.brand.service.impl;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import com.ust.pos.brand.service.BrandService;
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
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Brand brands = brandRepository.findByIdentifier(identifier);
        if (brands != null) {
            brands.setStatus(!brands.isStatus());
            brandRepository.save(brands);
        }
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

        Brand brand = modelMapper.map(brandDto, Brand.class);
        brandRepository.save(brand);
        return brandDto;
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);

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
        brandRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<BrandDto> findAll() {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(brandRepository.findAll(), listType);
    }

    @Override
    public List<BrandDto> findAll(Pageable pageable) {
        Type listtype = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findAll(pageable);
        return modelMapper.map(brandPage.getContent(), listtype);
    }
}