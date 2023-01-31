package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventorySearchForm {
    private String product;
    private String barcode;
}
