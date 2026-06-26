package com.ust.pos.brand.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import com.ust.pos.brand.service.BrandService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

import java.util.List;

@Service
public class BrandServiceImpl extends BaseService implements BrandService {
    public static final String BRAND_WITH_IDENTIFIER = "Brand with identifier - ";

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    public BrandServiceImpl(BrandRepository brandRepository, ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand == null) {
            throw new ResourceNotFoundException("Brand with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand != null) {
            if (existingBrand.isDeleted()) {
                brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + "has been soft deleted.(Rollback by changing status)");
                brandDto.setSuccess(false);
                return brandDto;
            }
            brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        setCreatedDetails(brand);
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
        setModifiedDetails(existingBrand);
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        softDelete(brand);
        setModifiedDetails(brand);
        brandRepository.save(brand);
    }

    public WsDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findByDeletedFalse(pageable);

        WsDto<BrandDto> brandWsDto = new WsDto<>();
        brandWsDto.setDtoList(modelMapper.map(brandPage.getContent(), listType));
        brandWsDto.setTotalRecords(brandPage.getTotalElements());
        brandWsDto.setTotalPages(brandPage.getTotalPages());
        brandWsDto.setSizePerPage(pageable.getPageSize());
        brandWsDto.setPage(pageable.getPageNumber());

        return brandWsDto;
    }

    @Override
    public BrandDto toggleStatus(String identifier, boolean status) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand != null) {
            brand.setStatus(status);
            setModifiedDetails(brand);
            brandRepository.save(brand);
        }
        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public List<BrandDto> findActiveBrands() {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(brandRepository.findByStatusTrue(), listType);
    }
}