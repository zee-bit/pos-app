package com.increff.pos.model.form;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {

    @CsvBindByName(column = "product")
    private String product;

    @CsvBindByName(column = "barcode")
    private String barcode;

    @CsvBindByName(column = "brand")
    private String brandName;

    @CsvBindByName(column = "category")
    private String brandCategory;

    @CsvBindByName(column = "price")
    private Double price;
    
}
