package com.ust.pos.product.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl extends BaseService implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct != null) {
            if (Boolean.TRUE.equals(existingProduct.getDeleted())) {
                productDto.setMessage("Product - " + identifier + " was deleted and cannot be recreated");
            } else {
                productDto.setMessage("Product with identifier - " + identifier + " already exists");
            }
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        setCreatedDetails(product);
        productRepository.save(product);
        productDto.setSuccess(true);
        return productDto;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingProduct == null) {
            productDto.setMessage("Product - " + identifier + " not found");
            productDto.setSuccess(false);
            return productDto;
        }
        String createdBy = existingProduct.getCreatedBy();
        LocalDateTime createdOn = existingProduct.getCreatedOn();
        modelMapper.map(productDto, existingProduct);
        existingProduct.setCreatedBy(createdBy);
        existingProduct.setCreatedOn(createdOn);
        setModifiedDetails(existingProduct);
        productRepository.save(existingProduct);
        productDto.setSuccess(true);
        return productDto;
    }

    @Override
    public void delete(String identifier) {
        Product product = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (product != null) {
            softDelete(product);
            setModifiedDetails(product);
            productRepository.save(product);
        }
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> productPage = productRepository.findByDeletedFalse(pageable);
        WsDto<ProductDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(productPage.getContent(), listType));
        wsDto.setTotalRecords(productPage.getTotalElements());
        wsDto.setTotalPages(productPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(
                productRepository.findByIdentifierAndDeletedFalse(identifier),
                ProductDto.class
        );
    }

    @Override
    public void updateStatus(String identifier, boolean status) {
        Product product = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (product != null) {
            product.setStatus(status);
            setModifiedDetails(product);
            productRepository.save(product);
        }
    }

    @Override
    public List<ProductDto> findAllActive() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(
                productRepository.findByStatusAndDeletedFalse(true),
                listType
        );
    }
}