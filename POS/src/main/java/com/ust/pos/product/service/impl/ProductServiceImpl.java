package com.ust.pos.product.service.impl;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.CategoryRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepository categoryRepository;

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
            productDto.setMessage("Product with identifier - " + identifier + " is not found");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        productRepository.save(product);
        return productDto;
    }

    @Override
    public void delete(String identifier) {
        productRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<ProductDto> findAll() {
        Type listOfType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findAll(), listOfType);
    }

    @Override
    public List<ProductDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> productPage = productRepository.findAll(pageable);
        return modelMapper.map(productPage.getContent(), listOfType);
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifier(identifier), ProductDto.class);
    }

    @Override
    public List<ProductDto> listOfCategories() {
        Type listOfType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findBySuperCategoryIsNot(""), listOfType);
    }
}
