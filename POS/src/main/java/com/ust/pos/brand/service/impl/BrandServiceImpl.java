package com.ust.pos.brand.service.impl;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.lang.reflect.Type;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    BrandRepository brandRepository;

    @Override
    public BrandDto save(BrandDto brandDto) {
        String identifier = brandDto.getIdentifier();
        Brand existingBrand = brandRepository.findByIdentifier(identifier);
        if (existingBrand != null) {
            brandDto.setMessage("Brand with identifier - " + identifier + " already exists");
            brandDto.setSuccess(false);
            return brandDto;
        }
        Brand brand = modelMapper.map(brandDto, Brand.class);
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
        brandRepository.save(existingBrand);
        return brandDto;
    }


    @Override
    @Transactional
    public void delete(String identifier) {
        brandRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findAll(pageable);

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
        return modelMapper.map(brandRepository.findByIdentifier(identifier), BrandDto.class);
    }

    @Override
    public List<Brand> findActiveBrands() {
        return brandRepository.findByStatus(true);
    }

    @Override
    public BrandDto toggleStatus(String identifier) {
        Brand brands = brandRepository.findByIdentifier(identifier);
        if (brands == null) {
            return null;
        }
        brands.setStatus(!brands.isStatus());
        brandRepository.save(brands);
        return modelMapper.map(brands, BrandDto.class);
    }
}
