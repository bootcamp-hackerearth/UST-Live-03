package com.ust.pos.product.service.impl;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public ProductDto save(ProductDto productDto) {
        Product existingProduct = productRepository.findByIdentifier(productDto.getIdentifier());
        if (existingProduct != null) {
            productDto.setMessage("Stock with identifier - " + productDto.getIdentifier() + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        productRepository.save(product);
        return productDto;
    }

    @Override
    public List<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        if (pageable == null) {
            return modelMapper.map(productRepository.findAll(), listType);
        }
        Page<Product> productPage = productRepository.findAll(pageable);
        return modelMapper.map(productPage.getContent(), listType);
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifier(identifier), ProductDto.class);
    }

    @Transactional
    @Override
    public void deleteByIdentifier(String identifier) {
        productRepository.deleteByIdentifier(identifier);
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
        productRepository.save(existingProduct);
        return productDto;
    }

}