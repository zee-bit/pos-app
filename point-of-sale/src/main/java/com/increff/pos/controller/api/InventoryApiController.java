package com.increff.pos.controller.api;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.InventorySearchForm;
import com.increff.pos.model.form.ProductSearchForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/inventory")
public class InventoryApiController {

    @Autowired
    InventoryDto dto;

    @ApiOperation(value = "Gets an inventory by barcode")
    @RequestMapping(path = "/{barcode}", method = RequestMethod.GET)
    public InventoryData getByBarcode(@PathVariable String barcode) throws ApiException {
        return dto.get(barcode);
    }

    @ApiOperation(value = "Gets list of all products in inventory")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<InventoryData> getAll() throws ApiException {
        return dto.getAll();
    }

    @ApiOperation(value = "Get list of filtered inventory items")
    @RequestMapping(path = "/search", method = RequestMethod.POST)
    public List<InventoryData> searchInventory(@RequestBody InventorySearchForm form) throws ApiException {
        return dto.searchInventory(form);
    }

    @ApiOperation(value = "Updates an inventory")
    @RequestMapping(path = "", method = RequestMethod.PUT)
    public InventoryData update(@RequestBody InventoryForm f) throws ApiException {
        return dto.update(f);
    }
}
