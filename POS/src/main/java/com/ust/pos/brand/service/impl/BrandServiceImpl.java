package com.ust.pos.brand.service.impl;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class BrandServiceImpl extends CommonService implements BrandService {

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    public BrandServiceImpl(BrandRepository brandRepository, ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
    }

    @Override
    public BrandDto toggleStatus(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        brand.setStatus(!brand.isStatus());
        setAuditFields(brand, false);
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
                brandDto.setMessage("Brand with identifier " + identifier + " was previously deleted. " +
                        "Please contact backend team to restore."
                );
                brandDto.setSuccess(false);
                return brandDto;
            }
            brandDto.setMessage("Brand with identifier - " + identifier + " already exists");
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
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand == null) {
            brandDto.setMessage("Brand with identifier - " + identifier + " not found");
            brandDto.setSuccess(false);
            return brandDto;
        }
        modelMapper.map(brandDto, existingBrand);
        setAuditFields(existingBrand, false);
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Override
    public boolean delete(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand == null) return false;
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
        WsDto<BrandDto> brandWsDto = new WsDto<>();
        brandWsDto.setDtoList(modelMapper.map(brandPage.getContent(), listType));
        brandWsDto.setTotalRecords(brandPage.getTotalElements());
        brandWsDto.setTotalPages(brandPage.getTotalPages());
        brandWsDto.setSizePerPage(pageable.getPageSize());
        brandWsDto.setPage(pageable.getPageNumber());

        return brandWsDto;
    }

    @Override
    public List<BrandDto> findIfTrue() {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(brandRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }
}