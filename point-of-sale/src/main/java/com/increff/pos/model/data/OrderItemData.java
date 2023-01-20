package com.increff.pos.model.data;

import com.increff.pos.model.form.OrderItemForm;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class OrderItemData extends OrderItemForm {

    private Integer id;
    private String productName;
    private Integer orderId;
}
