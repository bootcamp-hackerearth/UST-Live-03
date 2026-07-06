package com.ust.pos.brand.service.impl;

import com.ust.pos.CommonService;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.ProductDto;
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
@Transactional
public class BrandServiceImpl extends CommonService implements BrandService {

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
            if(existingBrand.isDeleted()){
                brandDto.setMessage("Brand identifier - " + identifier + " not available");
                brandDto.setSuccess(false);
                return brandDto;
            }
            brandDto.setMessage("Brand with identifier - " + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        setAuditFields(brand,true);
        brandRepository.save(brand);
        return brandDto;
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        Brand existingBrand = brandRepository.findByIdentifier(brandDto.getIdentifier());
        if (existingBrand == null) {
            brandDto.setSuccess(false);
            brandDto.setMessage("Brand not found");
            return brandDto;
        }
        existingBrand.setDescription(brandDto.getDescription());
        existingBrand.setStatus(brandDto.getStatus());
        setAuditFields(existingBrand,false);
        brandRepository.save(existingBrand);
        return brandDto;
    }

    @Override
    public void delete(String identifier) {
        Brand brand=brandRepository.findByIdentifier(identifier.trim());
        softDelete(brand);
        setAuditFields(brand,false);
    }

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {}.getType();
        Page<Brand> brandPage = brandRepository.findByIsDeletedFalse(pageable);
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
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        brand.setStatus(!brand.getStatus());
        setAuditFields(brand,false);
        brandRepository.save(brand);
    }

    @Override
    public List<BrandDto> findAllActive() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(brandRepository.findByStatusTrueAndIsDeletedFalse(), listType);
    }

    @Override
    public WsDto<BrandDto> findAll(Specification<Brand> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> page = brandRepository.findAll(example, pageable);
        WsDto<BrandDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}
