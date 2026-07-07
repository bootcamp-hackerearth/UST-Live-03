package com.ust.pos.product.service.impl;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_WITH_IDENTIFIER = "Product with identifier - ";

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public ProductDto findByIdentifier(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) {
            throw new ResourceNotFoundException(PRODUCT_WITH_IDENTIFIER + identifier + " not found");
        }
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);

        if (existingProduct != null) {
            if (Boolean.TRUE.equals(existingProduct.getIsDeleted())) {
                productDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " was deleted. Contact admin for further support or try with a different identifier.");
            } else {
                productDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " already exists");
            }
            productDto.setSuccess(false);
            return productDto;
        }

        Product product = modelMapper.map(productDto, Product.class);
        product.setIsDeleted(false);
        productRepository.save(product);
        return productDto;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);

        if (existingProduct == null) {
            productDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " not found");
            productDto.setSuccess(false);
            return productDto;
        }

        modelMapper.map(productDto, existingProduct);
        productRepository.save(existingProduct);
        return productDto;
    }

    @Override
    public ProductDto delete(String identifier) {
        ProductDto productDto = new ProductDto();
        Product product = productRepository.findByIdentifier(identifier);

        if (product == null) {
            productDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " not found");
            productDto.setSuccess(false);
            return productDto;
        }

        product.setIsDeleted(true);
        product.setStatus(false);
        productRepository.save(product);
        productDto.setSuccess(true);
        productDto.setMessage("Product deleted successfully");
        return productDto;
    }

    @Override
    public PaginatedResponseDto<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> productPage = productRepository.findByIsDeleted(false, pageable);
        List<ProductDto> items = modelMapper.map(productPage.getContent(), listType);
        PaginatedResponseDto<ProductDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public List<ProductDto> findAllActive() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findByStatusAndIsDeleted(true, false), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Product product = productRepository.findByIdentifier(identifier);
        product.setStatus(status);
        productRepository.save(product);
    }

    @Override
    public PaginatedResponseDto<ProductDto> findAll(Specification<Product> example, Pageable pageable) {

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> page = productRepository.findAll(example, pageable);

        PaginatedResponseDto<ProductDto> paginatedResponseDto = new PaginatedResponseDto<>();
        paginatedResponseDto.setItems(modelMapper.map(page.getContent(), listType));
        paginatedResponseDto.setTotalRecords(page.getTotalElements());
        paginatedResponseDto.setTotalPages(page.getTotalPages());
        paginatedResponseDto.setSizePerPage(pageable.getPageSize());
        paginatedResponseDto.setPage(pageable.getPageNumber());

        return paginatedResponseDto;
    }
}