package com.increff.pos.model.data;

import com.increff.pos.pojo.BrandPojo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesReportData {
    private String brand;
    private String category;
    private Integer quantity;
    private Double revenue;
    private Integer brandCategoryId;

    public SalesReportData(BrandPojo brandCategory, Integer quantity, Double revenue) {
        this.brand = brandCategory.getBrand();
        this.category = brandCategory.getCategory();
        this.brandCategoryId = brandCategory.getId();
        this.quantity = quantity;
        this.revenue = revenue;
    }
}
