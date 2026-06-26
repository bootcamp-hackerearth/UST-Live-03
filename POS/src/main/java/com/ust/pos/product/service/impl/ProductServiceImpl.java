package com.ust.pos.product.service.impl;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

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
        Product existingProduct =
                productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingProduct != null) {
            productDto.setMessage("Brand with identifier - " + identifier + " already exists");
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
        Product product = productRepository.findByIdentifier(identifier);

        if (product != null) {
            product.setDeleted(true);
            productRepository.save(product);
        }
    }

    @Override
    public List<ProductDto> findAll() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findAll(), listType);
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        Product product = productRepository.findByIdentifierAndDeletedFalse(identifier.trim());
        return product == null ? null : modelMapper.map(product, ProductDto.class);
    }

    @Override
    public Page<ProductDto> findAll(Pageable pageable, String search) {
        Page<Product> productPage;
        if (search != null && !search.trim().isEmpty()) {
            productPage =
                    productRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                            search,
                            pageable
                    );
        } else {
            productPage = productRepository.findByDeletedFalse(pageable);
        }

        return productPage.map(product ->
                modelMapper.map(product, ProductDto.class));
    }

    @Override
    public void toggleStatus(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product != null) {
            product.setStatus(!product.isStatus());
            productRepository.save(product);
        }
    }
}