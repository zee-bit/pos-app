package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "brand_categories",
indexes = {
        @Index(name = "brand_category_idx", columnList = "brand,category", unique = true)
})
public class BrandPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private String category;
}
