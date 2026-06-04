package com.ust.pos.brand.service.impl;


import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
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
    BrandRepository brandRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand != null) {
            brandDto.setMessage("Brand with identifier" + identifier + "already Exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand1 = modelMapper.map(brandDto, Brand.class);
        brandRepository.save(brand1);
        return brandDto;
    }

    @Override
    public void delete(String identifier) {
        brandRepository.deleteByIdentifier(identifier);
    }

    @Override
    public BrandDto update(BrandDto brandDto) {

        Brand existingBrand =
                brandRepository.findByIdentifier(brandDto.getIdentifier());

        if (existingBrand == null) {
            brandDto.setSuccess(false);
            brandDto.setMessage("Brand not found");
            return brandDto;
        }

        // ✅ update existing row
        existingBrand.setDescription(brandDto.getDescription());
        existingBrand.setStatus(brandDto.isStatus());

        brandRepository.save(existingBrand); // ✅ UPDATE

        return brandDto;
    }

    @Override
    public List<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findAll(pageable);
        return modelMapper.map(brandPage.getContent(), listType);
    }

    @Override
    public List<BrandDto> findAll() {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(brandRepository.findAll(), listType);
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Brand racks = brandRepository.findByIdentifier(identifier);
        if (racks != null) {
            // ✅ toggle status
            racks.setStatus(!racks.isStatus());
            brandRepository.save(racks);
        }

    }
}