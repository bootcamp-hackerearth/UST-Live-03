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
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(
                brandRepository.findByIdentifier(identifier),
                BrandDto.class
        );
    }

    @Override
    public BrandDto save(BrandDto brandDto) {

        Brand existing = brandRepository.findByIdentifier(brandDto.getIdentifier());
        if (existing != null) {
            brandDto.setSuccess(false);
            brandDto.setMessage("Brand already exists : " + brandDto.getIdentifier());
            return brandDto;
        }

        Brand brand = modelMapper.map(brandDto, Brand.class);
        brandRepository.save(brand);
        return brandDto;
    }

    @Override
    public BrandDto update(BrandDto brandDto) {

        Brand existing = brandRepository.findByIdentifier(brandDto.getIdentifier());
        if (existing == null) {
            brandDto.setSuccess(false);
            brandDto.setMessage("Brand not found : " + brandDto.getIdentifier());
            return brandDto;
        }

        modelMapper.map(brandDto, existing);
        brandRepository.save(existing);
        return brandDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        brandRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> customerPage = brandRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }

    @Override
    public List<BrandDto> findIfTrue() {
        return brandRepository.findByStatusTrue()
                .stream()
                .map(brand -> modelMapper.map(brand, BrandDto.class))
                .toList();
    }

    @Override
    public BrandDto toggleStatus(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        brand.setStatus(!brand.isStatus());
        brandRepository.save(brand);
        return modelMapper.map(brand, BrandDto.class);
    }

}