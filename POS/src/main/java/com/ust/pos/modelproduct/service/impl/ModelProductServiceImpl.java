package com.ust.pos.modelproduct.service.impl;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.ModelProduct;
import com.ust.pos.model.ModelProductRepository;
import com.ust.pos.modelproduct.service.ModelProductService;
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
@Transactional
public class ModelProductServiceImpl implements ModelProductService {

    @Autowired
    private ModelProductRepository modelProductRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ModelProductDto save(ModelProductDto modelProductDto) {
        ModelProduct existing = modelProductRepository.findByIdentifier(modelProductDto.getIdentifier());
        if (existing != null) {
            modelProductDto.setSuccess(false);
            modelProductDto.setMessage("Price already exists for identifier: " + modelProductDto.getIdentifier());
            return modelProductDto;
        }
        ModelProduct modelProduct = modelMapper.map(modelProductDto, ModelProduct.class);
        modelProductRepository.save(modelProduct);
        return modelProductDto;
    }

    @Override
    public ModelProductDto update(ModelProductDto modelProductDto) {
        ModelProduct existing = modelProductRepository.findByIdentifier(modelProductDto.getIdentifier());
        if (existing == null) {
            modelProductDto.setSuccess(false);
            modelProductDto.setMessage("Price not found for identifier: " + modelProductDto.getIdentifier());
            return modelProductDto;
        }
        modelMapper.map(modelProductDto, existing);
        modelProductRepository.save(existing);
        return modelProductDto;
    }

    @Override
    public ModelProductDto findByIdentifier(String identifier) {
        ModelProduct price = modelProductRepository.findByIdentifier(identifier);
        return modelMapper.map(price, ModelProductDto.class);
    }

    @Override
    public void delete(String identifier) {
        modelProductRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<ModelProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelProductDto>>() {
        }.getType();
        Page<ModelProduct> modelProductPage = modelProductRepository.findAll(pageable);
        WsDto<ModelProductDto> modelProductDtoWsDto = new WsDto<>();
        modelProductDtoWsDto.setDtoList(modelMapper.map(modelProductPage.getContent(), listType));
        modelProductDtoWsDto.setTotalRecords(modelProductPage.getTotalElements());
        modelProductDtoWsDto.setTotalPages(modelProductPage.getTotalPages());
        modelProductDtoWsDto.setSizePerPage(pageable.getPageSize());
        modelProductDtoWsDto.setPage(pageable.getPageNumber());
        return modelProductDtoWsDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        ModelProduct modelProduct = modelProductRepository.findByIdentifier(identifier);
        modelProduct.setStatus(!modelProduct.getStatus());
        modelProductRepository.save(modelProduct);
    }
}
