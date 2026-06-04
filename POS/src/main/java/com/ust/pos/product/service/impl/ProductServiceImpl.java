package com.ust.pos.product.service.impl;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDto save(ProductDto productDto) {
        if (productRepository.findByIdentifier(productDto.getIdentifier()) != null) {
            productDto.setSuccess(false);
            productDto.setMessage("Product already exists");
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        product.setCategories(productDto.getCategories() == null ? List.of() : productDto.getCategories());
        productRepository.save(product);
        productDto.setSuccess(true);
        return productDto;
    }

    @Override
    public List<ProductDto> findAll(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        Type type = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productPage.getContent(), type);
    }

    @Override
    public List<ProductDto> findAllActive() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findAllByStatus(true), listType);
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) return null;
        ProductDto dto = modelMapper.map(product, ProductDto.class);
        dto.setCategories(product.getCategories());
        return dto;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        Product existing = productRepository.findByIdentifier(productDto.getIdentifier());
        if (existing == null) {
            productDto.setSuccess(false);
            productDto.setMessage("Product not found");
            return productDto;
        }
        existing.setBrand(productDto.getBrand());
        existing.setModel(productDto.getModel());
        existing.setName(productDto.getName());
        existing.setCategories(productDto.getCategories() == null ? List.of() : productDto.getCategories());
        productRepository.save(existing);
        productDto.setSuccess(true);
        return productDto;
    }

    @Override
    public ProductDto toggleStatus(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        product.setStatus(!product.isStatus());
        productRepository.save(product);
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public boolean delete(String identifier) {
        productRepository.deleteByIdentifier(identifier);
        return true;
    }
}
