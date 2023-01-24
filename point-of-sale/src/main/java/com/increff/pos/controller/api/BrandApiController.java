package com.increff.pos.controller.api;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/brands")
public class BrandApiController {

    @Autowired
    private BrandDto dto;

    @ApiOperation(value = "Adds a new brand")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public BrandData add(@RequestBody BrandForm form) throws ApiException {
        return dto.add(form);
    }

    @ApiOperation(value = "Gets a brand by Id")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public BrandData get(@PathVariable Integer id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Gets list of all brands")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<BrandData> getAll() {
        return dto.getAll();
    }

    @ApiOperation(value = "Get list of filtered brands")
    @RequestMapping(path = "/search", method = RequestMethod.POST)
    public List<BrandData> getFiltered(@RequestBody BrandForm form) {
        return dto.getFilteredBrandCategory(form);
    }

    @ApiOperation(value = "Updates a brand")
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public BrandData update(@PathVariable Integer id, @RequestBody BrandForm form) throws ApiException {
        return dto.update(id, form);
    }
}
