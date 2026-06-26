package com.ust.pos.product.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ProductServiceImpl extends BaseService implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ModelMapper modelMapper) {

        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        Product existingProduct = productRepository.findByIdentifier(productDto.getIdentifier());
        if (existingProduct != null) {
            if (existingProduct.isDeleted()) {
                productDto.setMessage("Product with identifier - " + productDto.getIdentifier() + " has been soft deleted. (Rollback by changing status)");
                productDto.setSuccess(false);
                return productDto;
            }
            productDto.setMessage("Stock with identifier - " + productDto.getIdentifier() + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        setCreatedDetails(product);
        productRepository.save(product);
        return productDto;
    }

    @Override
    public PaginationResponseDto<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {}.getType();
        PaginationResponseDto<ProductDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Product> products = productRepository.findAll();
            response.setDtoList(modelMapper.map(products, listType));
            response.setTotalRecords(products.size());
            response.setTotalPages(1);
            response.setSizePerPage(products.size());
            response.setPage(0);
        } else {
            Page<Product> productPage = productRepository.findByDeletedFalse(pageable);
            response.setDtoList(modelMapper.map(productPage.getContent(), listType));
            response.setTotalRecords(productPage.getTotalElements());
            response.setTotalPages(productPage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifier(identifier), ProductDto.class);
    }

    @Transactional
    @Override
    public void deleteByIdentifier(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) {
            throw new EntityNotFoundException("Product not found");
        }
        softDelete(product);
        setModifiedDetails(product);
        productRepository.save(product);
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        Product existingProduct = productRepository.findByIdentifier(productDto.getIdentifier());
        if (existingProduct == null) {
            productDto.setMessage("Product with identifier - " + productDto.getIdentifier() + "not found");
            productDto.setSuccess(false);
            return productDto;
        }
        modelMapper.map(productDto, existingProduct);
        setModifiedDetails(existingProduct);
        productRepository.save(existingProduct);
        return productDto;
    }

    @Override
    public ProductDto toggleStatus(String identifier, boolean status) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) {
            ProductDto dto = new ProductDto();
            dto.setSuccess(false);
            dto.setMessage("Product not found");
            return dto;
        }
        product.setStatus(!product.isStatus());
        setModifiedDetails(product);
        productRepository.save(product);
        ProductDto dto = modelMapper.map(product, ProductDto.class);
        dto.setSuccess(true);
        dto.setMessage("Status updated successfully");
        return dto;
    }
}