package com.increff.pos.model.data;

import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.data.OrderItemData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter
public class OrderData {

    private Integer id;
    private Timestamp createdAt;
    private Double billAmount;
    private Boolean isInvoiceCreated;
}
