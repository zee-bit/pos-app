package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchForm {
    private String name;
    private String barcode;
    private String brand;
    private String category;
}
