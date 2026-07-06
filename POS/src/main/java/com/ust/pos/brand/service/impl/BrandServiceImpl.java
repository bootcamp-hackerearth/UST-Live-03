package com.ust.pos.brand.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class BrandServiceImpl extends BaseService implements BrandService {

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    public BrandServiceImpl(BrandRepository brandRepository, ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier().trim();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand != null) {
            if (existingBrand.isDeleted()) {
                brandDto.setMessage("Brand with identifier " + identifier + " has been soft deleted. (Rollback by changing status)");
                brandDto.setSuccess(false);
                return brandDto;
            }
            brandDto.setMessage("Brand with identifier - " + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        setCreatedDetails(brand);
        brandRepository.save(brand);
        brandDto.setSuccess(true);
        brandDto.setMessage("Brand created successfully");
        return brandDto;
    }

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        WsDto<BrandDto> wsDto = new WsDto<>();
        if (pageable == null) {
            List<BrandDto> brandDtoList = modelMapper.map(brandRepository.findByDeletedFalse(pageable), listType);
            wsDto.setDtoList(brandDtoList);
            wsDto.setTotalRecords(brandDtoList.size());
            return wsDto;
        }
        Page<Brand> brandPage = brandRepository.findByDeletedFalse(pageable);
        wsDto.setDtoList(modelMapper.map(brandPage.getContent(), listType));
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setTotalPages(brandPage.getTotalPages());
        wsDto.setTotalRecords(brandPage.getTotalElements());
        return wsDto;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        softDelete(brand);
        setModifiedDetails(brand);
        brandRepository.save(brand);
    }

    @Override
    public BrandDto update(BrandDto brandDto) {
        Brand existingBrand = brandRepository.findByIdentifier(brandDto.getIdentifier());
        if (existingBrand == null) {
            brandDto.setMessage("Brand with identifier - " + brandDto.getIdentifier() + " not found");
            brandDto.setSuccess(false);
            return brandDto;
        }
        modelMapper.map(brandDto, existingBrand);
        setModifiedDetails(existingBrand);
        brandRepository.save(existingBrand);
        brandDto.setSuccess(true);
        brandDto.setMessage("Brand updated successfully");
        return brandDto;
    }

    @Override
    @Transactional
    public BrandDto toggleStatus(String identifier, boolean status) {
        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand == null) {
            BrandDto response = new BrandDto();
            response.setSuccess(false);
            response.setMessage("Brand not found");
            return response;
        }
        brand.setStatus(status);
        setModifiedDetails(brand);
        brandRepository.save(brand);
        BrandDto response = modelMapper.map(brand, BrandDto.class);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

    @Override
    public WsDto<BrandDto> findAll(Specification<Brand> specification,
                                   Pageable pageable) {

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();

        Page<Brand> page =
                brandRepository.findAll(specification, pageable);

        WsDto<BrandDto> wsDto = new WsDto<>();

        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}