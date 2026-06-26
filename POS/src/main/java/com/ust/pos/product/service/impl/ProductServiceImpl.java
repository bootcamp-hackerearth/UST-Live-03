package com.ust.pos.product.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
@Service
public class ProductServiceImpl extends CommonService implements ProductService {
    public static final String PRODUCT_WITH_IDENTIFIER = "Product with identifier - ";
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

            if (Boolean.TRUE.equals(existingProduct.getDeleted())) {
                productDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " has been soft deleted. Restore it by changing status.");
                productDto.setSuccess(false);
                return productDto;
            }

            productDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }

        Product product = modelMapper.map(productDto, Product.class);
        product.setDeleted(false);
        product.setStatus(true);
        setAuditFields(product, true);
        productRepository.save(product);
        return productDto;
    }
    @Override
    public ProductDto update(ProductDto productDto) {
        String identifier =productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct == null) {
           productDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " not found");
           productDto.setSuccess(false);
            return productDto;
        }
        modelMapper.map(productDto, existingProduct);
        setAuditFields(existingProduct,false);
        productRepository.save(existingProduct);
        return productDto;
    }

    @Override
    @Transactional
    public boolean delete(String identifier) {

        Product product =
                productRepository.findByIdentifier(identifier);

        if (product == null) {
            return false;
        }

        softDelete(product);
        setAuditFields(product, false);
        productRepository.save(product);
        return true;
    }

    @Override
    public PageDto<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> productPage = productRepository.findByDeletedFalse(pageable);
        PageDto<ProductDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(productPage.getContent(), listType));
        pageDto.setTotalRecords(productPage.getTotalElements());
        pageDto.setTotalPages(productPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifier(identifier), ProductDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product != null) {
            boolean currentStatus = Boolean.TRUE.equals(product.getStatus());
            product.setStatus(!currentStatus);

            productRepository.save(product);
        }
    }

    @Override
    public List<ProductDto> findActiveProducts() {
        Type listType = new TypeToken<List<ProductDto>>() {}.getType();
        return modelMapper.map(productRepository.findByStatusTrue(),listType);
    }
}
