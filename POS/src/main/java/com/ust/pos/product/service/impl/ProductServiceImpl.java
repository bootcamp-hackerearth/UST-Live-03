package com.ust.pos.product.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ProductServiceImpl extends CommonService implements ProductService {
    public static final String PRODUCT_WITH_SKU_CODE = "Product with skuCode - ";
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final PriceRepository priceRepository;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper, PriceRepository priceRepository) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.priceRepository = priceRepository;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) {
            return null;
        }
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        Price price = priceRepository.findByProductId(product.getId());
        if (price != null) {
            productDto.setPrice(modelMapper.map(price, PriceDto.class));
        }
        return productDto;
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        productDto.setIdentifier(productDto.getIdentifier().trim());
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct != null) {
            if (existingProduct.isDeleted()) {
                productDto.setMessage(PRODUCT_WITH_SKU_CODE + identifier + " has been soft deleted.(Rollback by changing status");
                productDto.setSuccess(false);
                return productDto;
            }
            productDto.setMessage(PRODUCT_WITH_SKU_CODE + identifier + " already exists");
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
        String identifier = productDto.getIdentifier().trim();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct == null) {
            productDto.setMessage(PRODUCT_WITH_SKU_CODE + identifier + " not found");
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
        if (product == null) {
            return false;
        }
        softDelete(product);
        setAuditFields(product, false);
        productRepository.save(product);
        return true;
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {
        Page<Product> productPage = productRepository.findByDeletedFalse(pageable);
        List<ProductDto> productDtos = productPage.getContent().stream().map(product -> {
            ProductDto productDto = modelMapper.map(product, ProductDto.class);
            Price price = priceRepository.findByProductId(product.getId());
            if (price != null) {
                productDto.setPrice(modelMapper.map(price, PriceDto.class));
            }
            return productDto;
        }).toList();
        WsDto<ProductDto> wsDto = new WsDto<>();
        wsDto.setDtoList(productDtos);
        wsDto.setTotalRecords(productPage.getTotalElements());
        wsDto.setTotalPages(productPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public ProductDto toggleStatus(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        product.setStatus(!product.isStatus());
        productRepository.save(product);
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public List<ProductDto> findIfTrue() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findByStatusIsTrue(), listType);
    }
}