package com.ust.pos.product.service.impl;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.modell.Product;
import com.ust.pos.modell.ProductRepository;
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

    public static final RuntimeException PRODUCT_NOT_FOUND = new RuntimeException("Product not found");

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifier(identifier), ProductDto.class
        );
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);

        if (existingProduct != null) {
            productDto.setMessage("Shelf with identifier - " + identifier + " already exists");
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
            productDto.setMessage(
                    "Shelf with identifier - " + identifier + " not found");
            productDto.setSuccess(false);
            return productDto;
        }
        modelMapper.map(productDto, existingProduct);
        productRepository.save(existingProduct);
        return productDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        productRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> productPage = productRepository.findAll(pageable);
        return modelMapper.map(productPage.getContent(), listType);
    }

    @Override
    @Transactional
    public void toggleStatus(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);

        if (product == null) {
            throw PRODUCT_NOT_FOUND;
        }
        Boolean currentStatus = product.getStatus();

        if (currentStatus == null) {
            product.setStatus(Boolean.TRUE);
        } else {
            product.setStatus(!currentStatus);
        }
        productRepository.save(product);
    }

    @Override
    public List<ProductDto> findAllActive() {
        return productRepository.findByStatusTrue()
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }
}