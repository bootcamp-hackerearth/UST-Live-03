package com.ust.pos.stock.service.impl;

import com.ust.pos.dto.StockDto;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.stock.service.StockService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public StockDto createStock(StockDto stockDto) {
        var product = productRepository.findById(stockDto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        var warehouse = warehouseRepository.findById(stockDto.getWarehouseId()).orElseThrow(() -> new RuntimeException("Warehouse not found"));
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
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto updateStockQuantity(Long stockId, Integer quantity) {
        StockDto dto = new StockDto();
        stockRepository.findById(stockId).ifPresentOrElse(stock -> {
            stock.setQuantity(quantity);
            productRepository.findById(stock.getProductId()).ifPresent(product -> {
                stock.setProductName(product.getProductName());
                stock.setIdentifier(product.getIdentifier());
            });
            warehouseRepository.findById(stock.getWarehouseId()).ifPresent(warehouse -> stock.setWarehouseName(warehouse.getName()));
            stockRepository.save(stock);
            modelMapper.map(stock, dto);
        }, () -> {
            dto.setSuccess(false);
            dto.setMessage("Stock not found");
        });
        return dto;
    }

    @Override
    public StockDto getStock(Long productId, Long warehouseId) {
        StockDto dto = new StockDto();
        stockRepository.findByProductIdAndWarehouseId(productId, warehouseId).ifPresentOrElse(stock -> {
            modelMapper.map(stock, dto);
            productRepository.findById(stock.getProductId()).ifPresent(product -> {
                dto.setProductName(product.getProductName());
                dto.setIdentifier(product.getIdentifier());
            });
            warehouseRepository.findById(stock.getWarehouseId()).ifPresent(warehouse ->
                    dto.setWarehouseName(warehouse.getName()));
        }, () -> {
            dto.setSuccess(false);
            dto.setMessage("Stock not found");
        });
        return dto;
    }

    @Override
    public List<StockDto> findAll(Pageable pageable) {
        Page<Stock> stockPage = stockRepository.findAll(pageable);
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
        if (!stockRepository.existsById(stockId)) {
            return false;
        }
        stockRepository.deleteById(stockId);
        return true;
    }

    @Override
    public void toggleStatus(Long stockId) {
        stockRepository.findById(stockId).ifPresent(stock -> {
            stock.setStatus(!stock.isStatus());
            stockRepository.save(stock);
        });
    }
}
