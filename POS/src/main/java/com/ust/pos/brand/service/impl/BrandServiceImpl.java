package com.ust.pos.brand.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.brand.service.BrandService;
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
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class BrandServiceImpl extends BaseService implements BrandService {

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    public BrandServiceImpl(ModelMapper modelMapper, BrandRepository brandRepository) {
        this.modelMapper = modelMapper;
        this.brandRepository = brandRepository;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand != null) {
            brandDto.setMessage(
                    existingBrand.isDeleted()
                            ? " Brand with identifier - " + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : " Brand with identifier - " + identifier
                            + " already exists."
            );
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
            brandDto.setMessage("Brand with identifier - " + identifier + " not found");
            brandDto.setSuccess(false);
            return brandDto;
        }
        modelMapper.map(brandDto, existingBrand);
        setModifiedDetails(existingBrand);
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Transactional
    public void delete(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        setModifiedDetails(brand);
        softDelete(brand);
    }

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();

        Page<Brand> brandPage = brandRepository.findByIsDeletedFalse(pageable);

        List<BrandDto> brandDtos = modelMapper.map(
                brandPage.getContent(),
                listType
        );

        WsDto<BrandDto> wsDto =
                new WsDto<>();

        wsDto.setContent(brandDtos);
        wsDto.setPage(brandPage.getNumber());
        wsDto.setSizePerPage(brandPage.getSize());
        wsDto.setTotalPages(brandPage.getTotalPages());
        wsDto.setTotalRecords(brandPage.getTotalElements());

        return wsDto;
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
    public WsDto<BrandDto> findAll(Specification<Brand> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findAll(example,pageable);
        WsDto<BrandDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(brandPage.getContent(), listType));
        wsDto.setTotalRecords(brandPage.getTotalElements());
        wsDto.setTotalPages(brandPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}