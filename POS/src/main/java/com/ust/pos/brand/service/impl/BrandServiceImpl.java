package com.ust.pos.brand.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.BrandRepository;
import com.ust.pos.model.Brand;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.ust.pos.exception.ResourceNotFoundException;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class BrandServiceImpl extends CommonService implements BrandService {

    public static final String BRAND_WITH_IDENTIFIER = "Brand with identifier - ";

    private final ModelMapper modelMapper;

    private final BrandRepository brandRepository;

    public BrandServiceImpl(ModelMapper modelMapper, BrandRepository brandRepository) {
        this.modelMapper = modelMapper;
        this.brandRepository = brandRepository;
    }

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findByDeletedFalse(pageable);

        WsDto<BrandDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(brandPage.getContent(), listType));
        userWsDto.setTotalRecords(brandPage.getTotalElements());
        userWsDto.setTotalPages(brandPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());

        return userWsDto;
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
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand != null) {
            if(existingBrand.isDeleted()){
                brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + "has been soft deleted.(Rollback by changing status");
                brandDto.setSuccess(false);
                return brandDto;
            }
            brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        setAuditFields(brand, true);
        brandRepository.save(brand);
        return brandDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        softDelete(brand);
        setAuditFields(brand,false);
        brandRepository.save(brand);
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        Brand brand = brandRepository.findByIdentifierAndDeletedFalse(identifier);
        if (brand == null) {
            throw new ResourceNotFoundException("Brand with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(brand, BrandDto.class);    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand == null) {
            brandDto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        modelMapper.map(brandDto, existingBrand);
        setAuditFields(existingBrand,false);
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Override
    public BrandDto changeToggleStatus(String identifier, boolean status) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand != null) {
            brand.setStatus(status);
            brandRepository.save(brand);
        }
        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public List<BrandDto> findActiveStatus() {
        List<Brand> allBrands = brandRepository.findAll();
        List<Brand> activeBrands = allBrands.stream().filter(Brand::isStatus).toList();

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(activeBrands, listType);
    }
}
