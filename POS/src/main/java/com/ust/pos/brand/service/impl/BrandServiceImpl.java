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
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl extends BaseService implements BrandService {

    public static final RuntimeException BRAND_NOT_FOUND = new RuntimeException("brand not found");

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);

        if (existingBrand != null) {
            if (Boolean.TRUE.equals(existingBrand.getDeleted())) {
                brandDto.setMessage("Brand with Identifier " + identifier + " already exists (Soft-Deleted)");
                brandDto.setSuccess(false);
                return brandDto;
            }
            brandDto.setMessage("Brand with identifier - " + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        if (brand.getStatus() == null) {
            brand.setStatus(true);
        }
        setCreatedDetails(brand);
        brandRepository.save(brand);
        return brandDto;
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingBrand == null) {
            brandDto.setMessage("Brand with identifier - " + identifier + " not found");
            brandDto.setSuccess(false);
            return brandDto;
        }
        String originalCreatedBy = existingBrand.getCreatedBy();
        LocalDateTime originalCreatedOn = existingBrand.getCreatedOn();
        modelMapper.map(brandDto, existingBrand);
        existingBrand.setCreatedBy(originalCreatedBy);
        existingBrand.setCreatedOn(originalCreatedOn);
        setModifiedDetails(existingBrand);
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Brand brand = brandRepository.findByIdentifierAndDeletedFalse(identifier);

        if (brand != null) {
            softDelete(brand);
            setModifiedDetails(brand);
            brandRepository.save(brand);
        }
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifierAndDeletedFalse(identifier), BrandDto.class);
    }

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();

        Page<Brand> brandPage = brandRepository.findAllByDeletedFalse(pageable);
        WsDto<BrandDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(brandPage.getContent(), listType));
        wsDto.setTotalRecords(brandPage.getTotalElements());
        wsDto.setTotalPage(brandPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public List<BrandDto> findAllActive() {
        return brandRepository.findByStatusTrueAndDeletedFalse().stream().map(brand -> modelMapper.map(brand, BrandDto.class)).toList();
    }

    @Override
    @Transactional
    public BrandDto toggleStatus(String identifier) {
        Brand brand = brandRepository.findByIdentifierAndDeletedFalse(identifier);

        if (brand == null) {
            throw new IllegalArgumentException("brand not found with identifier: " + identifier
            );
        }
        Boolean currentStatus = brand.getStatus();
        brand.setStatus(currentStatus == null ? Boolean.TRUE : !currentStatus);
        setModifiedDetails(brand);
        Brand saved = brandRepository.save(brand);
        return modelMapper.map(saved, BrandDto.class
        );
    }
}