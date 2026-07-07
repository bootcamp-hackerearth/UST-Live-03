package com.ust.pos.product.service.impl;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.service.BaseService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl extends BaseService implements ProductService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
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
        Product product = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (product != null) {
            product.setDeleted(true);
            productRepository.save(product);
        }
    }

    @Override
    public List<ProductDto> findAll() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findByDeletedFalse(), listType);
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifierAndDeletedFalse(identifier), ProductDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Product products = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (products != null) {
            products.setStatus(!products.isStatus());
            productRepository.save(products);
        }
    }

    @Override
    public Page<ProductDto> findAll(Pageable pageable, String search) {
        Page<Product> products;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Product> specification = buildGlobalSearchSpec(Product.class, search);

            List<Product> filteredProducts = productRepository.findAll(specification, pageable)
                    .getContent()
                    .stream()
                    .filter(product -> !product.isDeleted())
                    .toList();

            products = new PageImpl<>(filteredProducts, pageable, filteredProducts.size());

        } else {
            products = productRepository.findByDeletedFalse(pageable);
        }

        return products.map(product -> modelMapper.map(product, ProductDto.class));
    }
}