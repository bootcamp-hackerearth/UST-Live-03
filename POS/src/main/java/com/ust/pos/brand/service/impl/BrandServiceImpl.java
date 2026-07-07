package com.ust.pos.brand.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
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

    private static final String VALIDATION_MESSAGE = "Brand with identifier - ";

    private final BrandRepository brandRepository;

    private final ModelMapper modelMapper;

    public BrandServiceImpl(ModelMapper modelMapper, BrandRepository brandRepository) {
        this.modelMapper = modelMapper;
        this.brandRepository = brandRepository;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {

        Brand brand = brandRepository.findByIdentifier(identifier);

        if (brand == null) {
            throw new ResourseNotFoundException("Data cannot found");
        }

        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public BrandDto save(BrandDto brandDto) {

        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);

        if (existingBrand != null) {

            throw new IllegalArgumentException(
                    existingBrand.isDeleted()
                            ? VALIDATION_MESSAGE + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : VALIDATION_MESSAGE + identifier
                            + " already exists."
            );
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
            brandDto.setMessage(VALIDATION_MESSAGE + identifier + " not found");
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
        setModifiedDetails(brand);
        softDelete(brand);
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

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {

        Page<Brand> brandPage = brandRepository.findByIsDeletedFalse(pageable);

        WsDto<BrandDto> paginationResponseDto = new WsDto<>();

        List<BrandDto> brandDtos = brandPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, BrandDto.class))
                .toList();

        paginationResponseDto.setContent(brandDtos);
        paginationResponseDto.setPage(brandPage.getNumber());
        paginationResponseDto.setSizePerPage(brandPage.getSize());
        paginationResponseDto.setTotalPages(brandPage.getTotalPages());
        paginationResponseDto.setTotalRecords(brandPage.getTotalElements());

        return paginationResponseDto;
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
    public List<BrandDto> findActiveBrands() {

        List<Brand> brands = brandRepository.findByStatus(true);
        return brands.stream().map(brand -> modelMapper.map(brand, BrandDto.class)).toList();
    }

}



