package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "products",
indexes = {
        @Index(name = "barcode_idx", columnList = "barcode", unique = true)
})
public class ProductPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String barcode;
    @Column(nullable = false)
    private String product;
    @Column(name = "brand_id", nullable = false)
    private Integer brandId;
    @Column(nullable = false)
    private Double price;
}
