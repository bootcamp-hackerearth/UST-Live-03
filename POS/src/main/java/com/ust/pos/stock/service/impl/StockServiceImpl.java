package com.ust.pos.stock.service.impl;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Stock;
import com.ust.pos.modell.StockRepository;
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

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier.trim());

        if (stock == null) {
            throw new IllegalArgumentException("Stock not found");
        }
        return mapToDto(stock);
    }

    @Override
    public StockDto findById(Long id) {
        Stock stock = stockRepository.findById(id).orElseThrow(() -> new RuntimeException("Stock not found"));
        return mapToDto(stock);
    }

    @Override
    public StockDto save(StockDto stockDto) {
        if (stockDto.getProductIdentifier() == null ||
                stockDto.getWarehouseIdentifier() == null) {

            throw new IllegalArgumentException("Product & Warehouse required");
        }
        Stock stock = new Stock();
        String identifier = "STK-" + stockDto.getProductIdentifier() + "-" + stockDto.getWarehouseIdentifier();
        stock.setIdentifier(identifier);
        stock.setProductIdentifier(stockDto.getProductIdentifier());
        stock.setWarehouseIdentifier(stockDto.getWarehouseIdentifier());
        stock.setQuantity(stockDto.getQuantity());
        stock.setMinimumStock(stockDto.getMinimumStock());
        stock.setStatus(
                stockDto.getQuantity() > stockDto.getMinimumStock()
        );
        return mapToDto(stockRepository.save(stock));
    }

    @Override
    public StockDto update(StockDto stockDto) {
        Stock stock = stockRepository.findById(stockDto.getId()).orElseThrow(() -> new RuntimeException("Stock not found"));
        stock.setProductIdentifier(stockDto.getProductIdentifier());
        stock.setWarehouseIdentifier(stockDto.getWarehouseIdentifier());
        String identifier = "STK-" + stockDto.getProductIdentifier() + "-" + stockDto.getWarehouseIdentifier();
        stock.setIdentifier(identifier);
        stock.setQuantity(stockDto.getQuantity());
        stock.setMinimumStock(stockDto.getMinimumStock());
        stock.setStatus(stockDto.getQuantity() > stockDto.getMinimumStock()
        );
        return mapToDto(stockRepository.save(stock));
    }

    @Override
    public void delete(Long id) {
        stockRepository.deleteById(id);
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAll(pageable);

        WsDto<StockDto> stockWsDto = new WsDto<>();
        stockWsDto.setDtoList(modelMapper.map(stockPage.getContent(), listType));
        stockWsDto.setTotalRecords(stockPage.getTotalElements());
        stockWsDto.setTotalPage(stockPage.getTotalPages());
        stockWsDto.setSizePerPage(pageable.getPageSize());
        stockWsDto.setPage(pageable.getPageNumber());

        return stockWsDto;
    }

    private StockDto mapToDto(Stock stock) {
        StockDto dto = new StockDto();
        dto.setId(stock.getId());
        dto.setIdentifier(stock.getIdentifier());
        dto.setQuantity(stock.getQuantity());
        dto.setMinimumStock(stock.getMinimumStock());
        dto.setStatus(stock.getStatus());
        dto.setProductIdentifier(stock.getProductIdentifier());
        dto.setWarehouseIdentifier(stock.getWarehouseIdentifier());
        return dto;
    }
}