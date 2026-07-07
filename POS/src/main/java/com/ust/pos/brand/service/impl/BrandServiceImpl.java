package com.ust.pos.brand.service.impl;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    private final ModelMapper modelMapper;

    public BrandServiceImpl(BrandRepository brandRepository, ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifierAndDeletedFalse(identifier);
        if(existingBrand != null)
        {
            brandDto.setMessage("Brand with identifier - "+ identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
        brand.setDeleted(false);
        brandRepository.save(brand);
        return brandDto;
    }

    @Override
    public BrandDto update(BrandDto brandDto) {

        Brand existingBrand =
                brandRepository.findByIdentifierAndDeletedFalse(brandDto.getIdentifier());

        if (existingBrand == null) {
            brandDto.setSuccess(false);
            brandDto.setMessage("Brand not found");
            return brandDto;
        }

        existingBrand.setDescription(brandDto.getDescription());
        existingBrand.setStatus(brandDto.isStatus());

        brandRepository.save(existingBrand);

        return brandDto;
    }

    @Override
    public void delete(String identifier) {
        Brand brand = brandRepository.findByIdentifierAndDeletedFalse(identifier);
        if(brand!=null)
        {
            brand.setDeleted(true);
            brandRepository.save(brand);
        }
    }

    @Override
    public List<BrandDto> findAll()
    {
        Type listtype = new TypeToken<List<BrandDto>>(){}.getType();
        return modelMapper.map(brandRepository.findByDeletedFalse(), listtype);
    }
    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<BrandDto>>(){}.getType();
        Page<Brand> brandPage = brandRepository.findByDeletedFalse(pageable);

        WsDto<BrandDto> brandDtoWsDto = new WsDto<>();
        brandDtoWsDto.setDtoList(modelMapper.map(brandPage.getContent(), listOfType));
        brandDtoWsDto.setTotalRecords(brandPage.getTotalElements());
        brandDtoWsDto.setTotalPage(brandPage.getTotalPages());
        brandDtoWsDto.setSizePerPage(pageable.getPageSize());
        brandDtoWsDto.setPage(pageable.getPageNumber());
        return brandDtoWsDto;
    }

    @Override
    public Page<BrandDto> findAll(Example<Brand> example, Pageable pageable) {
        Page<Brand> brandPage = brandRepository.findAll(example, pageable);
        List<Brand> filtered = brandPage.getContent().stream()
                .filter(b -> !Boolean.TRUE.equals(b.getDeleted()))
                .toList();
        Page<Brand> filteredPage = new PageImpl<>(filtered, pageable, brandPage.getTotalElements());
        return filteredPage.map(brand -> modelMapper.map(brand, BrandDto.class));
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        return modelMapper.map(brandRepository.findByIdentifierAndDeletedFalse(identifier), BrandDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Brand brand = brandRepository.findByIdentifierAndDeletedFalse(identifier);
        if (brand != null) {
            // ✅ toggle status
            brand.setStatus(!brand.getStatus());
            brandRepository.save(brand);
        }

    }
}
