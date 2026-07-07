package com.ust.pos.product.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Product;
import com.ust.pos.modell.ProductRepository;
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
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends BaseService implements ProductService {

    public static final RuntimeException PRODUCT_NOT_FOUND = new RuntimeException("Product not found");

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    @Override
    public ProductDto findByIdentifier(String identifier) {
        Product product = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (product == null) {
            throw new ResourceNotFoundException("Product with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);

        if (existingProduct != null) {
            if (Boolean.TRUE.equals(existingProduct.getDeleted())) {
                productDto.setMessage("Product with Identifier " + identifier + " already exists (Soft-Deleted)");
                productDto.setSuccess(false);
                return productDto;
            }

            productDto.setMessage("Product with identifier - " + identifier + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        if (product.getStatus() == null) {
            product.setStatus(true);
        }
        setCreatedDetails(product);
        productRepository.save(product);
        return productDto;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);

        if (existingProduct == null) {
            productDto.setMessage("Shelf with identifier - " + identifier + " not found");
            productDto.setSuccess(false);
            return productDto;
        }

        String originalCreatedBy = existingProduct.getCreatedBy();
        java.time.LocalDateTime originalCreatedOn = existingProduct.getCreatedOn();

        modelMapper.map(productDto, existingProduct);

        existingProduct.setCreatedBy(originalCreatedBy);
        existingProduct.setCreatedOn(originalCreatedOn);

        setModifiedDetails(existingProduct);
        productRepository.save(existingProduct);
        return productDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Product product = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (product != null) {
            softDelete(product);
            setModifiedDetails(product);
            productRepository.save(product);
        }
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> productPage = productRepository.findAllByDeletedFalse(pageable);

        WsDto<ProductDto> productWsDto = new WsDto<>();
        productWsDto.setDtoList(modelMapper.map(productPage.getContent(), listType));
        productWsDto.setTotalRecords(productPage.getTotalElements());
        productWsDto.setTotalPage(productPage.getTotalPages());
        productWsDto.setSizePerPage(pageable.getPageSize());
        productWsDto.setPage(pageable.getPageNumber());

        return productWsDto;
    }

    @Override
    @Transactional
    public ProductDto toggleStatus(String identifier) {
        Product product = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (product == null) {
            throw new NoSuchElementException("Product not found with identifier: " + identifier);
        }
        Boolean currentStatus = product.getStatus();
        product.setStatus(currentStatus == null ? Boolean.TRUE : !currentStatus);
        setModifiedDetails(product);
        Product saved = productRepository.save(product);
        return modelMapper.map(saved, ProductDto.class);
    }

    @Override
    public List<ProductDto> findAllActive() {
        return productRepository.findByStatusTrueAndDeletedFalse()
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }

    @Override
    public WsDto<ProductDto> findAll(Specification<Product> example, Pageable pageable) {

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> page = productRepository.findAll(example, pageable);

        WsDto<ProductDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPage(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}