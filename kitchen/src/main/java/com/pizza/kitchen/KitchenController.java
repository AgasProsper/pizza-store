package com.pizza.kitchen;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@RestController
@RequestMapping("/kitchen")
public class KitchenController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = Logger.getLogger(KitchenController.class.getName());

    private final Counter orderCounter;
    private final Timer dbQueryTimer;

    @Autowired
    public KitchenController(MeterRegistry meterRegistry) {
        this.orderCounter = meterRegistry.counter("kitchen.orders.processed");
        this.dbQueryTimer = meterRegistry.timer("kitchen.db.query.time");
    }

    @PostMapping("/order")
    public ResponseEntity<Object> addNewOrder(
            @RequestParam("id") int id,
            @RequestParam("location") String location) {

        long startTime = System.nanoTime();
        Timestamp orderTime = Timestamp.from(Instant.now());

        jdbcTemplate.update("INSERT INTO pizza_order values(?, 'Ordered', ?, ?)",
                id, location, orderTime);

        dbQueryTimer.record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        orderCounter.increment();

        logger.info("New order added: ID=" + id + ", Location=" + location);

        return ResponseEntity.ok(generateResponse(orderTime, "Order placed successfully"));
    }

    @PutMapping("/order")
    public ResponseEntity<Object> updateOrder(
            @RequestParam("id") int id,
            @RequestParam("status") String status,
            @RequestParam(name = "location", required = false) String location) {

        long startTime = System.nanoTime();
        int updatedRows;

        if (Objects.nonNull(location)) {
            updatedRows = jdbcTemplate.update("UPDATE pizza_order SET status = ? WHERE id = ? AND location = ?",
                    status, id, location);
        } else {
            updatedRows = jdbcTemplate.update("UPDATE pizza_order SET status = ? WHERE id = ?", status, id);
        }

        dbQueryTimer.record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);

        if (updatedRows == 0) {
            logger.warning("Order not found: ID=" + id);
            return new ResponseEntity<>(generateResponse(null, "Order not found"), HttpStatus.NOT_FOUND);
        }

        logger.info("Order updated: ID=" + id + ", Status=" + status);
        return ResponseEntity.ok(generateResponse(null, "Order updated successfully"));
    }

    @GetMapping("/order")
    public ResponseEntity<Object> getOrder(
            @RequestParam("id") int id,
            @RequestParam(name = "location", required = false) String location) {

        long startTime = System.nanoTime();
        List<Order> orders;

        if (Objects.nonNull(location)) {
            orders = jdbcTemplate.query("SELECT * FROM pizza_order WHERE id = ? AND location = ?",
                    new OrderRowMapper(), id, location);
        } else {
            orders = jdbcTemplate.query("SELECT * FROM pizza_order WHERE id = ?", new OrderRowMapper(), id);
        }

        dbQueryTimer.record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);

        if (orders.isEmpty()) {
            logger.warning("Order not found: ID=" + id);
            return new ResponseEntity<>(generateResponse(null, "Order not found"), HttpStatus.NOT_FOUND);
        }

        logger.info("Order retrieved: ID=" + id);
        return ResponseEntity.ok(generateResponse(orders.get(0), "Order retrieved successfully"));
    }

    @DeleteMapping("/orders")
    public ResponseEntity<Object> deleteOrders() {
        long startTime = System.nanoTime();
        jdbcTemplate.execute("TRUNCATE pizza_order");
        dbQueryTimer.record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);

        logger.info("All orders deleted");
        return ResponseEntity.ok(generateResponse(null, "All orders deleted"));
    }

    private static Map<String, Object> generateResponse(Object data, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("data", data);
        return result;
    }
}

