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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl extends BaseService implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier().trim();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct != null) {
            if (existingProduct.isDeleted()) {
                productDto.setMessage("Product with identifier " + identifier + " has been soft deleted. (Rollback by changing status)");
                productDto.setSuccess(false);
                return productDto;
            }
            productDto.setMessage("Product with identifier - " + identifier + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        setCreatedDetails(product);
        productRepository.save(product);
        productDto.setSuccess(true);
        productDto.setMessage("Product created successfully");
        return productDto;
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        WsDto<ProductDto> wsDto = new WsDto<>();
        if (pageable == null) {
            List<ProductDto> productDtoList = modelMapper.map(productRepository.findByDeletedFalse(pageable), listType);
            wsDto.setDtoList(productDtoList);
            wsDto.setTotalRecords(productDtoList.size());
            return wsDto;
        }
        Page<Product> productPage = productRepository.findByDeletedFalse(pageable);
        wsDto.setDtoList(modelMapper.map(productPage.getContent(), listType));
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setTotalPages(productPage.getTotalPages());
        wsDto.setTotalRecords(productPage.getTotalElements());
        return wsDto;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifier(identifier), ProductDto.class);
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        softDelete(product);
        setModifiedDetails(product);
        productRepository.save(product);
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        Product existingProduct = productRepository.findByIdentifier(productDto.getIdentifier());
        if (existingProduct == null) {
            productDto.setMessage("Product with identifier - " + productDto.getIdentifier() + " not found");
            productDto.setSuccess(false);
            return productDto;
        }
        modelMapper.map(productDto, existingProduct);
        setModifiedDetails(existingProduct);
        productRepository.save(existingProduct);
        productDto.setSuccess(true);
        productDto.setMessage("Product updated successfully");
        return productDto;
    }

    @Override
    public WsDto<ProductDto> findAll(Specification<Product> specification,
                                     Pageable pageable) {

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();

        Page<Product> page =
                productRepository.findAll(specification, pageable);

        WsDto<ProductDto> wsDto = new WsDto<>();

        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}