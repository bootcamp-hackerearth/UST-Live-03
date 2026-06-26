package com.ust.pos.stock.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.stock.service.StockService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StockServiceImpl extends CommonService implements StockService {
    public static final String STOCK_WITH_ID = "Stock with id '";
    public static final String NOT_FOUND = "' not found";
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository, ProductRepository productRepository, WarehouseRepository warehouseRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto createStock(StockDto stockDto) {
        var product = productRepository.findById(stockDto.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product with id '" + stockDto.getProductId() + NOT_FOUND));
        var warehouse = warehouseRepository.findById(stockDto.getWarehouseId()).orElseThrow(() -> new ResourceNotFoundException("Warehouse with id '" + stockDto.getWarehouseId() + NOT_FOUND));
        boolean exists = stockRepository.existsByProductIdAndWarehouseId(stockDto.getProductId(), stockDto.getWarehouseId());
        if (exists) {
            stockDto.setSuccess(false);
            stockDto.setMessage("Stock already exists");
            return stockDto;
        }
        stockDto.setProductName(product.getProductName());
        stockDto.setWarehouseName(warehouse.getName());
        stockDto.setIdentifier(product.getIdentifier());
        Stock stock = modelMapper.map(stockDto, Stock.class);
        stock.setStatus(true);
        setAuditFields(stock, true);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto updateStockQuantity(Long stockId, Integer quantity) {
        StockDto dto = new StockDto();
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new ResourceNotFoundException(STOCK_WITH_ID + stockId + NOT_FOUND));
        stock.setQuantity(quantity);
        productRepository.findById(stock.getProductId()).ifPresent(product -> {
            stock.setProductName(product.getProductName());
            stock.setIdentifier(product.getIdentifier());
        });
        warehouseRepository.findById(stock.getWarehouseId()).ifPresent(warehouse -> stock.setWarehouseName(warehouse.getName()));
        setAuditFields(stock, false);
        stockRepository.save(stock);
        modelMapper.map(stock, dto);
        return dto;
    }

    @Override
    public StockDto getStock(Long productId, Long warehouseId) {
        StockDto dto = new StockDto();
        Stock stock = stockRepository.findByProductIdAndWarehouseId(productId, warehouseId).orElseThrow(() -> new ResourceNotFoundException("Stock with productId '" + productId + "' and warehouseId '" + warehouseId + NOT_FOUND));
        modelMapper.map(stock, dto);
        productRepository.findById(stock.getProductId()).ifPresent(product -> {
            dto.setProductName(product.getProductName());
            dto.setIdentifier(product.getIdentifier());
        });
        warehouseRepository.findById(stock.getWarehouseId()).ifPresent(warehouse -> dto.setWarehouseName(warehouse.getName()));
        return dto;
    }

    @Override
    public List<StockDto> findAll(Pageable pageable) {
        Page<Stock> stockPage = stockRepository.findByDeletedFalse(pageable);
        return stockPage.getContent().stream().map(stock -> {
            StockDto dto = modelMapper.map(stock, StockDto.class);
            productRepository.findById(stock.getProductId()).ifPresent(product -> {
                dto.setProductName(product.getProductName());
                dto.setIdentifier(product.getIdentifier());
            });
            warehouseRepository.findById(stock.getWarehouseId()).ifPresent(warehouse -> dto.setWarehouseName(warehouse.getName()));
            return dto;
        }).toList();
    }

    @Override
    public boolean deleteStock(Long stockId) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new ResourceNotFoundException(STOCK_WITH_ID + stockId + NOT_FOUND));
        softDelete(stock);
        setAuditFields(stock, false);
        stockRepository.save(stock);
        return true;
    }

    @Override
    public void toggleStatus(Long stockId) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new ResourceNotFoundException(STOCK_WITH_ID + stockId + NOT_FOUND));
        stock.setStatus(!stock.isStatus());
        stockRepository.save(stock);
    }
}