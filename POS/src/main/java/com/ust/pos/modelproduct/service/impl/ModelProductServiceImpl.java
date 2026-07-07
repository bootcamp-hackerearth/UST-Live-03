package com.ust.pos.modelproduct.service.impl;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.ModelProduct;
import com.ust.pos.model.ModelProductRepository;
import com.ust.pos.modelproduct.service.ModelProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ModelProductServiceImpl implements ModelProductService {

    private final ModelMapper modelMapper;

    private final ModelProductRepository modelProductRepository;

    public ModelProductServiceImpl(ModelMapper modelMapper, ModelProductRepository modelProductRepository) {
        this.modelMapper = modelMapper;
        this.modelProductRepository = modelProductRepository;
    }

    @Override
    public ModelProductDto save(ModelProductDto modelProductDto) {
        String identifier = modelProductDto.getIdentifier();
        ModelProduct existingmodel = modelProductRepository.findByIdentifierAndDeletedFalse(identifier);
        if(existingmodel != null)
        {
            modelProductDto.setMessage("Model - "+identifier+" already exists");
            modelProductDto.setSuccess(false);
            return modelProductDto;
        }
        ModelProduct modelProduct = modelMapper.map(modelProductDto, ModelProduct.class);
        modelProduct.setDeleted(false);
        modelProductRepository.save(modelProduct);
        return modelProductDto;
    }

    @Override
    public ModelProductDto update(ModelProductDto modelProductDto) {
        String identifier = modelProductDto.getIdentifier();
        Optional<ModelProduct> optionalModelProduct = modelProductRepository.findById(modelProductDto.getId());
        if(optionalModelProduct.isEmpty()) {
            modelProductDto.setSuccess(false);
            return modelProductDto;
        }

        else{
            ModelProduct existingmodel = optionalModelProduct.get();
                if (!identifier.equals(existingmodel.getIdentifier()) && modelProductRepository.findByIdentifierAndDeletedFalse(identifier) != null) {
                    modelProductDto.setSuccess(false);
                    modelProductDto.setMessage("Model Already Exists");
                    return modelProductDto;
                }
                else{
                    modelMapper.map(modelProductDto , existingmodel);
                    modelProductRepository.save(existingmodel);
                    modelProductDto.setSuccess(true);
                }
            return modelProductDto;

        }
    }

    @Override
    public ModelProductDto findByIdentifier(String identifier) {
        return modelMapper.map(modelProductRepository.findByIdentifierAndDeletedFalse(identifier), ModelProductDto.class);
    }

    @Override
    public WsDto<ModelProductDto> findAll(Pageable pageable)
    {
        Type listtype = new TypeToken<List<ModelProductDto>>(){}.getType();
        Page<ModelProduct> modelProductPage = modelProductRepository.findByDeletedFalse(pageable);

        WsDto<ModelProductDto> modelProductDtoWsDto = new WsDto<>();
        modelProductDtoWsDto.setDtoList(modelMapper.map(modelProductPage.getContent(), listtype));
        modelProductDtoWsDto.setTotalRecords(modelProductPage.getTotalElements());
        modelProductDtoWsDto.setSizePerPage(pageable.getPageSize());
        modelProductDtoWsDto.setPage(pageable.getPageNumber());
        return modelProductDtoWsDto;
    }

    @Override
    public List<ModelProductDto> findAll() {
        Type listType = new TypeToken<List<ModelProductDto>>(){}.getType();
        return modelMapper.map(modelProductRepository.findByDeletedFalse(), listType);
    }

    @Override
    public void delete(String identifier) {

        ModelProduct modelProduct =
                modelProductRepository.findByIdentifierAndDeletedFalse(identifier);

        if(modelProduct != null)
        {
            modelProduct.setDeleted(true);
            modelProductRepository.save(modelProduct);
        }
    }

    @Override
    public Page<ModelProductDto> findAll(Example<ModelProduct> example, Pageable pageable) {
        Page<ModelProduct> modelProductPage = modelProductRepository.findAll(example, pageable);
        return modelProductPage.map(model -> modelMapper.map(model, ModelProductDto.class));

    }

    @Override
    public void toggleStatus(String identifier) {
        ModelProduct modelProduct = modelProductRepository.findByIdentifierAndDeletedFalse(identifier);
        if (modelProduct != null) {
            modelProduct.setStatus(!modelProduct.getStatus());
            modelProductRepository.save(modelProduct);
        }

    }
}
