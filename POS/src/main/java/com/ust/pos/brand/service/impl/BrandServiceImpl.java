package com.ust.pos.brand.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
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
@RequiredArgsConstructor
public class BrandServiceImpl extends BaseService implements BrandService {

    private final ModelMapper modelMapper;
    private final BrandRepository brandRepository;

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand != null) {
            if (Boolean.TRUE.equals(existingBrand.getDeleted())) {
                brandDto.setMessage("Brand identifier - " + identifier + " not available");
                brandDto.setSuccess(false);
                return brandDto;
            }
            brandDto.setMessage("Brand with identifier - " + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        setCreatedDetails(brand);
        setModifiedDetails(brand);
        brandRepository.save(brand);
        return brandDto;
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand == null) {
            brandDto.setMessage("brand with identifier - " + identifier + " not found");
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
        setModifiedDetails(brand);
        softDelete(brand);
    }

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findAllByDeletedFalse(pageable);

        WsDto<BrandDto> brandWsDto = new WsDto<>();
        brandWsDto.setDtoList(modelMapper.map(brandPage.getContent(), listType));
        brandWsDto.setTotalRecords(brandPage.getTotalElements());
        brandWsDto.setTotalPages(brandPage.getTotalPages());
        brandWsDto.setSizePerPage(pageable.getPageSize());
        brandWsDto.setPage(pageable.getPageNumber());
        return brandWsDto;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifierAndDeletedFalse(identifier), BrandDto.class);
    }

    @Override
    public List<Brand> findActiveBrands() {
        return brandRepository.findByStatusTrueAndDeletedFalse();
    }

    @Override
    public BrandDto toggleStatus(String identifier) {
        Brand brands = brandRepository.findByIdentifier(identifier);
        if (brands == null) {
            return null;
        }
        brands.setStatus(!brands.isStatus());
        setModifiedDetails(brands);
        brandRepository.save(brands);
        return modelMapper.map(brands, BrandDto.class);
    }
}
