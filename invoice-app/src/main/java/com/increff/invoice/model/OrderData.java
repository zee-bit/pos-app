package com.increff.invoice.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
public class OrderData {

    private Integer id;
    private Timestamp createdAt;
    private Double billAmount;
}
