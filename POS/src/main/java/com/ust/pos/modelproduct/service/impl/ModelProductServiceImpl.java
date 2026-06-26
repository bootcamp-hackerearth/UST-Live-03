package com.ust.pos.modelproduct.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.model.ModelProduct;
import com.ust.pos.model.ModelProductRepository;
import com.ust.pos.modelproduct.service.ModelProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ModelProductServiceImpl extends CommonService implements ModelProductService {

    private final ModelMapper modelMapper;
    private final ModelProductRepository modelProductRepository;

    ModelProductServiceImpl(ModelMapper modelMapper, ModelProductRepository modelProductRepository) {
        this.modelMapper = modelMapper;
        this.modelProductRepository = modelProductRepository;
    }

    @Override
    public ModelProductDto save(ModelProductDto modelProductDto) {
        String identifier = modelProductDto.getIdentifier();
        ModelProduct existingmodel = modelProductRepository.
                findByIdentifierAndIsDeleteFalse(identifier);
        if (existingmodel != null) {
            modelProductDto.setMessage("Model - " + identifier + " already exists");
            modelProductDto.setSuccess(false);
            return modelProductDto;
        }
        ModelProduct modelProduct = modelMapper.map(modelProductDto, ModelProduct.class);
        setAuditFields(modelProduct, true);
        modelProductRepository.save(modelProduct);
        return modelProductDto;
    }

    @Override
    public ModelProductDto update(ModelProductDto modelProductDto) {
        String identifier = modelProductDto.getIdentifier();
        ModelProduct existingmodel = modelProductRepository.
                findByIdentifierAndIsDeleteFalse(identifier);
        if (existingmodel == null) {
            modelProductDto.setMessage("Model - " + identifier + " not found");
            modelProductDto.setSuccess(false);
            return modelProductDto;
        }
        modelMapper.map(modelProductDto, existingmodel);
        setAuditFields(existingmodel, false);
        modelProductRepository.save(existingmodel);
        return modelProductDto;
    }

    @Override
    public ModelProductDto findByIdentifier(String identifier) {
        return modelMapper.map(modelProductRepository.
                findByIdentifierAndIsDeleteFalse(identifier), ModelProductDto.class);
    }

    @Override
    public List<ModelProductDto> findAll() {
        Type listType = new TypeToken<List<ModelProductDto>>() {
        }.getType();
        return modelMapper.map(modelProductRepository.findByIsDeleteFalse(), listType);
    }

    @Override
    public void delete(String identifier) {
        ModelProduct modelProduct = modelProductRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (modelProduct != null) {
            modelProduct.setDelete(true);
            setAuditFields(modelProduct, false);
            modelProductRepository.save(modelProduct);
        }
    }

    @Override
    public void updateStatusOnly(String identifier, boolean status) {
        ModelProduct modelProduct = modelProductRepository.
                findByIdentifierAndIsDeleteFalse(identifier);
        modelProduct.setStatus(status);
        setAuditFields(modelProduct, true);
        modelProductRepository.save(modelProduct);
    }

    @Override
    public List<ModelProductDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<ModelProductDto>>() {
        }.getType();
        Page<ModelProduct> modelProductPage = modelProductRepository.findByIsDeleteFalse(pageable);
        return modelMapper.map(modelProductPage.getContent(), listOfType);
    }
}
