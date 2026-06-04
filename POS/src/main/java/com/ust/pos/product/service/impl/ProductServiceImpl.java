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
    public ProductDto findByIdentifier(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        productDto.setIdentifier(productDto.getIdentifier().trim());
        String identifier = productDto.getIdentifier();
        String name = productDto.getName();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        Product existingName = productRepository.findByName(name);
        if (existingProduct != null) {
            productDto.setMessage("Product with identifier - " + identifier + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }
        if (existingName != null) {
            productDto.setMessage("Product with skuCode - " + name + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        productRepository.save(product);
        return productDto;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct == null) {
            productDto.setMessage("Product with identifier - " + identifier + " not found");
            productDto.setSuccess(false);
            return productDto;
        }
        modelMapper.map(productDto, existingProduct);
        productRepository.save(existingProduct);
        return productDto;
    }

    @Override
    public boolean delete(String identifier) {
        productRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public List<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> productPage = productRepository.findAll(pageable);
        return modelMapper.map(productPage.getContent(), listType);
    }

    @Override
    public ProductDto toggleStatus(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        product.setStatus(!product.isStatus());
        productRepository.save(product);
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public List<ProductDto> findIfTrue() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findByStatusIsTrue(), listType);
    }
}