package com.increff.pos.model.form;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BrandForm {

    @CsvBindByName(column = "brand")
    private String brand;

    @CsvBindByName(column = "category")
    private String category;

}
