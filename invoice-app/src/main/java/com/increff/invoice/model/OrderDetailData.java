package com.increff.invoice.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDetailData extends OrderData {

    List<OrderItemData> orderItems;
}
