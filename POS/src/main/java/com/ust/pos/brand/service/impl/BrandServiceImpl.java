package com.ust.pos.brand.service.impl;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.common.CommonService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class BrandServiceImpl extends CommonService implements BrandService {

    private static final String BRAND_WITH_IDENTIFIER = "Brand with identifier - ";
    private static final String NOT_FOUND = " not found";
    private static final String ALREADY_EXISTS = " already exists";
    private static final String SOFT_DELETED = " has been soft deleted.(Rollback by changing status";

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    public BrandServiceImpl(BrandRepository brandRepository, ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    private String notFound(String identifier) {
        return BRAND_WITH_IDENTIFIER + identifier + NOT_FOUND;
    }

    private String alreadyExists(String identifier) {
        return BRAND_WITH_IDENTIFIER + identifier + ALREADY_EXISTS;
    }

    private String softDeleted(String identifier) {
        return BRAND_WITH_IDENTIFIER + identifier + SOFT_DELETED;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(requireResource(brandRepository.findByIdentifier(identifier), notFound(identifier)), BrandDto.class);
    }

    @Override
    public BrandDto toggleStatus(String identifier) {

        Brand brand = requireResource(brandRepository.findByIdentifier(identifier), notFound(identifier));

        brand.setStatus(!brand.isStatus());

        brandRepository.save(brand);

        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public BrandDto save(BrandDto brandDto) {

        brandDto.setIdentifier(brandDto.getIdentifier().trim());

        String identifier = brandDto.getIdentifier();

        Brand existingBrand = brandRepository.findByIdentifier(identifier);

        if (existingBrand != null) {

            if (existingBrand.isDeleted()) {

                brandDto.setMessage(softDeleted(identifier));

                brandDto.setSuccess(false);

                return brandDto;
            }

            brandDto.setMessage(alreadyExists(identifier));

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

        String identifier = brandDto.getIdentifier();

        Brand existingBrand = requireResource(brandRepository.findByIdentifier(identifier), notFound(identifier));

        modelMapper.map(brandDto, existingBrand);

        setAuditFields(existingBrand, false);

        brandRepository.save(existingBrand);

        return brandDto;
    }

    @Override
    public boolean delete(String identifier) {

        Brand brand = requireResource(brandRepository.findByIdentifier(identifier), notFound(identifier));

        softDelete(brand);

        setAuditFields(brand, false);

        brandRepository.save(brand);

        return true;
    }

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();

        Page<Brand> brandPage = brandRepository.findByDeletedFalse(pageable);

        WsDto<BrandDto> wsDto = new WsDto<>();

        wsDto.setDtoList(modelMapper.map(brandPage.getContent(), listType));

        wsDto.setTotalRecords(brandPage.getTotalElements());

        wsDto.setTotalPages(brandPage.getTotalPages());

        wsDto.setSizePerPage(pageable.getPageSize());

        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public WsDto<BrandDto> findAll(Specification<Brand> spec, Pageable pageable, String keyword) {

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();

        Page<Brand> brandPage = brandRepository.findAll(spec, pageable);

        WsDto<BrandDto> wsDto = new WsDto<>();

        wsDto.setDtoList(modelMapper.map(brandPage.getContent(), listType));

        wsDto.setTotalRecords(brandPage.getTotalElements());

        wsDto.setTotalPages(brandPage.getTotalPages());

        wsDto.setSizePerPage(pageable.getPageSize());

        wsDto.setPage(pageable.getPageNumber());

        wsDto.setKeyword(keyword);

        return wsDto;
    }

    @Override
    public List<BrandDto> findIfTrue() {

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();

        return modelMapper.map(brandRepository.findByStatusIsTrue(), listType);
    }
}