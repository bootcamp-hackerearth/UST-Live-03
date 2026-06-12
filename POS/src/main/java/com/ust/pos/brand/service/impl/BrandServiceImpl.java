package com.ust.pos.brand.service.impl;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
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
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findAll(pageable);

        WsDto<BrandDto> brandWsDto = new WsDto<>();
        brandWsDto.setDtoList(modelMapper.map(brandPage.getContent(), listType));
        brandWsDto.setTotalRecords(brandPage.getTotalElements());
        brandWsDto.setTotalPages(brandPage.getTotalPages());
        brandWsDto.setSizePerPage(pageable.getPageSize());
        brandWsDto.setPage(pageable.getPageNumber());

        return brandWsDto;
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        if (brandDto.getBrandName() == null || brandDto.getBrandName().trim().isEmpty()) {
            brandDto.setSuccess(false);
            brandDto.setMessage("Brand name is required");
            return brandDto;
        }
        if (brandRepository.findByIdentifier(brandDto.getBrandName()) != null) {
            brandDto.setSuccess(false);
            brandDto.setMessage("Brand already exists");
            return brandDto;
        }
        Brand brand = new Brand();
        brand.setIdentifier(brandDto.getBrandName());
        brand.setBrandName(brandDto.getBrandName());
        brand.setDescription(brandDto.getDescription());
        brand.setStatus(brandDto.getStatus());
        Brand saved = brandRepository.save(brand);
        BrandDto dto = modelMapper.map(saved, BrandDto.class);
        dto.setSuccess(true);
        dto.setMessage("Brand added successfully");
        return dto;
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        Brand brand = brandRepository.findByIdentifier(brandDto.getIdentifier());
        if (brand == null) {
            brandDto.setSuccess(false);
            brandDto.setMessage("Brand not found");
            return brandDto;
        }
        modelMapper.map(brandDto, brand);
        brand.setIdentifier(brand.getIdentifier());
        brand.setBrandName(brand.getBrandName());
        Brand updated = brandRepository.save(brand);
        BrandDto dto = modelMapper.map(updated, BrandDto.class);
        dto.setSuccess(true);
        dto.setMessage("Brand updated successfully");
        return dto;
    }

    @Override
    public void delete(String identifier) {
        brandRepository.deleteByIdentifier(identifier);
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand == null) {
            return null;
        }
        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public List<BrandDto> findActiveBrands() {
        List<Brand> brands = brandRepository.findAll();
        List<BrandDto> result = new ArrayList<>();
        for (Brand b : brands) {
            if (Boolean.TRUE.equals(b.getStatus())) {
                result.add(modelMapper.map(b, BrandDto.class));
            }
        }
        return result;
    }

    @Override
    public BrandDto toggleStatus(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        BrandDto dto = new BrandDto();
        if (brand == null) {
            dto.setSuccess(false);
            dto.setMessage("Brand not found");
            return dto;
        }
        brand.setStatus(!Boolean.TRUE.equals(brand.getStatus()));
        brandRepository.save(brand);
        dto.setIdentifier(brand.getIdentifier());
        dto.setBrandName(brand.getBrandName());
        dto.setDescription(brand.getDescription());
        dto.setStatus(brand.getStatus());
        dto.setSuccess(true);
        dto.setMessage("Status updated successfully");
        return dto;
    }
}