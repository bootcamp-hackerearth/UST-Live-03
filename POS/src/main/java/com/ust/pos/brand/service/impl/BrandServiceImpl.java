package com.ust.pos.brand.service.impl;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import com.ust.pos.util.FileStorageUtil;
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

    public static final String BRAND_WITH_IDENTIFIER = "Brand with identifier - ";
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);

        if (existingBrand != null) {
            brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " Already Exists");
            brandDto.setSuccess(false);
            return brandDto;
        }

        Brand brand = modelMapper.map(brandDto, Brand.class);
        String iconPath = fileStorageUtil.saveBrandIcon(brandDto.getIcon(), identifier);
        brand.setIconPath(iconPath);
        brandRepository.save(brand);

        brandDto = modelMapper.map(brand, BrandDto.class);
        brandDto.setSuccess(true);
        brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " Added Successfully");
        return brandDto;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
    }

    @Override
    public List<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findAll(pageable);
        return modelMapper.map(brandPage.getContent(), listType);
    }

    @Override
    public List<BrandDto> findAllActive() {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(brandRepository.findAllByStatus(true), listType);
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        Brand brand = brandRepository.findByIdentifier(brandDto.getIdentifier());
        if (brand == null) {
            brandDto.setSuccess(false);
            brandDto.setMessage("Brand not found");
            return brandDto;
        }
        brand.setDescription(brandDto.getDescription());
        if (brandDto.getIcon() != null && !brandDto.getIcon().isEmpty()) {
            String iconPath = fileStorageUtil.saveBrandIcon(brandDto.getIcon(), brand.getIdentifier());
            brand.setIconPath(iconPath);
        }
        brandRepository.save(brand);
        brandDto = modelMapper.map(brand, BrandDto.class);
        brandDto.setMessage(BRAND_WITH_IDENTIFIER + brandDto.getIdentifier() + " Updated");
        return brandDto;
    }

    @Override
    public BrandDto toggleStatus(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        brand.setStatus(!brand.isStatus());
        brandRepository.save(brand);
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
    }

    @Override
    public boolean delete(String identifier) {
        brandRepository.deleteByIdentifier(identifier);
        return true;
    }
}
