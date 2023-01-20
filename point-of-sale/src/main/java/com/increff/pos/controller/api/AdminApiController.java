package com.increff.pos.controller.api;

import com.increff.pos.dto.AdminApiDto;
import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.UserData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    @Autowired
    private AdminApiDto dto;

    @ApiOperation(value = "Adds a new user")
    @RequestMapping(path = "/users", method = RequestMethod.POST)
    public void add(@RequestBody UserForm form) throws ApiException {
        dto.add(form);
    }

    @ApiOperation(value = "Gets list of all users")
    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<UserData> getAll() {
        return dto.getAll();
    }
}
