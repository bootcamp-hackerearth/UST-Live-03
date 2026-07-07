package com.ust.pos.brand.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
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

    private final BrandRepository brandRepository;

    private final ModelMapper modelMapper;

    public BrandServiceImpl(BrandRepository brandRepository, ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();

        Page<Brand> brandPage = brandRepository.findByIsDeletedFalse(pageable);

        WsDto<BrandDto> dto = new WsDto<>();

        dto.setContent(modelMapper.map(brandPage.getContent(), listType));
        dto.setTotalRecords(brandPage.getTotalElements());
        dto.setTotalPages(brandPage.getTotalPages());
        dto.setSizePerPage(pageable.getPageSize());
        dto.setPage(pageable.getPageNumber());

        return dto;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand == null) {
            throw new ResourceNotFoundException("Data cannot be found");
        }
        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand != null) {
            brandDto.setMessage(
                    existingBrand.isDeleted()
                            ? "Brand - " + identifier + " already exists but was deleted, Please contact Administrator"
                            : "Brand - " + identifier + "already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        setCreatedDetails(brand);
        brandRepository.save(brand);
        return brandDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        setModifiedDetails(brand);
        softDelete(brand);
    }

    @Override
    public List<Brand> findActiveBrands() {
        return brandRepository.findByStatus(true);
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand == null) {
            brandDto.setMessage("Brand with brand - " + identifier + " not found");
            brandDto.setSuccess(false);
            return brandDto;
        }
        modelMapper.map(brandDto, existingBrand);
        setModifiedDetails(existingBrand);
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand != null) {
            brand.setStatus(!brand.isStatus());
            brandRepository.save(brand);
        }
    }

    @Override
    public WsDto<BrandDto> findAll(Specification<Brand> example, Pageable pageable) {

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> page = brandRepository.findAll(example, pageable);

        WsDto<BrandDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}
