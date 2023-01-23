package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "order_items",
indexes = {
        @Index(name = "order_id_idx", columnList = "order_id")
})
public class OrderItemPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;
    @Column(name = "product_id", nullable = false)
    private Integer productId;
    @Column(nullable = false)
    private Integer quantity;
    @Column(name = "selling_price", nullable = false)
    private Double sellingPrice;
}
