package com.ust.pos.brand.service.impl;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.common.CommonService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class BrandServiceImpl extends CommonService implements BrandService {

    public static final String BRAND_WITH_IDENTIFIER = "Brand with identifier - ";
    private final BrandRepository brandRepository;

    private final ModelMapper modelMapper;

    public BrandServiceImpl(BrandRepository brandRepository, ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand != null) {
            if (Boolean.TRUE.equals(existingBrand.getDeleted())) {
                brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " has been soft deleted. Restore it by changing status.");
                brandDto.setSuccess(false);
                return brandDto;
            }
            brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        brand.setDeleted(false);
        brand.setStatus(true);
        setAuditFields(brand,true);
        brandRepository.save(brand);
        return brandDto;
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand == null) {
            brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " not found");
            brandDto.setSuccess(false);
            return brandDto;
        }
        modelMapper.map(brandDto, existingBrand);
        setAuditFields(existingBrand,false);
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Override
    public boolean delete(String identifier) {

        Brand brand = brandRepository.findByIdentifier(identifier);

        if (brand == null) {
            return false;
        }
        softDelete(brand);
        setAuditFields(brand,false);
        brandRepository.save(brand);
        return true;
    }

    @Override
    public PageDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findByDeletedFalse(pageable);
        PageDto<BrandDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(brandPage.getContent(), listType));
        pageDto.setTotalRecords(brandPage.getTotalElements());
        pageDto.setTotalPages(brandPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        Brand brand=brandRepository.findByIdentifier(identifier);
        if (brand == null) {
            throw new ResourceNotFoundException("Warehouse with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand != null) {
            boolean currentStatus = Boolean.TRUE.equals(brand.getStatus());
            brand.setStatus(!currentStatus);

            brandRepository.save(brand);
        }
    }

    @Override
    public List<BrandDto> findActiveBrands() {
        Type listType = new TypeToken<List<BrandDto>>() {}.getType();
        return modelMapper.map(brandRepository.findByStatusTrue(),listType);
    }
}

