package com.ust.pos.product.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
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

    private final ModelMapper modelMapper;

    private final ProductRepository productRepository;

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        modelMapper.map(productDto, existingProduct);
        setAuditFields(existingProduct,false);
        productRepository.save(existingProduct);
        return productDto;
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> userPage = productRepository.findByDeletedFalse(pageable);

        WsDto<ProductDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());

        return userWsDto;
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct != null) {
            if(existingProduct.isDeleted()) {
                productDto.setMessage("Product with identifier - " + identifier + "has been soft deleted.(Rollback by changing status");
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
    @Transactional
    public void delete(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        softDelete(product);
        setAuditFields(product,false);
        productRepository.save(product);
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifier(identifier), ProductDto.class);
    }

    @Override
    public ProductDto changeToggleStatus(String identifier, boolean status) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product != null) {
            product.setStatus(status);
            productRepository.save(product);
        }
        return modelMapper.map(product, ProductDto.class);
    }


    @Override
    public List<ProductDto> findActiveStatus() {
        List<Product> allProducts = productRepository.findAll();
        List<Product> activeProducts = allProducts.stream().filter(Product::isStatus).toList();

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(activeProducts, listType);
    }

}
