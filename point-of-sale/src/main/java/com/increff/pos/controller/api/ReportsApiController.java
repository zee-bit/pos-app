package com.increff.pos.controller.api;

import com.increff.pos.dto.ReportDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.DailySalesReportData;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.SalesReportData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Api
@RestController
@RequestMapping("/api/reports")
public class ReportsApiController {

    @Autowired
    private ReportDto dto;

    @ApiOperation(value = "Gets inventory report for all brand-categories")
    @RequestMapping(value = "/inventory", method = RequestMethod.POST)
    public List<InventoryReportData> inventoryReport(@RequestBody BrandForm brandForm) throws ApiException {
        return dto.getInventoryReport(brandForm);
    }

    @ApiOperation(value = "Gets brand-category report")
    @RequestMapping(value = "/brand", method = RequestMethod.POST)
    public List<BrandData> brandCategoryReport(@RequestBody BrandForm brandForm) throws ApiException {
        return dto.getBrandCategoryReport(brandForm);
    }

    @ApiOperation(value = "Gets category-wise sales report for interval")
    @RequestMapping(value = "/sales", method = RequestMethod.POST)
    public List<SalesReportData> getSalesReport(@RequestBody SalesReportForm salesReportForm) throws ApiException {
        return dto.getSalesReport(salesReportForm);
    }

    @ApiOperation(value = "Get daily sales report")
    @RequestMapping(value = "/daily-sales", method = RequestMethod.GET)
    public List<DailySalesReportData> getDailySalesReport() {
        return dto.getDailySalesReport();
    }
}
