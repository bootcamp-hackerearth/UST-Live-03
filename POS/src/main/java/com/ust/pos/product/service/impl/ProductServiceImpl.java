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
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);

        if (existingProduct != null) {
            productDto.setMessage("Product with identifier - " + identifier + " already exists");
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
    public void delete(String identifier) {
        productRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<ProductDto> findAll() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findAll(), listType);
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifier(identifier), ProductDto.class);
    }

    @Override
    public List<ProductDto> findAll(Pageable pageable) {
        Type listtype = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> productPage = productRepository.findAll(pageable);
        return modelMapper.map(productPage.getContent(), listtype);
    }
}