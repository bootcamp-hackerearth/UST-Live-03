package com.ust.pos.brand.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import com.ust.pos.model.Customer;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class BrandServiceImpl extends BaseService implements BrandService {

    public static final String BRAND = "Brand";
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    public BrandServiceImpl(BrandRepository brandRepository, ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PaginationResponseDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();

        Page<Brand> brandPage = brandRepository.findByIsDeletedFalse(pageable);

        List<BrandDto> brandDtoList = modelMapper.map(brandPage.getContent(), listType);

        PaginationResponseDto<BrandDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(brandDtoList);
        paginationResponseDto.setPage(brandPage.getNumber());
        paginationResponseDto.setSizePerPage(brandPage.getSize());
        paginationResponseDto.setTotalPages(brandPage.getTotalPages());
        paginationResponseDto.setTotalRecords(brandPage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public PaginationResponseDto<BrandDto> findAll(Specification<Brand> example, Pageable pageable) {

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> page = brandRepository.findAll(example, pageable);

        PaginationResponseDto<BrandDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationResponseDto.setTotalRecords(page.getTotalElements());
        paginationResponseDto.setTotalPages(page.getTotalPages());
        paginationResponseDto.setSizePerPage(pageable.getPageSize());
        paginationResponseDto.setPage(pageable.getPageNumber());

        return paginationResponseDto;
    }

    @Override
    public List<BrandDto> findByStatusTrue() {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(brandRepository.findByStatusTrue(), listType);
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);

        if (brand == null) {
            throw new ResourceNotFoundException("Brand does not exist");
        }

        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand brand = brandRepository.findByIdentifier(identifier);

        if (brand != null) {

            if (isSoftDeleted(brand)) {
                throw new IllegalStateException(
                        getDeletedMessage(BRAND, identifier)
                );
            }
            throw new IllegalArgumentException(
                    "Brand " + identifier + " already exists"
            );
        }

        brand = modelMapper.map(brandDto, Brand.class);
        setCreatedDetails(brand);
        brandRepository.save(brand);

        brandDto.setSuccess(true);
        brandDto.setMessage("Successfully added the brand");

        return brandDto;
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand =
                brandRepository.findByIdentifier(identifier);

        if (existingBrand == null) {
            throw new ResourceNotFoundException(
                    "Brand not found"
            );
        }

        if (isSoftDeleted(existingBrand)) {
            throw new IllegalStateException(
                    getDeletedMessage(BRAND, identifier)
            );
        }

        modelMapper.map(brandDto, existingBrand);
        setModifiedDetails(existingBrand);
        brandRepository.save(existingBrand);

        brandDto.setSuccess(true);
        brandDto.setMessage("Successfully updated the brand");

        return brandDto;
    }

    @Override
    @Transactional
    public BrandDto updateStatus(String identifier, boolean status) {
        Brand brand =
                brandRepository.findByIdentifier(identifier);

        if (brand == null) {
            throw new ResourceNotFoundException(
                    "Brand not found"
            );
        }

        setModifiedDetails(brand);
        brand.setStatus(status);

        BrandDto response = new BrandDto();
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        Brand brand =
                brandRepository.findByIdentifier(identifier);

        if (brand == null) {
            throw new ResourceNotFoundException(
                    "Brand not found"
            );
        }

        if (isSoftDeleted(brand)) {
            throw new IllegalStateException(
                    getDeletedMessage(BRAND, identifier)
            );
        }

        softDelete(brand);
        setModifiedDetails(brand);
        brandRepository.save(brand);
    }
}
