package com.ust.pos.modelproduct.service.impl;
import com.ust.pos.CommonService;
import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.ModelProduct;
import com.ust.pos.model.ModelProductRepository;
import com.ust.pos.modelproduct.service.ModelProductService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ModelProductServiceImpl extends CommonService implements ModelProductService {

    private final ModelProductRepository modelProductRepository;
    private final ModelMapper modelMapper;

    public ModelProductServiceImpl(ModelProductRepository modelProductRepository, ModelMapper modelMapper) {
        this.modelProductRepository = modelProductRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ModelProductDto save(ModelProductDto modelProductDto) {
        ModelProduct existing = modelProductRepository.findByIdentifier(modelProductDto.getIdentifier());
        if (existing != null) {
            if(existing.isDeleted()){
                modelProductDto.setMessage("ModelProduct identifier - " + modelProductDto.getIdentifier() + " not available");
                modelProductDto.setSuccess(false);
                return modelProductDto;
            }
            modelProductDto.setSuccess(false);
            modelProductDto.setMessage("Price already exists for identifier: " + modelProductDto.getIdentifier());
            return modelProductDto;
        }
        ModelProduct modelProduct = modelMapper.map(modelProductDto, ModelProduct.class);
        setAuditFields(modelProduct,true);
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
        setAuditFields(existing,false);
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
        ModelProduct modelProduct=modelProductRepository.findByIdentifier(identifier.trim());
        softDelete(modelProduct);
        setAuditFields(modelProduct,false);
    }

    @Override
    public WsDto<ModelProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelProductDto>>() {}.getType();
        Page<ModelProduct> modelProductPage = modelProductRepository.findByIsDeletedFalse(pageable);
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
        setAuditFields(modelProduct,false);
        modelProductRepository.save(modelProduct);
    }

    @Override
    public List<ModelProductDto> findAllActive() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(modelProductRepository.findByStatusTrueAndIsDeletedFalse(), listType);
    }
}
