package com.ust.pos.brand.service.impl;

import com.ust.pos.CommonService;
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
import java.util.List;

@Service
public class BrandServiceImpl extends CommonService implements BrandService {

    private static final String BRAND_WITH_IDENTIFIER = "Brand with identifier - ";

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    public BrandServiceImpl(BrandRepository brandRepository,
                            ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandDto findByIdentifier(String identifier) {
        Brand brand =brandRepository.findByIdentifier(identifier);
        if(brand==null){
            throw new ResourceNotFoundException("Data Cannot Found");
        }
        return modelMapper.map(brand, BrandDto.class);
    }

    @Override
    public BrandDto save(BrandDto dto) {

        if (dto == null || dto.getIdentifier() == null) {
            throw new IllegalArgumentException("Identifier is required");
        }

        String identifier = dto.getIdentifier();
        Brand existing = brandRepository.findByIdentifier(identifier);

        if (existing != null) {
            if (!existing.isDeleted()) {
                dto.setMessage("Brand with identifier '" + identifier + "' already exists");
                dto.setSuccess(false);
                return dto;
            }

            dto.setMessage("Brand was previously deleted. Please contact backend team to restore.");
            dto.setSuccess(false);
            return dto;
        }

        Brand brand = modelMapper.map(dto, Brand.class);
        setAuditFields(brand, true);

        Brand saved = brandRepository.save(brand);

        BrandDto response = modelMapper.map(saved, BrandDto.class);
        response.setSuccess(true);
        response.setMessage("Brand created successfully");

        return response;
    }

    @Override
    public BrandDto update(BrandDto dto) {

        String identifier = dto.getIdentifier();
        Brand existing = brandRepository.findByIdentifier(identifier);

        if (existing == null) {
            dto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " not found");
            dto.setSuccess(false);
            return dto;
        }

        if (existing.isDeleted()) {
            dto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " was previously deleted. Please contact backend team to restore.");
            dto.setSuccess(false);
            return dto;
        }

        modelMapper.map(dto, existing);
        setAuditFields(existing, false);

        brandRepository.save(existing);

        return dto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        Brand brand = brandRepository.findByIdentifier(identifier);

        if (brand != null) {
            softDelete(brand);
            setAuditFields(brand, false);
            brandRepository.save(brand);
        }
    }

    @Override
    public WsDto<BrandDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        Page<Brand> brandPage = brandRepository.findByDeletedFalse(pageable);

        WsDto<BrandDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(brandPage.getContent(), listType));
        wsDto.setTotalRecords(brandPage.getTotalElements());
        wsDto.setTotalPages(brandPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public List<BrandDto> findIfTrue() {
        Type listType = new TypeToken<List<BrandDto>>() {
        }.getType();
        return modelMapper.map(
                brandRepository.findByStatusIsTrueAndDeletedFalse(),
                listType
        );
    }

    @Override
    public BrandDto toggleStatus(String identifier) {

        Brand brand = brandRepository.findByIdentifier(identifier);

        if (brand == null) {
            BrandDto dto = new BrandDto();
            dto.setMessage(BRAND_WITH_IDENTIFIER + identifier + " not found");
            dto.setSuccess(false);
            return dto;
        }

        brand.setStatus(!brand.isStatus());
        setAuditFields(brand, false);

        brandRepository.save(brand);

        return modelMapper.map(brand, BrandDto.class);
    }
}