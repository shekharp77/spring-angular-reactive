package com.clairvoyant.stcokbackend.controller;

import com.clairvoyant.stcokbackend.model.Stock;
import com.clairvoyant.stcokbackend.repository.StockRepository;
import java.time.Duration;
import com.clairvoyant.stcokbackend.service.VirtualStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@EnableScheduling
public class StocksController {

  private final StockRepository stockRepository;
  private final VirtualStreamService virtualStreamService;


  @GetMapping
  public Flux<Stock> getAll() {
    return virtualStreamService.getVirtualStream();
  }

  @GetMapping("{id}")
  public Mono<ResponseEntity<Stock>> getById(@PathVariable String name) {
    return stockRepository
        .findByStockName(name)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping(value = "/ticker-price/stream/{name}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<Stock> streamTickerPrice(@PathVariable String name) {
    return Flux.interval(Duration.ofSeconds(2))
            .map(pulse -> {
              return virtualStreamService.getStockByName(name);
            });
  }
}
