package com.ust.pos.product.service.impl;

import com.ust.pos.product.service.ProductService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.service.BaseService;
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

    ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct =
                productRepository.findByIdentifierAndDeletedFalse(identifier);
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
        Product existingProduct =
                productRepository.findByIdentifierAndDeletedFalse(productDto.getIdentifier());
        if (existingProduct == null) {
            productDto.setSuccess(false);
            productDto.setMessage("Product not found");
            return productDto;
        }
        existingProduct.setSupplierId(productDto.getSupplierId());
        existingProduct.setBrand(productDto.getBrand());
        existingProduct.setUnit(productDto.getUnit());
        existingProduct.setCategory(productDto.getCategory());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setStatus(productDto.isStatus());
        productRepository.save(existingProduct);

        productDto.setSuccess(true);
        productDto.setMessage("Updated successfully");

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
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.
                findByIdentifierAndDeletedFalse(identifier), ProductDto.class);
    }

    @Override
    public ProductDto toggleStatus(String identifier) {
        Product product = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (product != null) {
            product.setStatus(!product.isStatus());
            productRepository.save(product);
        }
        return null;
    }

    @Override
    public Page<ProductDto> findAll(Pageable pageable, String search) {
        Page<Product> productPage;
        if (search != null && !search.trim().isEmpty()) {
            Specification<Product> specification = buildGlobalSearchSpec(Product.class, search);
            productPage = productRepository.findAll(specification, pageable);
        } else {
            productPage = productRepository.findByDeletedFalse(pageable);
        }
        return productPage.map(product -> modelMapper.map(product, ProductDto.class));
    }

    @Override
    public List<ProductDto> findAll() {
        Type listOfType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findByDeletedFalse(), listOfType);
    }
}
