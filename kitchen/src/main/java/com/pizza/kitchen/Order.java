package com.pizza.kitchen;

import java.sql.Timestamp;

public record Order(int id, String status, String location, Timestamp orderTime) {

}
