package com.ust.pos.brand.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    public WsDto<BrandDto> findAll(Pageable pageable) {

        Type type = new TypeToken<List<BrandDto>>() {}.getType();

        Page<Brand> page = brandRepository.findByDeletedFalse(pageable);

        WsDto<BrandDto> ws = new WsDto<>();
        ws.setDtoList(modelMapper.map(page.getContent(), type));
        ws.setTotalRecords(page.getTotalElements());
        ws.setTotalPages(page.getTotalPages());
        ws.setSizePerPage(pageable.getPageSize());
        ws.setPage(pageable.getPageNumber());

        return ws;
    }

    @Override
    public BrandDto save(BrandDto dto) {

        BrandDto response = new BrandDto();

        if (dto.getBrandName() == null || dto.getBrandName().trim().isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Brand name is required");
            return response;
        }

        Brand existing = brandRepository.findByIdentifier(dto.getBrandName());
        if (existing != null && !Boolean.TRUE.equals(existing.getDeleted())) {
            response.setSuccess(false);
            response.setMessage("Brand already exists");
            return response;
        }

        Brand brand = new Brand();
        brand.setIdentifier(dto.getBrandName().trim());
        brand.setBrandName(dto.getBrandName());
        brand.setDescription(dto.getDescription());
        brand.setStatus(true);

        setCreatedDetails(brand);

        Brand saved = brandRepository.save(brand);

        BrandDto result = modelMapper.map(saved, BrandDto.class);
        result.setSuccess(true);
        result.setMessage("Brand created successfully");

        return result;
    }

    @Override
    public BrandDto update(BrandDto dto) {

        Brand brand = brandRepository.findByIdentifier(dto.getIdentifier());

        if (brand == null || Boolean.TRUE.equals(brand.getDeleted())) {
            throw new ResourceNotFoundException("Brand not found: " + dto.getIdentifier());
        }

        brand.setBrandName(dto.getBrandName());
        brand.setDescription(dto.getDescription());
        setModifiedDetails(brand);
        Brand saved = brandRepository.save(brand);
        BrandDto result = modelMapper.map(saved, BrandDto.class);
        result.setSuccess(true);
        result.setMessage("Brand updated successfully");

        return result;
    }

    @Override
    public void delete(String identifier) {

        Brand brand = brandRepository.findByIdentifier(identifier);

        if (brand == null || Boolean.TRUE.equals(brand.getDeleted())) {
            throw new ResourceNotFoundException("Brand not found: " + identifier);
        }

        brand.setDeleted(true);
        setModifiedDetails(brand);
        brandRepository.save(brand);
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {

        Brand brand = brandRepository.findByIdentifier(identifier);
        if (brand == null || Boolean.TRUE.equals(brand.getDeleted())) {
            throw new ResourceNotFoundException("Brand not found: " + identifier);
        }
        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public List<BrandDto> findActiveBrands() {

        List<Brand> list = brandRepository.findByDeletedFalse();
        List<BrandDto> result = new ArrayList<>();

        for (Brand b : list) {
            if (Boolean.TRUE.equals(b.getStatus())) {
                result.add(modelMapper.map(b, BrandDto.class));
            }
        }
        return result;
    }

    @Override
    public BrandDto toggleStatus(String identifier) {

        Brand brand = brandRepository.findByIdentifier(identifier);

        if (brand == null || Boolean.TRUE.equals(brand.getDeleted())) {
            throw new ResourceNotFoundException("Brand not found: " + identifier);
        }

        brand.setStatus(!Boolean.TRUE.equals(brand.getStatus()));
        setModifiedDetails(brand);
        Brand saved = brandRepository.save(brand);
        BrandDto dto = modelMapper.map(saved, BrandDto.class);
        dto.setSuccess(true);
        dto.setMessage("Status updated successfully");
        return dto;
    }
}