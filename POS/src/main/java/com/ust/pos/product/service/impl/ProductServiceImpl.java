package com.ust.pos.product.service.impl;

import com.ust.pos.commonservice.CommonService;
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
public class ProductServiceImpl extends CommonService implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.
                findByIdentifierAndIsDeleteFalse(identifier);
        if (existingProduct != null) {
            productDto.setMessage("Product with identifier - " + identifier + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        setAuditFields(product, true);
        productRepository.save(product);
        return productDto;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.
                findByIdentifierAndIsDeleteFalse(identifier);
        if (existingProduct == null) {
            productDto.setMessage("Product with identifier - " + identifier + " is not found");
            productDto.setSuccess(false);
            return productDto;
        }
        modelMapper.map(productDto, existingProduct);
        setAuditFields(existingProduct, false);
        productRepository.save(existingProduct);
        return productDto;
    }

    @Override
    public void delete(String identifier) {
        Product product = productRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (product != null) {
            product.setDelete(true);
            setAuditFields(product, false);
            productRepository.save(product);
        }
    }

    @Override
    public List<ProductDto> findAll() {
        Type listOfType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findByIsDeleteFalse(), listOfType);
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.
                findByIdentifierAndIsDeleteFalse(identifier), ProductDto.class);
    }

    @Override
    public Page<ProductDto> findAll(Pageable pageable, String search) {
        Page<Product> products;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Product> specification = buildGlobalSearchSpec(Product.class, search);
            products = productRepository.findAll(specification, pageable);
        } else {
            products = productRepository.findByIsDeleteFalse(pageable);
        }

        return products.map(product -> modelMapper.map(product, ProductDto.class));
    }

    @Override
    public void toggleStatus(String identifier) {
        Product products = productRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (products != null) {
            products.setStatus(!products.getStatus());
            setAuditFields(products, false);
            productRepository.save(products);
        }
    }
}
