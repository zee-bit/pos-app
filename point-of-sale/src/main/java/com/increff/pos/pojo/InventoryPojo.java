package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "inventories")
public class InventoryPojo {

    @Id
    @Column(name = "product_id")
    private Integer productId;
    @Column(nullable = false)
    private Integer quantity;
}
