package com.ust.pos.product.service.impl;

import com.ust.pos.CommonService;
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
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl extends CommonService implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct != null) {
            if(existingProduct.isDeleted()){
                productDto.setMessage("Product identifier - " + identifier + " not available");
                productDto.setSuccess(false);
                return productDto;
            }
            productDto.setMessage("Product with identifier - " + identifier + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        setAuditFields(product,true);
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
        modelMapper.map(productDto, existingProduct);
        setAuditFields(existingProduct,false);
        productRepository.save(existingProduct);
        return productDto;
    }

    @Override
    public void delete(String identifier) {
        Product product=productRepository.findByIdentifier(identifier);
        softDelete(product);
        setAuditFields(product,false);
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {}.getType();
        Page<Product> productPage = productRepository.findByIsDeletedFalse(pageable);
        WsDto<ProductDto> productDtoWsDto = new WsDto<>();
        productDtoWsDto.setDtoList(modelMapper.map(productPage.getContent(), listType));
        productDtoWsDto.setTotalRecords(productPage.getTotalElements());
        productDtoWsDto.setTotalPages(productPage.getTotalPages());
        productDtoWsDto.setSizePerPage(pageable.getPageSize());
        productDtoWsDto.setPage(pageable.getPageNumber());
        return productDtoWsDto;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        Product product = productRepository.findByIdentifierAndIsDeletedFalse(identifier);
        if (product == null) {
            throw new ResourceNotFoundException("Product with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product != null) {
            Boolean status = product.getStatus();
            if (status == null) {
                status = false;
            }
            product.setStatus(!status);
            setAuditFields(product, false);
            productRepository.save(product);
        }
    }

    @Override
    public List<ProductDto> findAllActive() {
        Type listType = new TypeToken<List<ProductDto>>() {}.getType();
        return modelMapper.map(productRepository.findByStatusTrueAndIsDeletedFalse(), listType);
    }

    @Override
    public WsDto<ProductDto> findAll(Specification<Product> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> page = productRepository.findAll(example, pageable);
        WsDto<ProductDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}
