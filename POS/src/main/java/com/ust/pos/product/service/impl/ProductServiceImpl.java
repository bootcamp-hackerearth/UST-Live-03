package com.ust.pos.product.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl extends BaseService implements ProductService {

    private static final String CONST_PRODUCT = "Product ";
    private static final String DELETED_MESSAGE =
            " has been deleted. Please contact the administrator.";

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final PriceService priceService;
    private final StockRepository stockRepository;

    public ProductServiceImpl(ProductRepository productRepository, PriceService priceService, StockRepository stockRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.priceService = priceService;
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PaginationResponseDto<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();

        Page<Product> productPage = productRepository.findByIsDeletedFalse(pageable);
        List<ProductDto> productDtoList = modelMapper.map(productPage.getContent(), listType);

        productDtoList.forEach(this::enrichProduct);

        PaginationResponseDto<ProductDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(productDtoList);
        paginationResponseDto.setPage(productPage.getNumber());
        paginationResponseDto.setSizePerPage(productPage.getSize());
        paginationResponseDto.setTotalPages(productPage.getTotalPages());
        paginationResponseDto.setTotalRecords(productPage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public List<ProductDto> findByStatusTrue() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        List<ProductDto> productDtoList = modelMapper.map(productRepository.findByStatusTrue(), listType);

        productDtoList.forEach(this::enrichProduct);
        return productDtoList;
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct != null) {
            if (existingProduct.isDeleted()) {
                productDto.setMessage(CONST_PRODUCT + identifier + DELETED_MESSAGE);
                productDto.setSuccess(false);
                return productDto;
            }
            productDto.setMessage(CONST_PRODUCT + identifier + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        setCreatedDetails(product);
        productRepository.save(product);
        productDto.setMessage("Successfully added the product");
        productDto.setSuccess(true);
        return productDto;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) return null;

        ProductDto dto = modelMapper.map(product, ProductDto.class);

        enrichProduct(dto);
        return dto;
    }

    @Transactional
    @Override
    public ProductDto update(ProductDto productDto) {
        Optional<Product> productOptional =
                productRepository.findById(productDto.getId());

        if (productOptional.isEmpty()) {
            productDto.setMessage(
                    "Product - " + productDto.getIdentifier() + " not found"
            );
            productDto.setSuccess(false);
            return productDto;
        }

        Product existingProduct = productOptional.get();
        if (existingProduct.isDeleted()) {
            productDto.setMessage(
                    CONST_PRODUCT + existingProduct.getIdentifier()
                            + DELETED_MESSAGE
            );
            productDto.setSuccess(false);
            return productDto;
        }

        String productName = productDto.getIdentifier();
        boolean isProductNameChanged =
                !productName.equalsIgnoreCase(
                        existingProduct.getIdentifier()
                );

        if (isProductNameChanged) {
            Product duplicateProduct =
                    productRepository.findByIdentifier(productName);

            if (duplicateProduct != null) {

                if (duplicateProduct.isDeleted()) {
                    productDto.setMessage(
                            CONST_PRODUCT + productName
                                    + DELETED_MESSAGE
                    );
                    productDto.setSuccess(false);
                    return productDto;
                }

                productDto.setMessage(
                        CONST_PRODUCT + productName + " already exists"
                );
                productDto.setSuccess(false);
                return productDto;
            }
        }

        modelMapper.map(productDto, existingProduct);
        setModifiedDetails(existingProduct);
        productRepository.save(existingProduct);

        productDto.setMessage("Product successfully edited");
        productDto.setSuccess(true);

        return productDto;
    }

    @Override
    @Transactional
    public ProductDto updateStatus(String identifier, boolean status) {
        ProductDto response = new ProductDto();

        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) {
            response.setSuccess(false);
            response.setMessage("Product not found");
            return response;
        }
        setModifiedDetails(product);
        product.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        softDelete(product);
        setModifiedDetails(product);
        productRepository.save(product);
    }

    private void enrichProduct(ProductDto productDto) {
        if (productDto == null || productDto.getIdentifier() == null) {
            return;
        }

        PriceDto sellingPriceDto =
                priceService.findByIdentifier(
                        productDto.getIdentifier() + "Selling"
                );
        if (sellingPriceDto != null) {
            productDto.setSellingPrice(sellingPriceDto.getValue());
        }

        PriceDto mrpDto =
                priceService.findByIdentifier(
                        productDto.getIdentifier() + "Mrp"
                );
        if (mrpDto != null) {
            productDto.setMrp(mrpDto.getValue());
        }

        Stock stock =
                stockRepository.findByProduct(
                        productDto.getIdentifier()
                );
        if (stock != null) {
            productDto.setStockQuantity(stock.getQuantity());
        } else {
            productDto.setStockQuantity(0L);
        }
    }

    @Override
    public List<ProductDto> searchProduct(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Product> products = productRepository.searchActiveProducts(query);
        List<ProductDto> productDtoList = products.stream().map(p -> modelMapper.map(p, ProductDto.class)).toList();

        productDtoList.forEach(this::enrichProduct);
        return productDtoList;
    }
}
