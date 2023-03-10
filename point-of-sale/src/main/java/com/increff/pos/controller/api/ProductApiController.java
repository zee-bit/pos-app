package com.increff.pos.controller.api;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.form.ProductSearchForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    @Autowired
    private ProductDto dto;

    @ApiOperation(value = "Adds a new product")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public ProductData add(@RequestBody ProductForm form) throws ApiException {
        return dto.add(form);
    }

    @ApiOperation(value = "Gets a product by Id")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ProductData getById(@PathVariable Integer id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Gets a product by barcode")
    @RequestMapping(path = "/barcode/{barcode}", method = RequestMethod.GET)
    public ProductData getByBarcode(@PathVariable String barcode) throws ApiException {
        return dto.getByBarcode(barcode);
    }

    @ApiOperation(value = "Gets list of all products")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<ProductData> getAll() throws ApiException {
        return dto.getAll();
    }

    @ApiOperation(value = "Get list of filtered products")
    @RequestMapping(path = "/search", method = RequestMethod.POST)
    public List<ProductData> searchProduct(@RequestBody ProductSearchForm form) throws ApiException {
        return dto.searchProduct(form);
    }

    @ApiOperation(value = "Updates a product")
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ProductData update(@PathVariable Integer id, @RequestBody ProductForm f) throws ApiException {
        return dto.update(id, f);
    }
}
