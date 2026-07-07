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
@Transactional
public class BrandServiceImpl extends BaseService implements BrandService {
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
                brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " was deleted and cannot be re-created");
            } else {
                brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " already exists");
            }
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        setCreatedDetails(brand);
        brandRepository.save(brand);
        brandDto.setSuccess(true);
        return brandDto;
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifierAndDeletedFalse(identifier);
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
    public void delete(String identifier) {
        Brand brand = brandRepository.findByIdentifierAndDeletedFalse(identifier);
        if (brand != null) {
            softDelete(brand);
            setModifiedDetails(brand);
            brandRepository.save(brand);
        }
    }

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findByDeletedFalse(pageable);
        WsDto<BrandDto> brandDtoWsDto = new WsDto<>();
        brandDtoWsDto.setDtoList(modelMapper.map(brandPage.getContent(), listType));
        brandDtoWsDto.setTotalRecords(brandPage.getTotalElements());
        brandDtoWsDto.setTotalPages(brandPage.getTotalPages());
        brandDtoWsDto.setSizePerPage(pageable.getPageSize());
        brandDtoWsDto.setPage(pageable.getPageNumber());
        return brandDtoWsDto;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        Brand brand = brandRepository.findByIdentifierAndDeletedFalse(identifier);
        if (brand == null) {
            throw new ResourceNotFoundException("Brand with identifier " + identifier + " not found");
        }
        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public void updateStatus(String identifier, boolean status) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        brand.setStatus(status);
        brandRepository.save(brand);
    }

    @Override
    public List<BrandDto> findAllActive() {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(
                brandRepository.findByStatusAndDeletedFalse(true),
                listType
        );
    }

    @Override
    public WsDto<BrandDto> findAll(Specification<Brand> example, Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> page = brandRepository.findAll(example, pageable);
        WsDto<BrandDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }
}