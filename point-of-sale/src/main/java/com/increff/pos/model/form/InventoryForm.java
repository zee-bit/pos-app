package com.increff.pos.model.form;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class InventoryForm {

    @CsvBindByName(column = "barcode")
    private String barcode;

    @CsvBindByName(column = "quantity")
    private Integer quantity;

}
