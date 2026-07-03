package com.ust.pos.brand.service.impl;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.common.CommonService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import com.ust.pos.util.FileStorageUtil;
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
    public static final String BRAND_WITH_IDENTIFIER = "Brand with identifier - ";
    private final BrandRepository brandRepository;
    private final FileStorageUtil fileStorageUtil;
    private final ModelMapper modelMapper;

    public BrandServiceImpl(BrandRepository brandRepository, FileStorageUtil fileStorageUtil, ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.fileStorageUtil = fileStorageUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand != null) {
            if (!existingBrand.isDeleted()) {
                brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " already exists");
                brandDto.setSuccess(false);
                return brandDto;
            }
            brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        String iconPath = fileStorageUtil.saveBrandIcon(brandDto.getIcon(), identifier);
        brand.setIconPath(iconPath);
        setAuditFields(brand, true);
        brandRepository.save(brand);
        brandDto = modelMapper.map(brand, BrandDto.class);
        brandDto.setSuccess(true);
        brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " Added Successfully");
        return brandDto;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
    }

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {
        Page<Brand> brandPage = brandRepository.findByDeletedFalse(pageable);
        Type type = new TypeToken<List<BrandDto>>() {
        }.getType();
        WsDto<BrandDto> brandWsDto = new WsDto<>();
        brandWsDto.setDtoList(modelMapper.map(brandPage.getContent(), type));
        brandWsDto.setTotalRecords(brandPage.getTotalElements());
        brandWsDto.setTotalPages(brandPage.getTotalPages());
        brandWsDto.setSizePerPage(pageable.getPageSize());
        brandWsDto.setPage(pageable.getPageNumber());
        return brandWsDto;
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

    @Override
    public List<BrandDto> findAllActive() {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(brandRepository.findAllByStatusAndDeletedFalse(true), listType);
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        Brand brand = brandRepository.findByIdentifier(brandDto.getIdentifier());
        if (brand == null) {
            brandDto.setSuccess(false);
            brandDto.setMessage("Brand not found");
            return brandDto;
        }
        brand.setDescription(brandDto.getDescription());
        if (brandDto.getIcon() != null && !brandDto.getIcon().isEmpty()) {
            String iconPath = fileStorageUtil.saveBrandIcon(brandDto.getIcon(), brand.getIdentifier());
            brand.setIconPath(iconPath);
        }
        setAuditFields(brand, false);
        brandRepository.save(brand);
        brandDto = modelMapper.map(brand, BrandDto.class);
        brandDto.setMessage(BRAND_WITH_IDENTIFIER + brandDto.getIdentifier() + " Updated");
        return brandDto;
    }

    @Override
    public BrandDto toggleStatus(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        brand.setStatus(!brand.isStatus());
        setAuditFields(brand, false);
        brandRepository.save(brand);
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
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
}
