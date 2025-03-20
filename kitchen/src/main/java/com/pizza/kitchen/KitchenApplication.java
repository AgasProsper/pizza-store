package com.pizza.kitchen;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RestController
@RequestMapping("/kitchen")
public class KitchenApplication implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Counter orderCounter;
    private final Timer orderProcessingTimer;

    @Autowired
    public KitchenApplication(MeterRegistry meterRegistry) {
        this.orderCounter = meterRegistry.counter("kitchen.orders.processed");
        this.orderProcessingTimer = meterRegistry.timer("kitchen.order.processing.time");
    }

    public static void main(String[] args) {
        SpringApplication.run(KitchenApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        jdbcTemplate.execute("SELECT count(id) from pizza_order");
        System.out.println("Database connection pool initialized.");
    }

    @GetMapping("/orders")
    public String getOrders() {
        return "List of orders";
    }

    @PostMapping("/orders/process")
    public String processOrder() {
        long start = System.nanoTime();
        
        // Simulating order processing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        orderProcessingTimer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        orderCounter.increment();
        
        return "Order processed";
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "Kitchen Service is running!";
    }

}

