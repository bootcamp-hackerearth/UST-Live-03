package com.ust.pos.modelproduct.service.impl;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.model.ModelProduct;
import com.ust.pos.model.ModelProductRepository;
import com.ust.pos.modelproduct.service.ModelProductService;
import com.ust.pos.service.BaseService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ModelProductServiceImpl extends BaseService implements ModelProductService {

    private final ModelMapper modelMapper;
    private final ModelProductRepository modelProductRepository;

    public ModelProductServiceImpl(
            ModelMapper modelMapper,
            ModelProductRepository modelProductRepository
    ) {
        this.modelMapper = modelMapper;
        this.modelProductRepository = modelProductRepository;
    }

    @Override
    public ModelProductDto save(ModelProductDto modelProductDto) {
        String identifier = modelProductDto.getIdentifier();
        ModelProduct existingModel =
                modelProductRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingModel != null) {
            modelProductDto.setMessage("Model - " + identifier + " already exists");
            modelProductDto.setSuccess(false);
            return modelProductDto;
        }

        ModelProduct modelProduct = modelMapper.map(modelProductDto, ModelProduct.class);
        modelProductRepository.save(modelProduct);
        return modelProductDto;
    }

    @Override
    public ModelProductDto update(ModelProductDto modelProductDto) {
        String identifier = modelProductDto.getIdentifier();
        ModelProduct existingModel = modelProductRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingModel == null) {
            modelProductDto.setMessage("Model - " + identifier + " not found");
            modelProductDto.setSuccess(false);
            return modelProductDto;
        }

        ModelProduct modelProduct = modelMapper.map(modelProductDto, ModelProduct.class);
        modelProductRepository.save(modelProduct);
        return modelProductDto;
    }

    @Override
    public ModelProductDto findByIdentifier(String identifier) {
        return modelMapper.map(modelProductRepository.findByIdentifierAndDeletedFalse(identifier), ModelProductDto.class);
    }

    @Override
    public List<ModelProductDto> findAll() {
        Type listType = new TypeToken<List<ModelProductDto>>() {
        }.getType();
        return modelMapper.map(modelProductRepository.findByDeletedFalse(), listType);
    }

    @Override
    public void delete(String identifier) {
        ModelProduct modelProduct = modelProductRepository.findByIdentifierAndDeletedFalse(identifier);

        if (modelProduct != null) {
            modelProduct.setDeleted(true);
            modelProductRepository.save(modelProduct);
        }
    }

    @Override
    public void toggleStatus(String identifier) {
        ModelProduct modelProduct = modelProductRepository.findByIdentifierAndDeletedFalse(identifier);
        if (modelProduct != null) {
            modelProduct.setStatus(!modelProduct.isStatus());
            modelProductRepository.save(modelProduct);
        }
    }

    @Override
    public Page<ModelProductDto> findAll(Pageable pageable, String search) {
        Page<ModelProduct> modelProducts;

        if (search != null && !search.trim().isEmpty()) {
            Specification<ModelProduct> specification = buildGlobalSearchSpec(ModelProduct.class, search);

            List<ModelProduct> filteredModelProducts = modelProductRepository.findAll(specification, pageable)
                    .getContent()
                    .stream()
                    .filter(modelProduct -> !modelProduct.isDeleted())
                    .toList();

            modelProducts = new PageImpl<>(filteredModelProducts, pageable, filteredModelProducts.size());

        } else {
            modelProducts = modelProductRepository.findByDeletedFalse(pageable);
        }

        return modelProducts.map(modelProduct -> modelMapper.map(modelProduct, ModelProductDto.class));
    }
}