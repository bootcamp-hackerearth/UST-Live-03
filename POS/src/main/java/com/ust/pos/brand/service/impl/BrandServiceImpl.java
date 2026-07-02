package com.ust.pos.brand.service.impl;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class BrandServiceImpl extends CommonService implements BrandService {
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
                brandRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (existingBrand != null) {
            brandDto.setMessage("Brand with identifier - " + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        setAuditFields(brand, true);
        brandRepository.save(brand);
        return brandDto;
    }

    @Override
    public BrandDto update(BrandDto brandDto) {

        Brand existingBrand =
                brandRepository.findByIdentifierAndIsDeleteFalse(brandDto.getIdentifier());

        if (existingBrand == null) {
            brandDto.setSuccess(false);
            brandDto.setMessage("Brand not found");
            return brandDto;
        }

        existingBrand.setDescription(brandDto.getDescription());
        existingBrand.setStatus(brandDto.getStatus());
        setAuditFields(existingBrand, false);
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Override
    public void delete(String identifier) {
        Brand brand = brandRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (brand != null) {
            brand.setDelete(true);
            setAuditFields(brand, false);
            brandRepository.save(brand);
        }
    }

    @Override
    public List<BrandDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findByIsDeleteFalse(pageable);
        return modelMapper.map(brandPage.getContent(), listOfType);
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.
                findByIdentifierAndIsDeleteFalse(identifier), BrandDto.class);
    }

    @Override
    public void updateStatusOnly(String identifier, boolean status) {
        Brand brand = brandRepository.
                findByIdentifierAndIsDeleteFalse(identifier);
        brand.setStatus(status);
        setAuditFields(brand, false);
        brandRepository.save(brand);
    }

    @Override
    public Page<BrandDto> findAll(Pageable pageable, String search) {
        Page<Brand> brands;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Brand> specification = buildGlobalSearchSpec(Brand.class, search);
            brands = brandRepository.findAll(specification, pageable);
        } else {
            brands = brandRepository.findByIsDeleteFalse(pageable);
        }

        return brands.map(brand -> modelMapper.map(brand, BrandDto.class));
    }

    @Override
    public List<BrandDto> findAll() {
        Type listOfType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(brandRepository.findByIsDeleteFalse(), listOfType);
    }
}
