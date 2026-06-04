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
        Brand existingBrand = brandRepository.findByIdentifier(brandDto.getIdentifier());
        if (existingBrand != null) {
            brandDto.setMessage("Brand with identifier - " + brandDto.getIdentifier() + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        brandRepository.save(brand);
        return brandDto;
    }

    @Override
    public List<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        if (pageable == null) {
            return modelMapper.map(brandRepository.findAll(), listType);
        }
        Page<Brand> brandPage = brandRepository.findAll(pageable);
        return modelMapper.map(brandPage.getContent(), listType);
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
    }

    @Transactional
    @Override
    public void deleteByIdentifier(String identifier) {
        brandRepository.deleteByIdentifier(identifier);
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        Brand existingBrand = brandRepository.findByIdentifier(brandDto.getIdentifier());
        if (existingBrand == null) {
            brandDto.setMessage("Brand with identifier - " + brandDto.getIdentifier() + "not found");
            brandDto.setSuccess(false);
            return brandDto;
        }
        modelMapper.map(brandDto, existingBrand);
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Override
    @Transactional
    public BrandDto toggleStatus(String identifier, boolean status) {
        BrandDto response = new BrandDto();
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand == null) {
            response.setSuccess(false);
            response.setMessage("Brand not found");
            return response;
        }
        brand.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

    public List<BrandDto> findActiveBrands() {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(
                brandRepository.findByStatusTrue(),
                listType
        );
    }

}