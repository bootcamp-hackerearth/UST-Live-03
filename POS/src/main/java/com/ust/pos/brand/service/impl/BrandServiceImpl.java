package com.ust.pos.brand.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Brand;
import com.ust.pos.modell.BrandRepository;
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
@RequiredArgsConstructor
public class BrandServiceImpl extends BaseService implements BrandService {

    public static final String BRAND_WITH_IDENTIFIER = "Brand with identifier - ";

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);

        if (existingBrand != null) {

            if (Boolean.TRUE.equals(existingBrand.getDeleted())) {
                brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " was deleted and cannot be created again.");
                brandDto.setSuccess(false);
                return brandDto;
            }
            brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        brand.setStatus(brandDto.getStatus() != null ? brandDto.getStatus() : Boolean.TRUE);
        setCreatedDetails(brand);
        brandRepository.save(brand);
        brandDto.setMessage("Brand created successfully");
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
    @Transactional
    public void delete(String identifier) {
        Brand brand = brandRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(brand);
        setModifiedDetails(brand);
        brandRepository.save(brand);
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifierAndDeletedFalse(identifier), BrandDto.class
        );
    }

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findAllByDeletedFalse(pageable);
        WsDto<BrandDto> brandWsDto = new WsDto<>();
        brandWsDto.setDtoList(modelMapper.map(brandPage.getContent(), listType));
        brandWsDto.setTotalRecords(brandPage.getTotalElements());
        brandWsDto.setTotalPage(brandPage.getTotalPages());
        brandWsDto.setSizePerPage(pageable.getPageSize());
        brandWsDto.setPage(pageable.getPageNumber());
        return brandWsDto;
    }

    @Override
    @Transactional
    public BrandDto toggleStatus(String identifier) {
        Brand brand  = brandRepository.findByIdentifierAndDeletedFalse(identifier);

        if (brand == null) {
            throw new IllegalArgumentException("Brand not found with identifier: " + identifier);
        }
        Boolean currentStatus = brand.getStatus();
        brand.setStatus(currentStatus == null ? Boolean.TRUE : !currentStatus);
        setModifiedDetails(brand);
        Brand saved = brandRepository.save(brand);
        return modelMapper.map(saved, BrandDto.class);
    }

    @Override
    public List<BrandDto> findAllActive() {
        return brandRepository.findByStatusTrueAndDeletedFalse()
                .stream()
                .map(brand -> modelMapper.map(brand, BrandDto.class))
                .toList();
    }

}