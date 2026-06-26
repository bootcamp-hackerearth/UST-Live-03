package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Stock;
import com.ust.pos.modell.StockRepository;
import com.ust.pos.stock.service.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StockServiceImpl extends BaseService implements StockService {

    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifierAndDeletedFalse(identifier.trim());

        if (stock == null) {
            throw new IllegalArgumentException("Stock not found");
        }
        return mapToDto(stock);
    }

    @Override
    public StockDto findById(Long id) {
        Stock stock = stockRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new RuntimeException("Stock not found"));
        return mapToDto(stock);
    }

    @Override
    public StockDto save(StockDto stockDto) {

        if (stockDto.getProductIdentifier() == null || stockDto.getWarehouseIdentifier() == null) {
            throw new IllegalArgumentException("Product & Warehouse required");
        }
        String identifier = "STK-" + stockDto.getProductIdentifier() + "-" + stockDto.getWarehouseIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);

        if (existingStock != null) {
            if (Boolean.TRUE.equals(existingStock.getDeleted())) {
                stockDto.setMessage("Stock record with Identifier " + identifier + " already exists (Soft-Deleted)");
                stockDto.setSuccess(false);
                return stockDto;
            }
            stockDto.setMessage("Stock record already exists with identifier: " + identifier);
            stockDto.setSuccess(false);
            return stockDto;
        }

        Stock stock = new Stock();
        stock.setIdentifier(identifier);
        stock.setProductIdentifier(stockDto.getProductIdentifier());
        stock.setWarehouseIdentifier(stockDto.getWarehouseIdentifier());
        stock.setQuantity(stockDto.getQuantity());
        stock.setMinimumStock(stockDto.getMinimumStock());
        stock.setStatus(stockDto.getQuantity() > stockDto.getMinimumStock());
        setCreatedDetails(stock);
        return mapToDto(stockRepository.save(stock));
    }

    @Override
    public StockDto update(StockDto stockDto) {
        Stock stock = stockRepository.findByIdAndDeletedFalse(stockDto.getId()).orElseThrow(() -> new RuntimeException("Stock not found"));
        String originalCreatedBy = stock.getCreatedBy();
        java.time.LocalDateTime originalCreatedOn = stock.getCreatedOn();
        stock.setProductIdentifier(stockDto.getProductIdentifier());
        stock.setWarehouseIdentifier(stockDto.getWarehouseIdentifier());
        String identifier = "STK-" + stockDto.getProductIdentifier() + "-" + stockDto.getWarehouseIdentifier();
        stock.setIdentifier(identifier);
        stock.setQuantity(stockDto.getQuantity());
        stock.setMinimumStock(stockDto.getMinimumStock());
        stock.setStatus(stockDto.getQuantity() > stockDto.getMinimumStock());
        stock.setCreatedBy(originalCreatedBy);
        stock.setCreatedOn(originalCreatedOn);
        setModifiedDetails(stock);

        return mapToDto(stockRepository.save(stock));
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifierAndDeletedFalse(identifier);
        if (stock != null) {
            softDelete(stock);
            setModifiedDetails(stock);
            stockRepository.save(stock);
        }
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAllByDeletedFalse(pageable);
        List<StockDto> dtoList = modelMapper.map(stockPage.getContent(), listType);
        for (int i = 0; i < dtoList.size(); i++) {
            Stock stock = stockPage.getContent().get(i);
            StockDto dto = dtoList.get(i);
            if (stock.getQuantity() <= 0) {
                dto.setStatusLabel("OUT_OF_STOCK");
            } else if (stock.getQuantity() <= stock.getMinimumStock()) {
                dto.setStatusLabel("LOW_STOCK");
            } else {
                dto.setStatusLabel("IN_STOCK");
            }
        }
        WsDto<StockDto> stockWsDto = new WsDto<>();
        stockWsDto.setDtoList(dtoList);
        return stockWsDto;
    }

    private StockDto mapToDto(Stock stock) {
        StockDto dto = new StockDto();
        dto.setId(stock.getId());
        dto.setIdentifier(stock.getIdentifier());
        dto.setQuantity(stock.getQuantity());
        dto.setMinimumStock(stock.getMinimumStock());
        dto.setProductIdentifier(stock.getProductIdentifier());
        dto.setWarehouseIdentifier(stock.getWarehouseIdentifier());

        if (stock.getQuantity() <= 0) {
            dto.setStatusLabel("OUT_OF_STOCK");
        } else if (stock.getQuantity() <= stock.getMinimumStock()) {
            dto.setStatusLabel("LOW_STOCK");
        } else {
            dto.setStatusLabel("IN_STOCK");
        }
        dto.setSuccess(true);
        return dto;
    }

}