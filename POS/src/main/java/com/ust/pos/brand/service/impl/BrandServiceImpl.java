package com.ust.pos.brand.service.impl;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private static final String BRAND_WITH_IDENTIFIER = "Brand with identifier - ";

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);

        if (existingBrand != null) {
            if (Boolean.TRUE.equals(existingBrand.getIsDeleted())) {
                brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " was deleted. Contact admin for further support or try with a different identifier.");
            } else {
                brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " already exists");
            }
            brandDto.setSuccess(false);
            return brandDto;
        }

        Brand brand = modelMapper.map(brandDto, Brand.class);
        brand.setIsDeleted(false);
        brandRepository.save(brand);
        brandDto.setSuccess(true);
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
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Override
    public BrandDto delete(String identifier) {
        BrandDto brandDto = new BrandDto();
        Brand brand = brandRepository.findByIdentifier(identifier);

        if (brand == null) {
            brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " not found");
            brandDto.setSuccess(false);
            return brandDto;
        }

        brand.setIsDeleted(true);
        brand.setStatus(false);
        brandRepository.save(brand);
        brandDto.setSuccess(true);
        brandDto.setMessage("Brand deleted successfully");
        return brandDto;
    }

    @Override
    public PaginatedResponseDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findByIsDeleted(false, pageable);
        List<BrandDto> items = modelMapper.map(brandPage.getContent(), listType);
        PaginatedResponseDto<BrandDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(brandPage.getTotalElements());
        response.setTotalPages(brandPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand == null) {
            throw new ResourceNotFoundException("Brand with identifier " + identifier + " not found");
        }
        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public List<BrandDto> findAllActive() {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(brandRepository.findByStatusAndIsDeleted(true, false), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        brand.setStatus(status);
        brandRepository.save(brand);
    }
}