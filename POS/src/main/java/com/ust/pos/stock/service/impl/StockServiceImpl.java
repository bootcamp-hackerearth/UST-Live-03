package com.ust.pos.stock.service.impl;

import com.ust.pos.dto.StockDto;
import com.ust.pos.modell.*;
import com.ust.pos.stock.service.StockService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class StockServiceImpl implements StockService {

    public static final RuntimeException STOCK_EXISTS_EXCEPTION =
            new RuntimeException("Stock already exists for this Product and Warehouse");
    public static final RuntimeException PRODUCT_WAREHOUSE_REQUIRED_EXCEPTION =
            new RuntimeException("Product and Warehouse are required");
    public static final String STOCK_NOT_FOUND = "Stock not found";

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new RuntimeException(STOCK_NOT_FOUND));
        return mapToDto(stock);
    }

    @Override
    public StockDto findById(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(STOCK_NOT_FOUND));
        return mapToDto(stock);
    }

    @Override
    public StockDto save(StockDto stockDto) {

        if (stockDto.getProductId() == null || stockDto.getWarehouseId() == null) {
            throw PRODUCT_WAREHOUSE_REQUIRED_EXCEPTION;
        }
        Product product = productRepository.findById(stockDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(stockDto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        if (stockRepository.findByProductIdAndWarehouseId(
                stockDto.getProductId(),
                stockDto.getWarehouseId()
        ).isPresent()) {
            throw STOCK_EXISTS_EXCEPTION;
        }
        Stock stock = new Stock();
        stock.setProductId(product.getId());
        stock.setWarehouseId(warehouse.getId());
        stock.setProductIdentifier(product.getIdentifier());
        stock.setWarehouseIdentifier(warehouse.getIdentifier());
        stock.setIdentifier("STK-" + product.getIdentifier() + "-" + warehouse.getIdentifier());
        stock.setQuantity(stockDto.getQuantity());
        stock.setMinimumStock(stockDto.getMinimumStock());
        stock.setStatus(stockDto.getQuantity() > stockDto.getMinimumStock());
        Stock saved = stockRepository.save(stock);
        return mapToDto(saved);
    }

    @Override
    public StockDto update(StockDto stockDto) {
        Stock stock = stockRepository.findById(stockDto.getId())
                .orElseThrow(() -> new RuntimeException(STOCK_NOT_FOUND));

        if (stockDto.getProductId() != null &&
                !stockDto.getProductId().equals(stock.getProductId())) {
            Product product = productRepository.findById(stockDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            stock.setProductId(product.getId());
            stock.setProductIdentifier(product.getIdentifier());
        }

        if (stockDto.getWarehouseId() != null &&
                !stockDto.getWarehouseId().equals(stock.getWarehouseId())) {
            Warehouse warehouse = warehouseRepository.findById(stockDto.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found"));
            stock.setWarehouseId(warehouse.getId());
            stock.setWarehouseIdentifier(warehouse.getIdentifier());
        }
        stock.setQuantity(stockDto.getQuantity());
        stock.setMinimumStock(stockDto.getMinimumStock());
        stock.setStatus(stockDto.getQuantity() > stockDto.getMinimumStock());

        return mapToDto(stockRepository.save(stock));
    }

    @Override
    public void delete(Long id) {
        stockRepository.deleteById(id);
    }

    @Override
    public List<StockDto> findAll(Pageable pageable) {
        Page<Stock> stockPage = stockRepository.findAll(pageable);
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        return modelMapper.map(stockPage.getContent(), listType);
    }

    private StockDto mapToDto(Stock stock) {
        StockDto dto = new StockDto();

        dto.setId(stock.getId());
        dto.setIdentifier(stock.getIdentifier());
        dto.setQuantity(stock.getQuantity());
        dto.setMinimumStock(stock.getMinimumStock());
        dto.setStatus(stock.isStatus());

        dto.setProductId(stock.getProductId());
        dto.setWarehouseId(stock.getWarehouseId());
        dto.setProductIdentifier(stock.getProductIdentifier());
        dto.setWarehouseIdentifier(stock.getWarehouseIdentifier());

        return dto;
    }

}