package com.ust.pos.product.service.impl;

import com.ust.pos.api.BaseService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifierAndDeletedFalse(identifier);
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
        Product existingProduct = productRepository.findByIdentifierAndDeletedFalse(identifier);
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
        Product product = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (product != null) {
            product.setDeleted(true);
            productRepository.save(product);
        }
    }

    @Override
    public List<ProductDto> findAll() {
        Type listOfType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findByDeletedFalse(), listOfType);
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifierAndDeletedFalse(identifier), ProductDto.class);
    }


    @Override
    public Page<ProductDto> findAll(String search, Pageable pageable) {
        Page<Product> products;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Product> specification =
                    buildGlobalSearchSpec(Product.class, search);
            products = productRepository.findAll(specification, pageable);
        } else {
            products = productRepository.findByDeletedFalse(pageable);
        }

        return products.map(product ->
                modelMapper.map(product, ProductDto.class));
    }

    @Override
    public void toggleStatus(String identifier) {
        Product products = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (products != null) {
            products.setStatus(!products.getStatus());
            productRepository.save(products);
        }
    }
}
