package com.ust.pos.brand.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class BrandServiceImpl extends BaseService implements BrandService {

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    public BrandServiceImpl(
            BrandRepository brandRepository,
            ModelMapper modelMapper) {

        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        Brand existingBrand = brandRepository.findByIdentifier(brandDto.getIdentifier());
        if (existingBrand != null) {
            if(existingBrand.isDeleted())
            {
                brandDto.setMessage("Category with identifier - " + brandDto.getIdentifier() +
                        "has been soft deleted.(Rollback by changing status)");
                brandDto.setSuccess(false);
                return brandDto;
            }
            brandDto.setMessage("Brand with identifier - " + brandDto.getIdentifier() + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        setCreatedDetails(brand);
        brandRepository.save(brand);
        return brandDto;
    }

    @Override
    public PaginationResponseDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {}.getType();
        PaginationResponseDto<BrandDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Brand> brands = brandRepository.findAll();
            response.setDtoList(modelMapper.map(brands, listType));
            response.setTotalRecords(brands.size());
            response.setTotalPages(1);
            response.setSizePerPage(brands.size());
            response.setPage(0);
        } else {
            Page<Brand> brandPage = brandRepository.findByDeletedFalse(pageable);
            response.setDtoList(modelMapper.map(brandPage.getContent(), listType));
            response.setTotalRecords(brandPage.getTotalElements());
            response.setTotalPages(brandPage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
    }

    @Override
    @Transactional
    public void deleteByIdentifier(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand == null) {
            throw new EntityNotFoundException(
                    "Brand with identifier - " + identifier + " not found");
        }
        softDelete(brand);
        setModifiedDetails(brand);
        brandRepository.save(brand);
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
        setModifiedDetails(existingBrand);
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Override
    @Transactional
    public BrandDto toggleStatus(String identifier, boolean status) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand != null) {
            brand.setStatus(!brand.isStatus());
            setModifiedDetails(brand);
            brandRepository.save(brand);
        }
        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public PaginationResponseDto<BrandDto> findAll(Specification<Brand> example, Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> page = brandRepository.findAll(example, pageable);
        PaginationResponseDto<BrandDto> paginationresponse = new PaginationResponseDto<>();
        paginationresponse.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationresponse.setTotalRecords(page.getTotalElements());
        paginationresponse.setTotalPages(page.getTotalPages());
        paginationresponse.setSizePerPage(pageable.getPageSize());
        paginationresponse.setPage(pageable.getPageNumber());
        return paginationresponse;
    }
}