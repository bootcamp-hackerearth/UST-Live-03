package com.ust.pos.product.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ProductServiceImpl extends CommonService implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) {
            throw new ResourceNotFoundException("Product with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        productDto.setIdentifier(productDto.getIdentifier().trim());
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct != null) {
            if (existingProduct.isDeleted()) {
                productDto.setMessage("Node with identifier " + identifier + " was previously deleted. " +
                        "Please contact backend team to restore."
                );
                productDto.setSuccess(false);
                return productDto;
            }
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
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct == null) {
            productDto.setMessage("Product with identifier - " + identifier + " not found");
            productDto.setSuccess(false);
            return productDto;
        }
        modelMapper.map(productDto, existingProduct);
        setAuditFields(existingProduct, false);
        productRepository.save(existingProduct);
        return productDto;
    }

    @Override
    public boolean delete(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) return false;
        softDelete(product);
        setAuditFields(product, false);
        productRepository.save(product);
        return true;
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> productPage = productRepository.findByDeletedFalse(pageable);

        WsDto<ProductDto> productWsDto = new WsDto<>();
        productWsDto.setDtoList(modelMapper.map(productPage.getContent(), listType));
        productWsDto.setTotalRecords(productPage.getTotalElements());
        productWsDto.setTotalPages(productPage.getTotalPages());
        productWsDto.setSizePerPage(pageable.getPageSize());
        productWsDto.setPage(pageable.getPageNumber());

        return productWsDto;
    }

    @Override
    public ProductDto toggleStatus(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        product.setStatus(!product.isStatus());
        setAuditFields(product, false);
        productRepository.save(product);
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public List<ProductDto> findIfTrue() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }

    @Override
    public WsDto<ProductDto> findAll(Specification<Product> example, Pageable pageable) {

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> page = productRepository.findAll(example, pageable);

        WsDto<ProductDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}